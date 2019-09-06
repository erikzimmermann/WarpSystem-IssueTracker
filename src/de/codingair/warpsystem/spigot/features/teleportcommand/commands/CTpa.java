package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CTpa extends CommandBuilder {
    public CTpa() {
        super("Tpa", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpa <§eplayer§7>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpa <§eplayer§7>");
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName()) || TeleportCommandManager.getInstance().deniesTpaRequests(player)) continue;
                    suggestions.add(ChatColor.stripColor(player.getDisplayName()));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player receiver = Bukkit.getPlayer(argument);

                if(receiver == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                if(receiver.getName().equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Cant_Teleport_Yourself"));
                    return false;
                }

                if(TeleportCommandManager.getInstance().deniesTpaRequests(receiver)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(receiver.getDisplayName())));
                    return false;
                }

                if(TeleportCommandManager.getInstance().hasOpenInvites((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Open_Requests"));
                    return false;
                }

                int i = TeleportCommandManager.getInstance().sendTeleportRequest(new BungeePlayer((Player) sender), false, true, receiver);
                if(i == 0)
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_already_sent"));
                else
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_sent").replace("%PLAYER%", ChatColor.stripColor(receiver.getDisplayName())));
                return false;
            }
        });
    }
}
