package de.codingair.warpsystem.gui.affiliations;

import de.codingair.codingapi.serializable.SerializableLocation;

public enum Action {
    RUN_COMMAND(0, String.class),
    OPEN_CATEGORY(1, Category.class),
    TELEPORT_TO_WARP(2, SerializableLocation.class),
    SWITCH_SERVER(3, String.class);

    private int id;
    private Class<?> clazz;

    Action(int id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getId() {
        return this.id;
    }

    public static Action getById(int id) {
        for(Action action : values()) {
            if(action.getId() == id) return action;
        }

        return null;
    }
}
