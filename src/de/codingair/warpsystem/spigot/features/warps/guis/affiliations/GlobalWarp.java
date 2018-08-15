package de.codingair.warpsystem.spigot.features.warps.guis.affiliations;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.List;

public class GlobalWarp extends ActionIcon implements Serializable {
    static final long serialVersionUID = 1L;
    private Category category;

    public GlobalWarp() {
    }

    public GlobalWarp(Category category) {
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, Category category) {
        super(name, item, slot);
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, String permission, Category category) {
        super(name, item, slot, permission);
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, List<ActionObject> actions, Category category) {
        super(name, item, slot, actions);
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, String permission, List<ActionObject> actions, Category category) {
        super(name, item, slot, permission, actions);
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, Category category, ActionObject... actions) {
        super(name, item, slot, actions);
        this.category = category;
    }

    public GlobalWarp(String name, ItemStack item, int slot, String permission, Category category, ActionObject... actions) {
        super(name, item, slot, permission, actions);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public IconType getType() {
        return IconType.GLOBAL_WARP;
    }
}
