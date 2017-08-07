package de.codingair.warpsystem.remastered.gui.affiliations;

import java.io.Serializable;

public class ActionObject implements Serializable {
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
}
