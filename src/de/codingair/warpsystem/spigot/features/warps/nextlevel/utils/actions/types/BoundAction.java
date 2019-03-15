package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types;

import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.ActionObject;
import org.bukkit.entity.Player;

public class BoundAction extends ActionObject<String> {
    public BoundAction(String command) {
        super(Action.BOUND_TO_WORLD, command);
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
}
