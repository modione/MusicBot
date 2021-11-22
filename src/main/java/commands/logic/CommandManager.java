package commands.logic;

import commands.cmds.*;
import logic.main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandManager extends ListenerAdapter {
    public static CommandManager INSTANCE;
    public ArrayList<ICommand> commands = new ArrayList<>();
    public CommandListUpdateAction commandList = main.INSTANCE.jda.updateCommands();
    public CommandManager() {
        INSTANCE=this;
        ModiMenuCommand modiMenu = new ModiMenuCommand();
        PlaySong play = new PlaySong();
        SkipSong skip = new SkipSong();
        PauseSong song = new PauseSong();
        Rejoin rejoin = new Rejoin();
        LoopCommand loop = new LoopCommand();
        commands.add(play);
        commands.add(skip);
        commands.add(song);
        commands.add(modiMenu);
        commands.add(rejoin);
        commands.add(loop);
        commandList.addCommands(play.data(), skip.data(), song.data(), modiMenu.data(), rejoin.data(), loop.data()).queue();
        commandList.queue();
        for (Guild guild : main.INSTANCE.jda.getGuilds()) {
            Map<String, Collection<? extends CommandPrivilege>> pre = new HashMap<>();
            guild.updateCommands().queue();
            commands.forEach(command -> {
                if (command.privileges().isEmpty()) return;
                main.INSTANCE.jda.retrieveCommands().complete().forEach(command1 -> {
                    if (!command1.getName().equals(command.data().getName())) return;
                    pre.put(command1.getId(), command.privileges());
                });
            });
            guild.updateCommandPrivileges(pre).complete();
        }
        main.INSTANCE.jda.addEventListener(modiMenu);
    }
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null) return;
        for (ICommand command : commands) {
            if (!event.getName().equals(command.data().getName())) continue;
            ArrayList<OptionMapping> options = new ArrayList<>();
            if (!command.options().isEmpty()) for (OptionData data : command.options()) options.add(event.getOption(data.getName()));
            new Thread(() -> command.on_command(event, options)).start();
        }
    }
}
