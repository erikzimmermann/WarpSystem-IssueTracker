package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
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
    private static final TimeMap<String, Object[]> teleport = new TimeMap<>();

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
        Object[] n = teleport.remove(e.getPlayer().getName());

        if(n != null) {
            if(((Location) n[0]).getWorldName() == null) ((Location) n[0]).setWorld(e.getPlayer().getLocation().getWorld());

            if(((Location) n[0]).getWorld() == null) {
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> e.getPlayer().sendMessage(new String[] {" ", Lang.getPrefix() + "§4World '" + ((Location) n[0]).getWorldName() + "' is missing. Please contact an admin!", " "}), 2L);
                return;
            }

            e.setSpawnLocation((Location) n[0]);

            TeleportOptions options = new TeleportOptions(new Destination(new EmptyAdapter()), (String) n[1]);
            if(n[2] != null) options.setMessage((String) n[2]);
            options.setOrigin(Origin.GlobalWarp);
            options.setCanMove(true);
            options.setSilent(true);
            options.setSkip(true);
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

    public static void setSpawnPositionOrTeleport(String name, Location location, String displayName) {
        setSpawnPositionOrTeleport(name, location, displayName, null);
    }

    public static void setSpawnPositionOrTeleport(String name, Location location, String displayName, String message) {
        if(message != null) message = Lang.getPrefix() + message.replace("%warp%", displayName);

        Player player = Bukkit.getPlayer(name);

        if(player == null) {
            //save
            teleport.put(name, new Object[] {location, displayName, message});
        } else {
            //teleport
            if(location.getWorld() == null) {
                player.sendMessage(new String[] {" ", Lang.getPrefix() + "§4World '" + location.getWorld() + "' is missing. Please contact an admin!", " "});
                return;
            }

            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.GlobalWarp, new Destination(new LocationAdapter(location)), displayName, 0, true, true, true, true, null), 2L);
        }
    }
}
