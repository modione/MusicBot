package commands.logic;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;

public interface ICommand {
    void on_command(SlashCommandInteraction event, ArrayList<OptionMapping> data);
    CommandData data();
    Collection<OptionData> options();
    Collection<CommandPrivilege> privileges();
}
