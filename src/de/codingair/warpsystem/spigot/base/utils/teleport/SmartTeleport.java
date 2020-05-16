package de.codingair.warpsystem.spigot.base.utils.teleport;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class SmartTeleport {
    private Player player;
    private Location location;
    private Callback<Boolean> callback;

    public SmartTeleport(Player player, Location location, Callback<Boolean> callback) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(location);
        this.player = player;
        this.location = location;
        this.callback = callback;
    }

    public void start() {
        if((player.getWorld() != location.getWorld() || location.distance(player.getLocation()) > 100) && !location.getWorld().isChunkLoaded(location.getBlockX(), location.getBlockZ())) {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                boolean flying, allowed;
                float speed;

                @EventHandler
                public void onLoaded(ChunkLoadEvent e) {
                    if(e.getChunk().getX() == location.getBlockX() && e.getChunk().getZ() == location.getBlockZ()) {
                        flying = player.isFlying();
                        allowed = player.getAllowFlight();
                        speed = player.getFlySpeed();

                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.setFlySpeed(0F);

                        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                            player.setFallDistance(0);
                            player.setAllowFlight(allowed);
                            player.setFlying(flying);
                            player.setFlySpeed(speed);
                            HandlerList.unregisterAll(this);
                            if(callback != null) callback.accept(true);
                        }, 1L);
                    }
                }

                @EventHandler
                public void onMove(EntityDamageEvent e) {
                    if(e.getEntity().equals(player)) {
                        e.setCancelled(true);
                    }
                }
            }, WarpSystem.getInstance());

            location.getWorld().loadChunk(location.getBlockX(), location.getBlockZ());
        } else {
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            if(callback != null) callback.accept(true);
        }
    }
}
