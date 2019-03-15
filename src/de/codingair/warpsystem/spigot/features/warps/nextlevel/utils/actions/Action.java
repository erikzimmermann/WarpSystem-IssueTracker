package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions;

import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.BoundAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.WarpAction;

public enum Action {
    WARP(0, WarpAction.class),
    COMMAND(1, CommandAction.class),
    COSTS(2, CostsAction.class),
    BOUND_TO_WORLD(3, BoundAction.class),
    ;

    private int id;
    private Class<? extends ActionObject> clazz;

    Action(int id, Class<? extends ActionObject> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public Class<? extends ActionObject> getClazz() {
        return clazz;
    }

    public static Action getById(int id) {
        for(Action value : values()) {
            if(value.id == id) return value;
        }

        return null;
    }
}
