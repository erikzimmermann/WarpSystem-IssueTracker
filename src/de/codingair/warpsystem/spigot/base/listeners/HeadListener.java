package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadListener implements Listener {

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        WarpSystem.getInstance().getHeadManager().update(e.getPlayer());
    }

}
