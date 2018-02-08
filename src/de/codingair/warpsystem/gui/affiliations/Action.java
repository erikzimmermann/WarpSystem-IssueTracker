package de.codingair.warpsystem.gui.affiliations;

import de.codingair.codingapi.serializable.SerializableLocation;

public enum Action {
    RUN_COMMAND(String.class),
    OPEN_CATEGORY(Category.class),
    TELEPORT_TO_WARP(SerializableLocation.class),
    SWITCH_SERVER(String.class);

    private Class<?> clazz;

    Action(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
