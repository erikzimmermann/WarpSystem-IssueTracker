package de.codingair.warpsystem.listeners;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeleportListener implements Listener {

    @EventHandler
    public void onMove(PlayerWalkEvent e) {
        Player p = e.getPlayer();

        if(!WarpSystem.getInstance().getTeleportManager().isTeleporting(p) || WarpSystem.getInstance().getTeleportManager().isCanMove()) return;
        WarpSystem.getInstance().getTeleportManager().cancelTeleport(p);
    }

}
