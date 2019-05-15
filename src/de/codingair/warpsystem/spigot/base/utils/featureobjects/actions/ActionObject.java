package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions;

import de.codingair.warpsystem.spigot.base.utils.featureobjects.Serializable;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public abstract class ActionObject<T> {
    private Action type;
    private T value;

    public ActionObject(Action type, T value) {
        this.type = type;
        this.value = value;
    }

    public ActionObject() {
    }

    public abstract boolean perform(Player player);

    public Action getType() {
        return type;
    }

    public void setType(Action type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract void read(String data);
    public abstract String write();

    public void destroy() {
        if(this.value instanceof Serializable) {
            ((Serializable) this.value).destroy();
        }
    }
}
