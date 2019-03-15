package de.codingair.warpsystem.spigot.features.shortcuts.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.shortcuts.listeners.ShortcutListener;
import de.codingair.warpsystem.spigot.features.shortcuts.listeners.ShortcutPacketListener;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ShortcutManager implements Manager, BungeeFeature {
    private List<Shortcut> shortcuts = new ArrayList<>();
    private ShortcutPacketListener listener;

    @Override
    public boolean load() {
        WarpSystem.getInstance().getBungeeFeatureList().add(this);

        if(WarpSystem.getInstance().getFileManager().getFile("Shortcuts") == null) WarpSystem.getInstance().getFileManager().loadFile("Shortcuts", "/Memory/");

        this.shortcuts.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Shortcuts");
        FileConfiguration config = file.getConfig();

        WarpSystem.log("  > Loading Shortcuts");

        for(String key : config.getKeys(false)) {
            String dest = config.getString(key + ".Destination");

            //Old
            String warpId = config.getString(key + ".WarpId", null);
            String globalWarp = config.getString(key + ".GlobalWarp", null);

            Destination destination;
            if(dest != null) {
                destination = new Destination(dest);
            } else if(warpId != null) {
                destination = new Destination(warpId, DestinationType.SimpleWarp);
            } else if(globalWarp != null) {
                destination = new Destination(globalWarp, DestinationType.GlobalWarp);
            } else continue;

            this.shortcuts.add(new Shortcut(destination, key));
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
            config.set(sc.getDisplayName() + ".Destination", sc.getDestination() == null ? null : sc.getDestination().toJSONString());
        }

        if(!saver) WarpSystem.log("    ...saved " + config.getKeys(false).size() + " Shortcut(s)");

        file.saveConfig();
    }

    @Override
    public void destroy() {
        this.shortcuts.clear();
    }

    @Override
    public void onConnect() {
        WarpSystem.getInstance().getDataHandler().register(listener = new ShortcutPacketListener());
    }

    @Override
    public void onDisconnect() {
        if(listener != null) {
            WarpSystem.getInstance().getDataHandler().unregister(listener);
            listener = null;
        }
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
