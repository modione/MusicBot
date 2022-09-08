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
            } catch (IOException ignored) {}
            if (items.size() > 1) {
                ArrayList<SelectOption> options = new ArrayList<>();
                for (SearchResult item : items) {
                    if (item.getSnippet().getTitle().length() >= 100) {
                        options.add(SelectOption.of(item.getSnippet().getTitle().substring(0, 100), item.getId().getVideoId()));
                    }
                    options.add(SelectOption.of(item.getSnippet().getTitle(), item.getId().getVideoId()));
                }
                SelectionMenu menu = SelectionMenu.create("play:menu")
                        .setPlaceholder("Wähle deinen Song aus")
                        .addOptions(options)
                        .setRequiredRange(1, 1)
                        .build();

                embedBuilder.setDescription("Wähle den Song aus den du meinst.");
                hook.sendMessageEmbeds(embedBuilder.build()).addActionRow(menu).queue();
            }
        } catch (Exception e) {
            embedBuilder.addField("Fehler", e.getMessage(), true);
            hook.sendMessageEmbeds(embedBuilder.build()).queue();
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
