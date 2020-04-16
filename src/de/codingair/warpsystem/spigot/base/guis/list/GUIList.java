package de.codingair.warpsystem.spigot.base.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class GUIList<E> extends SimpleGUI {

    public GUIList(Player p, String title, boolean searchable) {
        super(p, null, new PList<E>(p, searchable, title), WarpSystem.getInstance(), false);

        List<ListItem<E>> itemList = new ArrayList<>();
        addListItems(itemList);
        ((PList<E>) getMain()).initList(itemList);

        initialize(p);

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {
            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {
            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(isClosingForGUI() || isClosingByButton() || isClosingByOperation()) return;
                onClose();
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {
            }

            @Override
            public void onMoveToTopInventory(ItemStack item, int oldRawSlot, List<Integer> newRawSlots) {
            }

            @Override
            public void onCollectToCursor(ItemStack item, List<Integer> oldRawSlots, int newRawSlot) {
            }
        });
    }

    public String getSearched() {
        return ((PList) getMain()).getSearching();
    }

    public abstract void addListItems(List<ListItem<E>> items);

    public abstract void onClick(E value, ClickType clickType);

    public abstract void onClose();

    public abstract void buildItemDescription(List<String> lore);
}
