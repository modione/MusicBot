package commands.logic;

import commands.cmds.LoopCommand;
import commands.cmds.PauseSong;
import commands.cmds.PlaySong;
import commands.cmds.SkipSong;
import logic.main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandManager extends ListenerAdapter {
    public static CommandManager INSTANCE;
    public ArrayList<ICommand> commands = new ArrayList<>();
    public CommandListUpdateAction commandList = main.INSTANCE.jda.updateCommands();

    public CommandManager() {
        INSTANCE = this;
        addCmd(new PlaySong(), new SkipSong(), new PauseSong(), new LoopCommand());
        commandList.queue();
        for (Guild guild : main.INSTANCE.jda.getGuilds()) {
            Map<String, Collection<? extends CommandPrivilege>> pre = new HashMap<>();
            guild.updateCommands().queue();
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null) return;
        for (ICommand command : commands) {
            if (!event.getName().equals(command.data().getName())) continue;
            ArrayList<OptionMapping> options = new ArrayList<>();
            if (!command.options().isEmpty())
                for (OptionData data : command.options()) options.add(event.getOption(data.getName()));
            Thread thread = new Thread(() -> command.on_command(event, options));
            thread.start();
        }
    }

    public void addCmd(ICommand... command) {
        Collection<CommandData> cmds = new ArrayList<>();
        for (ICommand iCommand : command) {
            cmds.add(iCommand.data());
        }
        commands.addAll(Arrays.asList(command));
        commandList.addCommands(cmds).queue();
    }
}
