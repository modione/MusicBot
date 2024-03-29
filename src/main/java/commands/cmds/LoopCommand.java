package commands.cmds;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.logic.ICommand;
import logic.AudioUtils;
import music.GuildMusicPlayer;
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
    private static final HashMap<Long, Boolean> guildAloop = new HashMap<>();
    public static boolean isLooping(long guildid) {
        return guildAloop.getOrDefault(guildid, false);
    }
    public static boolean switchLooping(long guildid) {
        boolean isLooped = isLooping(guildid);
        isLooped=!isLooped;
        guildAloop.put(guildid, isLooped);
        return isLooped;
    }

    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        boolean isLooped = switchLooping(Objects.requireNonNull(event.getGuild()).getIdLong());
        String eOd;
        if (isLooped) {
            AudioUtils.getGuildAudioPlayer(event.getGuild()).scheduler.queue.clear();
            eOd="aktiviert";
        }else eOd="deaktiviert";
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
