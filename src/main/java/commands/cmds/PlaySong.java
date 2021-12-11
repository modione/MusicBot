package commands.cmds;

import com.google.api.services.youtube.model.SearchResult;
import commands.logic.ICommand;
import logic.AudioUtils;
import logic.YoutubeAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlaySong implements ICommand {
    @Override
    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        String link = data.get(0).getAsString();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("**Spiele Song ab**");
        ArrayList<ActionRow> rows = new ArrayList<>();
        try {
            List<SearchResult> items = YoutubeAPI.getVideosAsItem(link);
            ArrayList<Button> buttons = new ArrayList<>();
            try {
                int responseCode = ((HttpURLConnection) (new URL(link)).openConnection()).getResponseCode();
                if (responseCode == 200) {
                    AudioUtils.loadAndPlay(hook, link, event.getMember());
                    return;
                }
            } catch (IOException ignored) {
            }
            if (items.size() > 1) {
                for (SearchResult s : items) {
                    if (s.getSnippet().getTitle().length() >= 80)
                        buttons.add(Button.success("play@" + s.getId().getVideoId(), s.getSnippet().getTitle().replace("&#39", "'").substring(0, 80)));
                    else
                        buttons.add(Button.success("play@" + s.getId().getVideoId(), s.getSnippet().getTitle().replace("&#39", "'")));
                }
                double size = buttons.size();
                double count = Math.ceil((size / 5.0));
                for (int i = 0; i < count; i++) {
                    if (i + 1 >= count) rows.add(ActionRow.of(buttons.subList(5 * i, buttons.size())));
                    else rows.add(ActionRow.of(buttons.subList(5 * i, 5 * (i + 1))));
                }
                embedBuilder.setDescription("Wähle den Song aus den du meinst.");
            }
        } catch (Exception e) {
            embedBuilder.setDescription("**Fehler**\n" + e.getMessage());
        }
        hook.sendMessageEmbeds(embedBuilder.build()).addActionRows(rows).queue();
    }

    @Override
    public CommandData data() {
        return new CommandData("play", "Spielt einen Song ab").addOptions(options());
    }

    @Override
    public Collection<OptionData> options() {
        ArrayList<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "name", "Youtube Video Name oder Link").setRequired(true));
        return data;
    }

    @Override
    public Collection<CommandPrivilege> privileges() {
        return new ArrayList<>();
    }
}
