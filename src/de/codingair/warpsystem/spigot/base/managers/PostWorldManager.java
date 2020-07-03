package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.listeners.PostWorldListener;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostWorldManager {
    private static PostWorldManager instance;
    private final HashMap<String, List<Callback<World>>> callbacks = new HashMap<>();

    public PostWorldManager() {
        if(instance == null) instance = this;
        Bukkit.getPluginManager().registerEvents(new PostWorldListener(), WarpSystem.getInstance());
    }

    public static void callback(String world, Callback<World> callback) {
        List<Callback<World>> l = getInstance().callbacks.computeIfAbsent(world.toLowerCase(), k -> new ArrayList<>());
        l.add(callback);
    }

    public void onLoad(World world) {
        List<Callback<World>> l = callbacks.remove(world.getName().toLowerCase());
        if(l == null) return;

        for(Callback<World> c : l) {
            c.accept(world);
        }
        l.clear();
    }

    public static PostWorldManager getInstance() {
        return instance;
    }
}
