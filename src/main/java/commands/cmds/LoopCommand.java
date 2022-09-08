package commands.cmds;

import commands.logic.ICommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class LoopCommand implements ICommand {
    private static HashMap<Long, Boolean> guildAloop = new HashMap<>();
    public static boolean isLooping(long guildid) {
        return guildAloop.getOrDefault(guildid, false);
    }

    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        boolean isLooped = isLooping(Objects.requireNonNull(event.getGuild()).getIdLong());
        isLooped=!isLooped;
        guildAloop.put(event.getGuild().getIdLong(), isLooped);
        String eOd = "";
        if (isLooped) eOd="aktiviert";
        else eOd="deaktiviert";
        event.getHook().sendMessage("Die Schleife wurde "+eOd).queue();
    }

    @Override
    public CommandData data() {
        return new CommandData("loop", "Setzt den Song der gerade gespielt wird in Schleife");
    }

    @Override
    public Collection<OptionData> options() {
        return new ArrayList<>();
    }

    @Override
    public Collection<CommandPrivilege> privileges() {
        return new ArrayList<>();
    }
}
