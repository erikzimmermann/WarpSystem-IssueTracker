package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions;

import de.codingair.codingapi.tools.io.utils.Serializable;
import org.bukkit.entity.Player;

public abstract class ActionObject<T> implements Serializable {
    private Action type;
    private T value;

    public ActionObject(Action type, T value) {
        this.type = type;
        this.value = value;
    }

    public ActionObject() {
    }

    public abstract boolean perform(Player player);

    public abstract ActionObject clone();

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

    public abstract boolean usable();
}
