package logic;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.cmds.LoopCommand;
import music.GuildMusicPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AudioUtils {
    static {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                main.INSTANCE.musicManagers.forEach((aLong, guildMusicPlayer) -> {
                    AudioManager audioManager = Objects.requireNonNull(main.INSTANCE.jda.getGuildById(aLong)).getAudioManager();
                    if (guildMusicPlayer.player.getPlayingTrack()==null || Objects.requireNonNull(audioManager.getConnectedChannel()).getMembers().size()==1) {
                        audioManager.closeAudioConnection();

                    }
                });
            }
        }, TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS));
    }

    public static synchronized GuildMusicPlayer getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicPlayer musicManager = main.INSTANCE.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicPlayer(main.INSTANCE.playerManager, guild);
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
                for (AudioTrack track : playlist.getTracks()) {
                    play(hook.getInteraction().getGuild(), musicManager, track, member);
                }
                hook.sendMessage("Playlist **"+playlist.getName()+"** hinzugefügt, erster Track ist **"+musicManager.player.getPlayingTrack().getInfo().title+"**").queue();
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
            long guildid = channel.getInteraction().getGuild().getIdLong();
            if (LoopCommand.isLooping(guildid)) {
                LoopCommand.switchLooping(guildid);
            }
            return;
        }
        musicManager.scheduler.nextTrack();
        AudioTrack playingTrack1 = musicManager.player.getPlayingTrack();
        channel.sendMessage("Überspringe **" + playingTrack.getInfo().title + "**------->**" + playingTrack1.getInfo().title + "**").queue();
    }
    public static void pauseTrack(InteractionHook hook) {
        GuildMusicPlayer player = getGuildAudioPlayer(Objects.requireNonNull(hook.getInteraction().getGuild()));
        player.player.setPaused(!player.player.isPaused());
        hook.sendMessage("**"+player.player.getPlayingTrack().getInfo().title+"** "+(player.player.isPaused()?"pausiert":"fortgesetzt")).queue();
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
