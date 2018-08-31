package de.codingair.warpsystem.spigot.api;

import de.codingair.warpsystem.spigot.api.blocks.listeners.RuleListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotAPI {
    private static SpigotAPI instance;

    public static SpigotAPI getInstance() {
        if(instance == null) instance = new SpigotAPI();
        return instance;
    }

    public void onEnable(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new RuleListener(), plugin);
    }

    public void onDisable(JavaPlugin plugin) {

    }
}
