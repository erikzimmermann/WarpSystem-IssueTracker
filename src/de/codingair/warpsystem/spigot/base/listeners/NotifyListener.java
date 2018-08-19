package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NotifyListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().notifyPlayers(p), 20L);
    }

}
