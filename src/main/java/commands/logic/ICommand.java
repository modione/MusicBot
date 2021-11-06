package commands.logic;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;

public interface ICommand {
    void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data);
    CommandData data();
    Collection<OptionData> options();
    Collection<CommandPrivilege> privileges();
}
