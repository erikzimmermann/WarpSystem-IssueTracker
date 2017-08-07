package de.codingair.warpsystem.remastered;

import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.Files.FileManager;
import de.codingair.warpsystem.remastered.managers.IconManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpSystem extends JavaPlugin {
    private static WarpSystem instance;

    private IconManager iconManager = new IconManager();
    private FileManager fileManager = new FileManager(this);

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        this.fileManager.loadFile("ActionIcons", "/Memory/");
        this.iconManager.load(true);
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable();

        this.iconManager.save(true);
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
