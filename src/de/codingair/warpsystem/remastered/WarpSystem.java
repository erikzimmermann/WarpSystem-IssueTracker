package de.codingair.warpsystem.remastered;

import de.CodingAir.v1_6.CodingAPI.API;
import de.codingair.warpsystem.remastered.managers.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpSystem extends JavaPlugin {
    private static WarpSystem instance;
    private WarpManager warpManager = new WarpManager();

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable();
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
}
