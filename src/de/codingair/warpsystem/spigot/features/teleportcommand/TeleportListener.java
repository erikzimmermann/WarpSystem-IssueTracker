package de.codingair.warpsystem.spigot.features.teleportcommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TeleportCommandManager.getInstance().clear(e.getPlayer());
    }

}
