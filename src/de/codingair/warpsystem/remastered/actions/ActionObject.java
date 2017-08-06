package de.codingair.warpsystem.remastered.actions;

public class ActionObject {
    private Action action;
    private Object value;

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
