package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.tools.io.DataWriter;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import org.bukkit.entity.Player;

public class BoundAction extends ActionObject<String> {
    public BoundAction(String world) {
        super(Action.BOUND_TO_WORLD, world);
    }

    public BoundAction() {
        this(null);
    }

    @Override
    public void read(String s) {
        setValue(s);
    }

    @Override
    public String write() {
        return getValue();
    }

    @Override
    public boolean perform(Player player) {
        return true;
    }

    @Override
    public boolean usable() {
        return getValue() != null;
    }

    @Override
    public boolean read(DataWriter d) {
        setValue(d.get("world"));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("world", getValue());
    }

    @Override
    public BoundAction clone() {
        return new BoundAction(getValue());
    }
}
