package de.codingair.warpsystem.gui.affiliations;

import de.CodingAir.v1_6.CodingAPI.Serializable.SerializableItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class Icon implements Serializable {
    static final long serialVersionUID = 1L;

    String name;
    SerializableItemStack item;
    int slot;

    public Icon() {
    }

    public Icon(String name, ItemStack item, int slot) {
        this.name = name;
        this.item = new SerializableItemStack(item);
        this.slot = slot;
    }

    public void setItem(ItemStack item) {
        this.item = new SerializableItemStack(item);;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItem() {
        return item.getItem();
    }
}
