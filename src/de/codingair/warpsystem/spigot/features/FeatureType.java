package de.codingair.warpsystem.spigot.features;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.List;

public enum FeatureType {
    WARPS(IconManager.class, Priority.HIGHEST, "Warps"),
    GLOBAL_WARPS(GlobalWarpManager.class, Priority.LOW, "GlobalWarps"),
    SIGNS(SignManager.class, Priority.LOWEST, "WarpSigns"),
    PORTALS(PortalManager.class, Priority.LOW, "Portals"),
    NATIVE_PORTALS(NativePortalManager.class, Priority.LOW, "NativePortals"),
    SHORTCUTS(ShortcutManager.class, Priority.LOW, "Shortcuts"),
    TEMP_WARPS(TempWarpManager.class, Priority.LOW, "TempWarps"),
    SIMPLE_WARPS(SimpleWarpManager.class, Priority.HIGH, "SimpleWarps"),
    RANDOM_TELEPORTS(RandomTeleporterManager.class, Priority.HIGH, "RandomTeleports"),
    TELEPORT_COMMAND(TeleportCommandManager.class, Priority.ALWAYS_ON, "TeleportCommand"),
    ;

    private Class<? extends Manager> managerClass;
    private Priority priority;
    private String name;

    FeatureType(Class<? extends Manager> managerClass, Priority priority, String name) {
        this.managerClass = managerClass;
        this.priority = priority;
        this.name = name;
    }

    public Class<? extends Manager> getManagerClass() {
        return managerClass;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        if(getPriority() == Priority.ALWAYS_ON) return true;
        if(getPriority() == Priority.DISABLED) return false;
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        return file.getConfig().getBoolean("WarpSystem.Functions." + getName(), true);
    }

    public static FeatureType[] values(Priority priority) {
        List<FeatureType> featureTypes = new ArrayList<>();

        for(FeatureType value : values()) {
            if(value.getPriority().equals(priority)) featureTypes.add(value);
        }

        return featureTypes.toArray(new FeatureType[0]);
    }

    public Priority getPriority() {
        return priority;
    }

    public enum Priority {
        ALWAYS_ON,
        HIGHEST,
        HIGH,
        LOW,
        LOWEST,
        DISABLED
    }
}
