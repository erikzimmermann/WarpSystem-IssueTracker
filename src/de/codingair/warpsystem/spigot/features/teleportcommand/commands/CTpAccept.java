package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.features.teleportcommand.Invitation;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CTpAccept extends WSCommandBuilder {
    public CTpAccept() {
        super("TpAccept", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP_ACCEPT) {
            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpaccept <§eplayer§7>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpaccept <§eplayer§7>");
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<Invitation> invites = TeleportCommandManager.getInstance().getReceivedInvites(sender.getName());

                for(Invitation invite : invites) {
                    suggestions.add(invite.getSender());
                }

                invites.clear();
            }

            @Override
            public boolean runCommand(CommandSender commandSender, String label, String argument, String[] args) {
                Invitation invitation = TeleportCommandManager.getInstance().getInvitation(argument, commandSender.getName());
                if(invitation != null) {
                    //has invitation
                    invitation.accept((Player) commandSender);
                } else {
                    //no invite
                    commandSender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", argument));
                }
                return false;
            }
        });
    }
}
