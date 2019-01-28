package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;

public enum Action {
    RUN_COMMAND(0, String.class, Priority.LOW),
    OPEN_CATEGORY(1, Category.class, Priority.LOW),
    TELEPORT_TO_WARP(2, SerializableLocation.class, Priority.LOW),
    SWITCH_SERVER(3, String.class, Priority.LOW),
    PAY_MONEY(4, Double.class, Priority.HIGHEST),
    BOUND_TO_WORLD(5, String.class, Priority.LOW),
    ;

    private int id;
    private Class<?> clazz;
    private Priority priority;

    Action(int id, Class<?> clazz, Priority priority) {
        this.id = id;
        this.clazz = clazz;
        this.priority = priority;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getId() {
        return this.id;
    }

    public Priority getPriority() {
        return priority;
    }

    public static Action getById(int id) {
        for(Action action : values()) {
            if(action.getId() == id) return action;
        }

        return null;
    }
    
    public enum Priority {
        HIGHEST, HIGH, LOW, LOWEST
    }
}
