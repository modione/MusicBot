package commands.cmds;

import commands.logic.ICommand;
import logic.main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Rejoin implements ICommand {
    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        if (Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getMemberById(main.INSTANCE.jda.getSelfUser().getId())).getVoiceState()).inVoiceChannel()) {
            rejoin(event.getGuild());
            hook.sendMessage("Erfolgreich rejoined!").queue();
        }else {
            hook.sendMessage("ich bin in keinem voicechannel :(").queue();
        }
    }

    @Override
    public CommandData data() {
        return new CommandData("rejoin", "Lässt den Bot rejoinen lul w");
    }

    @Override
    public Collection<OptionData> options() {
        return new ArrayList<>();
    }

    @Override
    public Collection<CommandPrivilege> privileges() {
        return new ArrayList<>();
    }
    public static void rejoin(Guild guild) {
        try {

            Member member = guild.getMemberById(main.INSTANCE.jda.getSelfUser().getId());
            assert member != null;
            if (!Objects.requireNonNull(member.getVoiceState()).inVoiceChannel()) return;
            VoiceChannel channel = member.getVoiceState().getChannel();
            guild.kickVoiceMember(member).queue();
        }catch (Exception ignored) {}
    }
}
