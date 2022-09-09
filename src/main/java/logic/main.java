package logic;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.cmds.LoopCommand;
import commands.logic.CommandManager;
import music.GuildMusicPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

public class main extends ListenerAdapter {
    public static main INSTANCE;
    public JDA jda;



    public static void main(String[] args) throws Exception {
        main main = new main();
        INSTANCE.jda = JDABuilder.create(System.getenv("TOKEN"), GUILD_MESSAGES, GUILD_VOICE_STATES)
                .build();
        INSTANCE.jda.awaitReady();
        INSTANCE.jda.addEventListener(main, new CommandManager(), new Logger());
    }

    public final AudioPlayerManager playerManager;
    public final Map<Long, GuildMusicPlayer> musicManagers;

    private main() {
        INSTANCE = this;
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        if (Objects.equals(Objects.requireNonNull(event.getComponent()).getId(), "play:menu")) {
            event.deferReply().queue();
            if (LoopCommand.isLooping(Objects.requireNonNull(event.getGuild()).getIdLong())) {
                event.getHook().sendMessage("Ne Kollege loop is an").queue();
            }else {
            AudioUtils.loadAndPlay(event.getHook(),
                    "youtube.com/watch?v="+ Objects.requireNonNull(event.getInteraction().getSelectedOptions()).get(0).getValue(), event.getMember());
            }
            event.getInteraction().editSelectionMenu(Objects.requireNonNull(event.getSelectionMenu()).asEnabled()).queue();
        }
    }
}
