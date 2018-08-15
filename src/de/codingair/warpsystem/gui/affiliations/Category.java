package de.codingair.warpsystem.gui.affiliations;

import de.codingair.warpsystem.transfer.serializeable.icons.SCategory;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class Category extends ActionIcon implements Serializable, Mergable {
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

    public Category(SCategory s) {
        super(s);
    }

    @Override
    public SIcon getSerializable() {
        return new SCategory(super.getSerializable());
    }

    @Override
    public IconType getType() {
        return IconType.CATEGORY;
    }

    @Override
    public Object convert() {
        de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category c = new de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category(getName(), getItem(), getSlot(), getPermission());

        for(ActionObject action : getActions()) {
            c.addAction(new de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionObject(de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action.getById(action.getAction().getId()), action.getValue()));
        }

        return c;
    }
}
