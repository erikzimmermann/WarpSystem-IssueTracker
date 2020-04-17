package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions;

import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.*;

public enum Action {
    WARP(0, WarpAction.class),
    COMMAND(1, CommandAction.class),
    COSTS(2, CostsAction.class),
    BOUND_TO_WORLD(3, BoundAction.class),
    MESSAGE(4, MessageAction.class),
    TELEPORT_SOUND(5, TeleportSoundAction.class),
    ;

    private int id;
    private Class<? extends ActionObject<?>> clazz;

    Action(int id, Class<? extends ActionObject<?>> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public static Action getById(int id) {
        for(Action value : values()) {
            if(value.id == id) return value;
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public Class<? extends ActionObject<?>> getClazz() {
        return clazz;
    }

    public Serializable getNewInstance() {
        try {
            return clazz.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
