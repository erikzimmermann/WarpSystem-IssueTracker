package de.codingair.warpsystem.gui.affiliations;

import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Warp extends ActionIcon implements Serializable, Mergable {
    static final long serialVersionUID = 1L;
    Category category;

    public Warp() {
    }

    public Warp(String name, ItemStack item, int slot, Category category, List<ActionObject> actions) {
        super(name, item, slot, actions);
        this.category = category;
    }

    public Warp(String name, ItemStack item, int slot, Category category, ActionObject... actions) {
        this(name, item, slot, category, Arrays.asList(actions));
    }

    public Warp(String name, ItemStack item, int slot, String permission, Category category, List<ActionObject> actions) {
        super(name, item, slot, permission, actions);
        this.category = category;
    }

    public Warp(String name, ItemStack item, int slot, String permission, Category category, ActionObject... actions) {
        this(name, item, slot, permission, category, Arrays.asList(actions));
    }

    @Override
    public IconType getType() {
        return IconType.WARP;
    }

    @Override
    public Object convert() {
        List<de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionObject> list = new ArrayList<>();
        for(ActionObject action : getActions()) {
            list.add(new de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionObject(Action.getById(action.getAction().getId()), action.getValue()));
        }

        return new de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp(getName(), getItem(), getSlot(), getPermission(), this.category == null ? null : (de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category) this.category.convert(), list);
    }
}
