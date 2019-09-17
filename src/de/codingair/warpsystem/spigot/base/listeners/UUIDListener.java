package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UUIDListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if(Bukkit.getOnlinePlayers().size() > 1) WarpSystem.getInstance().getUUIDManager().download(e.getPlayer());
        else {
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                if(WarpSystem.getInstance().getUUIDManager().isCached(e.getPlayer().getName())) WarpSystem.getInstance().getUUIDManager().convertFromCached(e.getPlayer());
                else if(WarpSystem.getInstance().getUUIDManager().get(e.getPlayer()) == null) WarpSystem.getInstance().getUUIDManager().download(e.getPlayer());
            }, 20);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        WarpSystem.getInstance().getUUIDManager().remove(e.getPlayer());
    }

}
