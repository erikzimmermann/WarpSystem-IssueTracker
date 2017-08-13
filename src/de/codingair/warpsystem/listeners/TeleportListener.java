package de.codingair.warpsystem.listeners;

import de.codingair.warpsystem.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(!WarpSystem.getInstance().getTeleportManager().isTeleporting(p) || WarpSystem.getInstance().getTeleportManager().isCanMove()) return;

        double x = e.getFrom().getX() - e.getTo().getX();
        double y = e.getFrom().getY() - e.getTo().getY();
        double z = e.getFrom().getZ() - e.getTo().getZ();

        if(x < 0) x *= -1;
        if(y < 0) y *= -1;
        if(z < 0) z *= -1;

        double result = x + y + z;

        if(result > 0.05) {
            WarpSystem.getInstance().getTeleportManager().cancelTeleport(p);
        }
    }

}
