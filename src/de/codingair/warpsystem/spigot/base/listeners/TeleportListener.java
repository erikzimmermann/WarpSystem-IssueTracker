package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashMap;

public class TeleportListener implements Listener {
    public static final HashMap<Player, org.bukkit.Location> TELEPORTS = new HashMap<>();
    private static final TimeMap<String, TeleportOptions> teleport = new TimeMap<>();

    public static void setSpawnPositionOrTeleport(String name, TeleportOptions options) {
        if(options == null) {
            teleport.remove(name);
            return;
        }

        options.setSkip(true);
        Player player = Bukkit.getPlayer(name);

        if(player == null) {
            //save
            teleport.put(name, options);
        } else {
            //teleport
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().getTeleportManager().teleport(player, options), 2L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent e) {
        org.bukkit.Location loc = TELEPORTS.remove(e.getPlayer());

        if(loc != null) {
            e.setCancelled(false);
            e.setTo(loc);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(PlayerSpawnLocationEvent e) {
        TeleportOptions options = teleport.remove(e.getPlayer().getName());

        if(options != null) {
            org.bukkit.Location l = options.buildLocation();
            if(l == null || l.getWorld() == null) {
                String world = l instanceof Location ? ((Location) l).getWorldName() : null;
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> e.getPlayer().sendMessage(new String[] {" ", Lang.getPrefix() + "§4World " + (world == null ? "" : "'" + world + "' ") + "is missing. Please contact an admin!", " "}), 2L);
                return;
            }

            e.setSpawnLocation(l);

            options.setCanMove(true);
            options.setSilent(true);
            options.setSkip(true);

            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().getTeleportManager().teleport(e.getPlayer(), options), 2L);
        }
    }

    @EventHandler
    public void onMove(PlayerWalkEvent e) {
        Player p = e.getPlayer();

        if(!WarpSystem.getInstance().getTeleportManager().isTeleporting(p) || WarpSystem.getInstance().getTeleportManager().getTeleport(e.getPlayer()).isCanMove()) return;

        Block exact = p.getLocation().getBlock();
        Block below = p.getLocation().subtract(0, 0.5, 0).getBlock();

        if(exact.getType().name().contains("WATER") || below.getType().name().contains("WATER")
                || exact.getType().name().contains("LAVA") || below.getType().name().contains("LAVA")
                || exact.getType().name().contains("KELP") || below.getType().name().contains("KELP")
                || exact.getType().name().contains("SEAGRASS") || below.getType().name().contains("SEAGRASS")
        ) {
            Vector v = e.getTo().subtract(e.getFrom()).toVector();
            if(Math.abs(v.getX()) + Math.abs(v.getZ()) <= 0.05 && Math.abs(v.getY()) < 0.25) return;
        }

        WarpSystem.getInstance().getTeleportManager().cancelTeleport(p);
    }
}
