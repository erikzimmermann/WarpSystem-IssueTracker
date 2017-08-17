package de.codingair.warpsystem.gui.affiliations;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category extends ActionIcon implements Serializable {
    static final long serialVersionUID = 1L;
    List<Warp> warps;

    public Category() {
    }

    public Category(String name, ItemStack item, int slot, List<Warp> warps) {
        super(name, item, slot, new ActionObject(Action.OPEN_CATEGORY, null));
        this.warps = warps;

        for(ActionObject action : getActions()) {
            if(action.getAction().equals(Action.OPEN_CATEGORY)) action.value = this;
        }
    }

    public Category(String name, ItemStack item, int slot, Warp... warps) {
        this(name, item, slot, Arrays.asList(warps));
    }

    public Category(String name, ItemStack item, int slot, String permission, List<Warp> warps) {
        super(name, item, slot, permission, new ActionObject(Action.OPEN_CATEGORY, null));
        this.warps = warps;

        for(ActionObject action : getActions()) {
            if(action.getAction().equals(Action.OPEN_CATEGORY)) action.value = this;
        }
    }

    public Category(String name, ItemStack item, int slot, String permission, Warp... warps) {
        this(name, item, slot, permission, Arrays.asList(warps));
    }

    public void addWarp(Warp warp) {
        checkList();
        this.warps.add(warp);
    }

    public void removeWarp(Warp warp) {
        checkList();
        this.warps.remove(warp);
    }

    public List<Warp> getWarps() {
        return warps;
    }

    private void checkList() {
        if(!(this.warps instanceof ArrayList)) {
            List<Warp> warps = new ArrayList<>();
            warps.addAll(this.warps);
            this.warps = warps;
        }
    }
}
