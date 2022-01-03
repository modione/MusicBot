package commands.cmds;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.logic.ICommand;
import logic.AudioUtils;
import logic.main;
import music.GuildMusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class QueueCommand implements ICommand {
    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Queue");
        GuildMusicPlayer player = AudioUtils.getGuildAudioPlayer(Objects.requireNonNull(event.getGuild()));
        builder.appendDescription("╔Liste der Songs in der Playlist");
        for (AudioTrack track : player.scheduler.queue) {
            builder.appendDescription("╠"+track.getIdentifier());
        }
    }

    @Override
    public CommandData data() {
        return null;
    }

    @Override
    public Collection<OptionData> options() {
        return null;
    }

    @Override
    public Collection<CommandPrivilege> privileges() {
        return null;
    }
}
