package de.codingair.warpsystem.remastered.gui.affiliations;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ActionIcon extends Icon implements Serializable {
    private final static long serialVersionUID = 140217;

    List<ActionObject> actions;

    public ActionIcon() {
    }

    public ActionIcon(String name, ItemStack item, int slot, ActionObject... actions) {
        super(name, item, slot);
        this.actions = Arrays.asList(actions);
    }

    public List<ActionObject> getActions() {
        return actions;
    }
}
