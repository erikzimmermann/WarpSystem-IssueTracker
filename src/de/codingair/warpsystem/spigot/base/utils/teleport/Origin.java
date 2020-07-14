package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
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
    ShortCut(Shortcut.class, "Shortcuts"),
    CommandBlock,
    TeleportCommand,
    Custom,
    TeleportRequest,
    PlayerWarp(PlayerWarp.class, "PlayerWarps"),
    Portal(Portal.class, "Portals"),
    Spawn(Spawn.class, "Spawn"),
    RandomTP(null, "RandomTp"),
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

    public boolean sendTeleportMessage() {
        if(configName == null) return false;
        return WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message." + getConfigName(), true);
    }

    public long getCooldown() {
        return WarpSystem.opt().getCooldown(this);
    }
}
