package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;

public enum Origin {
    WarpIcon(Icon.class),
    GlobalWarpIcon,
    GlobalWarp,
    SimpleWarp,
    DirectSimpleWarp,
    Warp,
    TempWarp,
    WarpSign(WarpSign.class),
    EffectPortal(de.codingair.warpsystem.spigot.features.effectportals.utils.Portal.class),
    NativePortal(de.codingair.warpsystem.spigot.features.nativeportals.Portal.class),
    ShortCut,
    CommandBlock,
    TeleportCommand,
    UNKNOWN
    ;

    private Class<? extends FeatureObject> clazz = null;

    Origin() {
    }

    Origin(Class<? extends FeatureObject> clazz) {
        this.clazz = clazz;
    }

    public static Origin getByClass(FeatureObject clazz) {
        for(Origin value : values()) {
            if(value.clazz == clazz.getClass()) return value;
        }

        return UNKNOWN;
    }
}
