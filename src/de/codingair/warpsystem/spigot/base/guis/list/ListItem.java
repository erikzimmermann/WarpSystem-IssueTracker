package de.codingair.warpsystem.spigot.base.guis.list;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class ListItem<E> {
    private final E value;
    private final ItemStack item;

    public ListItem(E value) {
        this.value = value;
        this.item = buildItem();
    }

    public E getValue() {
        return value;
    }

    public ItemStack getItem() {
        return item;
    }

    public abstract ItemStack buildItem();

    public abstract void onClick(E value, ClickType clickType);

    public abstract boolean isSearched(String searching);
}
