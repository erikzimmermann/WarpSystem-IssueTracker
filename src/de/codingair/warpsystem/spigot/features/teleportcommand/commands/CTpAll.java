package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CTpAll extends CommandBuilder {
    public CTpAll() {
        super("TpAll", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPALL) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpAll <§eteleport request: true|false§7>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpAll <§eteleport request: true|false§7>");
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);

        getBaseComponent().addChild(new CommandComponent("true") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                List<Player> players = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName())) continue;
                    players.add(player);
                }

                if(TeleportCommandManager.getInstance().hasOpenInvites((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Open_Requests"));
                    return false;
                }

                int i = TeleportCommandManager.getInstance().sendTeleportRequest(new BungeePlayer((Player) sender), true, false, players.toArray(new Player[0]));
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", i + "").replace("%MAX%", (Bukkit.getOnlinePlayers().size() - 1) + ""));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("false") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                int i = 0;

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName())) continue;
                    if(TeleportCommandManager.getInstance().deniesForceTps(player)) continue;

                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CustomTeleportCommands, ((Player) sender).getLocation(), sender.getName(), true);
                    i++;
                }

                sender.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", i + "").replace("%MAX%", (Bukkit.getOnlinePlayers().size() - 1) + ""));
                return false;
            }
        });
    }
}
