package de.codingair.warpsystem.bungee;

import de.codingair.codingapi.bungeecord.files.FileManager;
import de.codingair.warpsystem.bungee.managers.GlobalWarpManager;
import de.codingair.warpsystem.bungee.managers.ServerManager;
import de.codingair.warpsystem.transfer.bungee.BungeeDataHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class WarpSystem extends Plugin {
    private static WarpSystem instance;
    private BungeeDataHandler dataHandler = new BungeeDataHandler(this);
    private FileManager fileManager = new FileManager(this);
    private GlobalWarpManager globalWarpManager = new GlobalWarpManager();
    private ServerManager serverManager = new ServerManager();

    @Override
    public void onEnable() {
        instance = this;

        this.dataHandler.onEnable();
        this.fileManager.loadFile("GlobalWarps", "/");
        this.globalWarpManager.load();
        this.serverManager.run();
    }

    @Override
    public void onDisable() {
        this.dataHandler.onDisable();
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public BungeeDataHandler getDataHandler() {
        return dataHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public GlobalWarpManager getGlobalWarpManager() {
        return globalWarpManager;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }
}
