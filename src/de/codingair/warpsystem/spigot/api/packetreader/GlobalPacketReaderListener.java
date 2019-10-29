package de.codingair.warpsystem.spigot.api.packetreader;

import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlobalPacketReaderListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> SpigotAPI.getInstance().getGlobalPacketReaderManager().injectAll(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SpigotAPI.getInstance().getGlobalPacketReaderManager().uninjectAll(e.getPlayer());
    }

}
