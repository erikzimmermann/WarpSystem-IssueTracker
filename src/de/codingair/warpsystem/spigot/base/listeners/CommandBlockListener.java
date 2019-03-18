package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.commands.CWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandBlockListener implements Listener {

    @EventHandler
    public void onCommandBlock(ServerCommandEvent e) {
        if(e.getSender() instanceof BlockCommandSender) {
            String cmd = e.getCommand().replaceFirst("/", "");
            String arg = null;
            if(cmd.contains(" ")) {
                String[] a = cmd.split(" ");
                cmd = a[0];
                if(a.length > 1) arg = a[1];
            }

            PluginCommand command = Bukkit.getPluginCommand(cmd);
            if(command == null) return;
            if(command.getExecutor() instanceof CWarp || command.getExecutor() instanceof CGlobalWarp) {
                Location location = ((BlockCommandSender) e.getSender()).getBlock().getLocation().add(0.5, 0, 0.5);
                Player player = getNearest(location, 5);
                if(player == null) return;

                if(command.getExecutor() instanceof CWarp) {
                    if(SimpleWarpManager.getInstance() != null) {
                        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(arg);
                        if(warp != null) {
                            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CommandBlock, new Destination(warp.getName(), DestinationType.SimpleWarp), warp.getName(), 0, true, true,
                                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.CommandBlocks", true), false, null);
                            return;
                        }
                    }
                }

                if(command.getExecutor() instanceof CGlobalWarp) {
                    String name = arg;

                    if(GlobalWarpManager.getInstance() != null) {
                        if(GlobalWarpManager.getInstance().exists(name)) {
                            name = GlobalWarpManager.getInstance().getCaseCorrectlyName(name);
                            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CommandBlock, new Destination(name, DestinationType.GlobalWarp), name, 0, true, true,
                                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.CommandBlocks", true), false, null);
                        }
                    }
                }
            }
        }
    }

    private Player getNearest(Location from, double maxDistance) {
        Player nearest = null;
        double distance = -1;

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getLocation().getWorld().equals(from.getWorld())) {
                double d = player.getLocation().distance(from);
                if(d <= maxDistance && (distance == -1 || d < distance)) {
                    nearest = player;
                    distance = d;
                }
            }
        }

        return nearest;
    }

}
