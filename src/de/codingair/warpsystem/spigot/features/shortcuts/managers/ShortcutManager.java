package de.codingair.warpsystem.spigot.features.shortcuts.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.shortcuts.listeners.ShortcutListener;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ShortcutManager implements Manager {
    private List<Shortcut> shortcuts = new ArrayList<>();

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("Shortcuts") == null) WarpSystem.getInstance().getFileManager().loadFile("Shortcuts", "/Memory/");

        this.shortcuts.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Shortcuts");
        FileConfiguration config = file.getConfig();

        WarpSystem.log("  > Loading Shortcuts");

        for(String key : config.getKeys(false)) {
            String warpId = config.getString(key + ".WarpId", null);
            String globalWarp = config.getString(key + ".GlobalWarp", null);

            Warp warp = warpId == null ? null : IconManager.getInstance().getWarp(warpId);

            this.shortcuts.add(warp == null ? new Shortcut(globalWarp, key) : new Shortcut(warp, key));
        }

        WarpSystem.log("     ...got " + this.shortcuts.size() + " Shortcut(s)");

        Bukkit.getPluginManager().registerEvents(new ShortcutListener(), WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Shortcuts");
        FileConfiguration config = file.getConfig();

        if(!saver) WarpSystem.log("  > Saving Shortcuts");

        for(String key : config.getKeys(false)) config.set(key, null);

        for(Shortcut sc : this.shortcuts) {
            config.set(sc.getDisplayName() + ".WarpId", sc.getWarp() == null ? null : ((Warp) sc.getWarp()).getIdentifier());
            config.set(sc.getDisplayName() + ".GlobalWarp", sc.getGlobalWarp());
        }

        if(!saver) WarpSystem.log("    ...saved " + config.getKeys(false).size() + " Shortcut(s)");

        file.saveConfig();
    }

    public Shortcut getShortcut(String displayName) {
        for(Shortcut shortcut : this.shortcuts) {
            if(shortcut.getDisplayName().equalsIgnoreCase(displayName)) return shortcut;
        }

        return null;
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public static ShortcutManager getInstance() {
        return ((ShortcutManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.SHORTCUTS));
    }
}
