package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import java.io.Serializable;

public class ActionObject implements Serializable {
    static final long serialVersionUID = 1L;

    Action action;
    Object value;

    public ActionObject() {
    }

    public ActionObject(Action action, Object value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public <T> T getValue() {
        return (T) action.getClazz().cast(this.value);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
