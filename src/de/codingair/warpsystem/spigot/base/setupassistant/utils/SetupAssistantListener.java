package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.setupassistant.SetupAssistantManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SetupAssistantListener implements Listener {
    @EventHandler
    public void onFinalJoin(PlayerFinalJoinEvent e) {
        SetupAssistantManager.getInstance().onJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SetupAssistant a = SetupAssistantManager.getInstance().getAssistant();
        if(a != null && a.getPlayer().equals(e.getPlayer())) {
            a.onQuit();
        }
    }
}
