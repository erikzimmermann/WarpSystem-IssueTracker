package de.codingair.warpsystem.remastered.gui.affiliations;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Category extends Icon {
    private List<ActionIcon> actionIcons;

    public Category(String name, ItemStack item, int slot, List<ActionIcon> actionIcons) {
        super(name, item, slot);
        this.actionIcons = actionIcons;
    }

    public void addActionIcon(ActionIcon icon) {
        this.actionIcons.add(icon);
    }

    public void removeActionIcon(ActionIcon icon) {
        this.actionIcons.remove(icon);
    }
}
