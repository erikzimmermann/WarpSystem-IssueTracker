package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NotifyListener implements Listener {

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> Notifier.notifyPlayers(e.getPlayer()), 20L * 5L);
    }

}
