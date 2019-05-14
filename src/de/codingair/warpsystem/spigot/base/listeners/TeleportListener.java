package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public class TeleportListener implements Listener {
    public static final HashMap<Player, Location> TELEPORTS = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent e) {
        Location loc = TELEPORTS.remove(e.getPlayer());

        if(loc != null) {
            e.setCancelled(false);
            e.setTo(loc);
        }
    }

    @EventHandler
    public void onMove(PlayerWalkEvent e) {
        Player p = e.getPlayer();

        if(!WarpSystem.getInstance().getTeleportManager().isTeleporting(p) || WarpSystem.getInstance().getTeleportManager().getTeleport(e.getPlayer()).isCanMove()) return;
        WarpSystem.getInstance().getTeleportManager().cancelTeleport(p);
    }

}
