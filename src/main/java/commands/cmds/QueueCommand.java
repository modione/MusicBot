package commands.cmds;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.logic.ICommand;
import logic.AudioUtils;
import music.GuildMusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.awt.*;
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
        AudioTrack playingTrack = player.player.getPlayingTrack();
        if (playingTrack==null) {
            builder.setDescription("Es wird kein Song gespielt!");
            builder.setColor(Color.RED);
            hook.sendMessageEmbeds(builder.build()).queue();
            return;
        }
        builder.addField("Spielt gerade", playingTrack.getInfo().title, false);
        for (AudioTrack track : player.scheduler.queue) {
            builder.addField("Track "+track.getPosition()+1, track.getInfo().title, false);
        }
        builder.addField("Schleife", LoopCommand.isLooping(event.getGuild().getIdLong()) ? "An":"Aus", false);
        hook.sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public CommandData data() {
        return new CommandData("queue", "Zeigt dir die Warteschlange.");
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
