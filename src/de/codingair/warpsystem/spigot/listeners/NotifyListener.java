package de.codingair.warpsystem.spigot.listeners;

import de.codingair.warpsystem.spigot.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NotifyListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        WarpSystem.getInstance().notifyPlayers(p);
    }

}
