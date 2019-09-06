package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;

public enum Origin {
    WarpIcon(Icon.class, "WarpGUI"),
    GlobalWarpIcon,
    GlobalWarp,
    SimpleWarp,
    DirectSimpleWarp,
    Warp,
    TempWarp,
    WarpSign(WarpSign.class, "WarpSigns"),
    EffectPortal(de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal.class, "Portals"),
    NativePortal(de.codingair.warpsystem.spigot.features.nativeportals.Portal.class, "NativePortals"),
    ShortCut(Shortcut.class, "Shortcuts"),
    CommandBlock,
    TeleportCommand,
    Custom,
    CustomTeleportCommands,
    UNKNOWN;

    private Class<? extends FeatureObject> clazz = null;
    private String configName = null;

    Origin() {
    }

    Origin(Class<? extends FeatureObject> clazz, String configName) {
        this.clazz = clazz;
        this.configName = configName;
    }

    public static Origin getByClass(FeatureObject clazz) {
        for(Origin value : values()) {
            if(value.clazz == clazz.getClass()) return value;
        }

        return UNKNOWN;
    }

    public Class<? extends FeatureObject> getClazz() {
        return clazz;
    }

    public String getConfigName() {
        return configName;
    }
}
