package de.codingair.warpsystem.remastered.gui.affiliations;

import de.CodingAir.v1_6.CodingAPI.Tools.Location;

public enum Action {
    RUN_COMMAND(String.class),
    OPEN_CATEGORY(Category.class),
    TELEPORT_TO_WARP(Location.class),
    SWITCH_SERVER(String.class);

    private Class<?> clazz;

    Action(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
