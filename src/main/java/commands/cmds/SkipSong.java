package commands.cmds;

import commands.logic.ICommand;
import logic.AudioUtils;
import music.GuildMusicPlayer;
import music.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class SkipSong implements ICommand {
    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        GuildMusicPlayer musicPlayer = AudioUtils.getGuildAudioPlayer(Objects.requireNonNull(event.getGuild()));
        if (musicPlayer.player.getPlayingTrack() == null) {
            hook.sendMessage("Es wird gerade kein Song gespielt").queue();
            return;
        }
        AudioUtils.skipTrack(hook);
    }

    @Override
    public CommandData data() {
        return new CommandData("skip", "Überspringt den Gerade spielenden Song");
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
