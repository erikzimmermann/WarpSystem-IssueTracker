package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions;

import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Serializable;
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

    @Override
    public void destroy() {
        if(this.value instanceof Serializable) {
            ((Serializable) this.value).destroy();
        }
    }
}
