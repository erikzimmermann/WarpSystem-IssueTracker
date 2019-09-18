package de.codingair.warpsystem.spigot.features.teleportcommand.listeners;

import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TeleportCommandManager.getInstance().clearBackHistory(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        TeleportCommandManager.getInstance().addToBackHistory(e.getEntity(), e.getEntity().getLocation());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN || TeleportCommandManager.getInstance().usingBackCommand(e.getPlayer())) return;
        TeleportCommandManager.getInstance().addToBackHistory(e.getPlayer(), e.getFrom());
    }

}
