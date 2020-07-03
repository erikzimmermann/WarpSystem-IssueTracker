package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.managers.PostWorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class PostWorldListener implements Listener {

    @EventHandler
    public void onLoad(WorldLoadEvent e) {
        PostWorldManager.getInstance().onLoad(e.getWorld());
    }

}
