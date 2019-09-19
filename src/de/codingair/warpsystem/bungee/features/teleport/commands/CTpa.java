package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CTpa extends Command {
    public CTpa() {
        super("tpa");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Only_For_Players")));
            return;
        }

        if(!sender.hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("No_Permission")));
            return;
        }

        if(args.length != 1) {
            sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpa <§eplayer§7>");
        } else {
            ProxiedPlayer receiver = BungeeCord.getInstance().getPlayer(args[0]);

            if(receiver == null) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                return;
            }

            if(receiver.getName().equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Cant_Teleport_Yourself"));
                return;
            }

            if(TeleportManager.getInstance().deniesTpaRequests(receiver)) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(receiver.getDisplayName())));
                return;
            }

            if(TeleportManager.getInstance().hasOpenInvites((ProxiedPlayer) sender)) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Open_Requests"));
                return;
            }

            if(!((ProxiedPlayer) sender).getServer().getInfo().equals(receiver.getServer().getInfo()) && (
                    !TeleportManager.getInstance().isAccessible(((ProxiedPlayer) sender).getServer().getInfo()) || !TeleportManager.getInstance().isAccessible(receiver.getServer().getInfo())
            )) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Receiver_not_availabe"));
                return;
            }

            TeleportManager.getInstance().sendTeleportRequest((ProxiedPlayer) sender, false, true, receiver);
            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_sent").replace("%PLAYER%", ChatColor.stripColor(receiver.getDisplayName())));
        }
    }
}
