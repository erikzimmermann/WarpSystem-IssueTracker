package de.codingair.warpsystem.listeners;

import de.codingair.warpsystem.WarpSystem;
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
