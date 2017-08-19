package de.codingair.warpsystem.gui.affiliations;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category extends ActionIcon implements Serializable {
    static final long serialVersionUID = 1L;

    public Category() {
    }

    public Category(String name, ItemStack item, int slot) {
        super(name, item, slot, new ActionObject(Action.OPEN_CATEGORY, null));

        for(ActionObject action : getActions()) {
            if(action.getAction().equals(Action.OPEN_CATEGORY)) action.value = this;
        }
    }

    public Category(String name, ItemStack item, int slot, String permission) {
        super(name, item, slot, permission, new ActionObject(Action.OPEN_CATEGORY, null));

        for(ActionObject action : getActions()) {
            if(action.getAction().equals(Action.OPEN_CATEGORY)) action.value = this;
        }
    }
}
