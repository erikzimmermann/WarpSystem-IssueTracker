package de.codingair.warpsystem.spigot.base.ad.features.utils;

import de.codingair.warpsystem.spigot.base.ad.features.*;

public enum Feature {
    TEMP_WARP_KEYS(new TempWarpKeys()),
    TELEPORT_COMMANDS(new TeleportCommands()),
    ;

    private PremiumFeature instance;

    Feature(PremiumFeature instance) {
        this.instance = instance;
    }

    public PremiumFeature getInstance() {
        return instance;
    }

    public String[] getSuccessMessage() {
        return getInstance().getSuccessMessage();
    }

    public String getName() {
        return getInstance().getName();
    }

    public boolean disable() {
        return getInstance().disable();
    }
}
