package logic;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.GuildMusicPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class AudioUtils {

    public static synchronized GuildMusicPlayer getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicPlayer musicManager = main.INSTANCE.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicPlayer(main.INSTANCE.playerManager);
            main.INSTANCE.musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public static void loadAndPlay(final InteractionHook hook, final String trackUrl, Member member) {
        GuildMusicPlayer musicManager = getGuildAudioPlayer(Objects.requireNonNull(hook.getInteraction().getGuild()));

        main.INSTANCE.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                hook.sendMessage("**" + track.getInfo().title + "** wurde zu der Playlist hinzugefügt").queue();
                play(hook.getInteraction().getGuild(), musicManager, track, member);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                }
                hook.sendMessage("---").queue();
            }

            @Override
            public void noMatches() {
                hook.sendMessage(trackUrl + " wurde nicht gefunden").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                hook.sendMessage("Konnt den Song nicht spielen: "+exception.getMessage()).queue();
            }
        });
    }

    public static void play(Guild guild, GuildMusicPlayer musicManager, AudioTrack track, Member member) {
        connectToFirstVoiceChannel(guild.getAudioManager(), member);
        musicManager.scheduler.queue(track);
    }

    public static void skipTrack(InteractionHook channel) {
        GuildMusicPlayer musicManager = getGuildAudioPlayer(Objects.requireNonNull(channel.getInteraction().getGuild()));
        AudioTrack playingTrack = musicManager.player.getPlayingTrack();
        int size = musicManager.scheduler.queue.size();
        if (size==0) {
            channel.sendMessage("Stoppe das lied").queue();
            musicManager.player.stopTrack();
            return;
        }
        musicManager.scheduler.nextTrack();
        AudioTrack playingTrack1 = musicManager.player.getPlayingTrack();
        channel.sendMessage("Überspringe **" + playingTrack.getInfo().title + "**------->**" + playingTrack1.getInfo().title + "**").queue();
    }
    public static void pauseTrack(InteractionHook hook) {
        GuildMusicPlayer player = getGuildAudioPlayer(Objects.requireNonNull(hook.getInteraction().getGuild()));
        player.player.setPaused(!player.player.isPaused());
        hook.sendMessage("**"+player.player.getPlayingTrack().getInfo().title+"** paussiert!").queue();
    }
    public static void connectToFirstVoiceChannel(AudioManager audioManager, Member member) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                if (voiceChannel.getMembers().contains(member)) {
                    audioManager.openAudioConnection(voiceChannel);
                    return;
                }
            }
            audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannels().get(0));
        }

    }
}
