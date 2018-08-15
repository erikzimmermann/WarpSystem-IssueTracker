package de.codingair.warpsystem.spigot.features;

import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.List;

public enum FeatureType {
    WARPS(IconManager.class, Priority.HIGH),
    GLOBAL_WARPS(GlobalWarpManager.class, Priority.LOW),
    SIGNS(SignManager.class, Priority.LOWEST),
    PORTALS(PortalManager.class, Priority.LOW),
    ;

    private Class<? extends Manager> managerClass;
    private Priority priority;

    FeatureType(Class<? extends Manager> managerClass, Priority priority) {
        this.managerClass = managerClass;
        this.priority = priority;
    }

    public Class<? extends Manager> getManagerClass() {
        return managerClass;
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
        HIGHEST,
        HIGH,
        LOW,
        LOWEST
    }
}
