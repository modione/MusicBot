package logic;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Logger extends ListenerAdapter {
    private static Logger INSTANCE;
    private final static String url = "https://discord.com/api/webhooks/908804147858534430/Bvd_4UaKbYF__-hcfW7FaAX4_YNhyvSkL4GzJ_8x_8vzAjrcbS4p7rOAVibQoaRJmNzn";
    private final WebhookClient webhook;

    Logger() {
        INSTANCE = this;
        webhook = build();
    }

    public static void send(String msg) {
        INSTANCE.webhook.send(msg);
    }

    public @NotNull WebhookClient build() {
        WebhookClientBuilder builder = new WebhookClientBuilder(url);
        builder.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setName("Webhook");
            return thread;
        });
        builder.setWait(true);
        return builder.build();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        send("Auf dem Server: **"+event.getGuild().getName()+"** wurde der Befehl: **"+event.getCommandString()+"**" +
                " von: **"+event.getMember().getEffectiveName()+"** ausgeführt.");
    }
}
