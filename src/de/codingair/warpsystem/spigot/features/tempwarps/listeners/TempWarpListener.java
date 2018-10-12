package de.codingair.warpsystem.spigot.features.tempwarps.listeners;

import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TempWarpListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        TempWarpManager.getManager().updateWarps(e.getPlayer());
    }

}
