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
    public static HashMap<Long, Boolean> guildAloop = new HashMap<>();

    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        boolean isLooped = guildAloop.getOrDefault(Objects.requireNonNull(event.getGuild()).getIdLong(), false);
        isLooped=!isLooped;
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
