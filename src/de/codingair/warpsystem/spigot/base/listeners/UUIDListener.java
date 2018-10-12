package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UUIDListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        WarpSystem.getInstance().getUUIDManager().download(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        WarpSystem.getInstance().getUUIDManager().remove(e.getPlayer());
    }

}
