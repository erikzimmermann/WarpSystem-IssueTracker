package de.codingair.warpsystem.spigot.api;

import de.codingair.warpsystem.spigot.api.blocks.listeners.RuleListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderManager;
import de.codingair.warpsystem.spigot.api.packetreader.readers.TeleportReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotAPI {
    private static SpigotAPI instance;

    private GlobalPacketReaderManager globalPacketReaderManager = new GlobalPacketReaderManager();

    public static SpigotAPI getInstance() {
        if(instance == null) instance = new SpigotAPI();
        return instance;
    }

    public void onEnable(JavaPlugin plugin) {
        this.globalPacketReaderManager.register(new TeleportReader(), false);

        Bukkit.getPluginManager().registerEvents(new RuleListener(), plugin);
        this.globalPacketReaderManager.onEnable();
        Bukkit.getPluginManager().registerEvents(new GlobalPacketReaderListener(), plugin);
    }

    public void onDisable(JavaPlugin plugin) {
        this.globalPacketReaderManager.onDisable();
    }

    public GlobalPacketReaderManager getGlobalPacketReaderManager() {
        return globalPacketReaderManager;
    }
}
