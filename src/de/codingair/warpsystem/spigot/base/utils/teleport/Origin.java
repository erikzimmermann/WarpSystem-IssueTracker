package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.options.specific.FeatureOptions;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
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
    NativePortal(de.codingair.warpsystem.spigot.features.nativeportals.NativePortal.class, "NativePortals"),
    ShortCut(Shortcut.class, "Shortcuts"),
    CommandBlock,
    TeleportCommand,
    Custom,
    CustomTeleportCommands,
    PlayerWarp(de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp.class, "PlayerWarps"),
    UNKNOWN;

    private Class<? extends FeatureObject> clazz = null;
    private String configName = null;
    private Class<? extends Options> optionClass;

    Origin() {
    }

    Origin(Class<? extends FeatureObject> clazz, String configName) {
        this.clazz = clazz;
        this.configName = configName;
    }

    Origin(Class<? extends FeatureObject> clazz, String configName, Class<? extends Options> optionClass) {
        this.clazz = clazz;
        this.configName = configName;
        this.optionClass = optionClass;
    }

    public static Origin getByClass(FeatureObject clazz) {
        for(Origin value : values()) {
            if(value.clazz == clazz.getClass()) return value;
        }

        return UNKNOWN;
    }

    public FeatureOptions getOptions() {
        return (FeatureOptions) WarpSystem.getOptions(optionClass);
    }

    public Class<? extends FeatureObject> getClazz() {
        return clazz;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean sendTeleportMessage() {
        if(configName == null) return false;
        FeatureOptions options = getOptions();
        if(options != null) return options.getSendTeleportMessage().getValue();

        return WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message." + getConfigName(), true);
    }
}
