package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;

public class TeleportManager implements Manager {
    @Override
    public boolean load() {
        WarpSystem.log("  > Initializing TeleportManager");

        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTeleport());
        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), new TabCompleterListener());
        return true;
    }

    @Override
    public void save(boolean saver) {

    }

    @Override
    public void destroy() {

    }
}
