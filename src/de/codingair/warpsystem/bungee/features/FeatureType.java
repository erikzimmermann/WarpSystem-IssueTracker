package de.codingair.warpsystem.bungee.features;

import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.bungee.features.randomtp.RandomTPManager;
import de.codingair.warpsystem.bungee.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.List;

public enum FeatureType {
    GLOBAL_WARPS(GlobalWarpManager.class, Priority.LOW),
    TELEPORT(TeleportManager.class, Priority.LOW),
    SPAWN(SpawnManager.class, Priority.LOW),
    RANDOM_TP(RandomTPManager.class, Priority.LOW),
    ;

    private Class<? extends Manager> managerClass;
    private Priority priority;

    FeatureType(Class<? extends Manager> managerClass, Priority priority) {
        this.managerClass = managerClass;
        this.priority = priority;
    }

    public static FeatureType[] values(Priority priority) {
        List<FeatureType> featureTypes = new ArrayList<>();

        for(FeatureType value : values()) {
            if(value.getPriority().equals(priority)) featureTypes.add(value);
        }

        return featureTypes.toArray(new FeatureType[0]);
    }

    public Class<? extends Manager> getManagerClass() {
        return managerClass;
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
