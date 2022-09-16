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
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
        try {
            List<SearchResult> items = YoutubeAPI.getVideosAsItem(link);
            try {
                int responseCode = ((HttpURLConnection) (new URL(link)).openConnection()).getResponseCode();
                if (responseCode == 200) {
                    AudioUtils.loadAndPlay(hook, link, event.getMember());
                    return;
                }
            } catch (IOException ignored) {}
            if (items.size() > 1) {
                ArrayList<SelectOption> options = new ArrayList<>();
                for (SearchResult item : items) {
                    String title = StringEscapeUtils.unescapeXml(item.getSnippet().getTitle());
                    String id = item.getId().getVideoId();
                    if (title.length() >= 100) {
                        options.add(SelectOption.of(title.substring(0, 100), id));
                    }
                    options.add(SelectOption.of(title, id));
                }
                ArrayList<SelectOption> options1 = new ArrayList<>();
                for (SelectOption option : options) {
                    options1.removeIf(selectOption -> option.getValue().equals(selectOption.getValue()));
                    options1.add(option);
                }

                embedBuilder.setDescription("Wähle den Song aus den du meinst.");
                hook.sendMessageEmbeds(embedBuilder.build()).addActionRow(SelectionMenu.create("play:menu")
                        .setPlaceholder("Wähle deinen Song aus")
                        .addOptions(options1)
                        .setRequiredRange(1, 1)
                        .build()).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            embedBuilder.addField("Fehler",e.getClass().getName(), false);
            hook.sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
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
