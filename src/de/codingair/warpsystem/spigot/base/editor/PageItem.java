package de.codingair.warpsystem.spigot.base.editor;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PageItem extends Page {
    private ItemStack pageItem;

    public PageItem(Player p, String title, ItemStack pageItem, boolean preInitialize) {
        super(p, title, false);

        this.pageItem = pageItem;

        if(preInitialize) initialize(p);
    }

    public ItemStack getPageItem() {
        return pageItem;
    }

    @Override
    public Editor getLast() {
        return (Editor) super.getLast();
    }
}
