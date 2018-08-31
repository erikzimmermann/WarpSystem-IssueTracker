package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeleportListener implements Listener {

    @EventHandler
    public void onMove(PlayerWalkEvent e) {
        Player p = e.getPlayer();

        if(!WarpSystem.getInstance().getTeleportManager().isTeleporting(p) || WarpSystem.getInstance().getTeleportManager().getTeleport(e.getPlayer()).isCanMove()) return;
        WarpSystem.getInstance().getTeleportManager().cancelTeleport(p);
    }

}
