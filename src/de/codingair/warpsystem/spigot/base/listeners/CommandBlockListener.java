package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.commands.CWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommandBlockListener implements Listener {

    @EventHandler
    public void onCommandBlock(ServerCommandEvent e) {
        if(e.getSender() instanceof BlockCommandSender) {
            String cmd = e.getCommand().replaceFirst("/", "");
            String arg = null;
            String specific = null;

            if(cmd.contains(" ")) {
                String[] a = cmd.split(" ");
                cmd = a[0];
                if(a.length > 1) arg = a[1];
                if(a.length > 2) specific = a[2];
            }

            PluginCommand command = Bukkit.getPluginCommand(cmd);
            if(command != null && (command.getExecutor() instanceof CWarp || command.getExecutor() instanceof CGlobalWarp)) {
                e.setCancelled(true);
                List<Player> players = new ArrayList<>();
                Location location = ((BlockCommandSender) e.getSender()).getBlock().getLocation().add(0.5, 0, 0.5);

                if(specific == null) {
                    Player player = getNearest(location, -1);
                    if(player != null) players.add(player);
                } else {
                    switch(specific.toLowerCase()) {
                        case "@a": {
                            players.addAll(Bukkit.getOnlinePlayers());
                            break;
                        }

                        case "@r": {
                            if(Bukkit.getOnlinePlayers().isEmpty()) break;
                            Player player = Bukkit.getOnlinePlayers().toArray(new Player[0])[(int) (Math.random() * Bukkit.getOnlinePlayers().size())];
                            if(player != null) players.add(player);
                            break;
                        }

                        default: {
                            Player player = getNearest(location, -1);
                            if(player != null) players.add(player);
                            break;
                        }
                    }
                }

                if(players.isEmpty()) return;

                if(command.getExecutor() instanceof CWarp) {
                    if(SimpleWarpManager.getInstance() != null) {
                        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(arg);
                        if(warp != null) {
                            TeleportOptions options = new TeleportOptions(new Destination(warp.getName(), DestinationType.SimpleWarp), warp.getName());
                            options.setOrigin(Origin.CommandBlock);
                            options.setSkip(true);

                            for(Player player : players) {
                                WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                            }
                            return;
                        }
                    }
                }

                if(command.getExecutor() instanceof CGlobalWarp) {
                    String name = arg;

                    if(GlobalWarpManager.getInstance() != null) {
                        if(GlobalWarpManager.getInstance().exists(name)) {
                            name = GlobalWarpManager.getInstance().getCaseCorrectlyName(name);

                            TeleportOptions options = new TeleportOptions(new Destination(name, DestinationType.GlobalWarp), name);
                            options.setOrigin(Origin.CommandBlock);
                            options.setSkip(true);

                            for(Player player : players) {
                                WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                            }
                        }
                    }
                }
            } else if(cmd.equalsIgnoreCase("tp") || cmd.equalsIgnoreCase("teleport")) {
                e.setCancelled(true);
                try {
                    SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getServer().getPluginManager();
                    Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);
                    SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMapField.get(simplePluginManager);

                    Command tpCommand = simpleCommandMap.getCommand("minecraft:tp");

                    String[] a = e.getCommand().split(" ");
                    String[] args = new String[a.length - 1];

                    if(args.length >= 0) System.arraycopy(a, 1, args, 0, args.length);

                    tpCommand.execute(e.getSender(), cmd, args);
                } catch(IllegalAccessException | NoSuchFieldException e1) {
                    e1.printStackTrace();
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
                if((maxDistance == -1 || d <= maxDistance) && (distance == -1 || d < distance)) {
                    nearest = player;
                    distance = d;
                }
            }
        }

        return nearest;
    }

}
