package de.codingair.warpsystem.gui.affiliations;

import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import de.codingair.warpsystem.transfer.serializeable.icons.SWarp;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Warp extends ActionIcon implements Serializable {
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

    public Warp(SWarp s) {
        super(s);
        this.category = WarpSystem.getInstance().getIconManager().getCategory(s.getCategory());
    }

    public boolean isInCategory() {
        return category != null;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public Location getLocation() {
        SerializableLocation sLoc = getAction(Action.TELEPORT_TO_WARP).getValue();
        return sLoc.getLocation();
    }

    @Override
    public SIcon getSerializable() {
        SWarp s = new SWarp(super.getSerializable());
        s.setCategory(this.category == null ? null : this.category.getName());
        return s;
    }
}
