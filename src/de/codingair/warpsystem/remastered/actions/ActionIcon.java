package de.codingair.warpsystem.remastered.actions;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ActionIcon {
    private String name;
    private ItemStack icon;
    private List<ActionObject> actions;

    public ActionIcon(String name, ItemStack icon, ActionObject... actions) {
        this.name = name;
        this.icon = icon;
        this.actions = Arrays.asList(actions);
    }
}
