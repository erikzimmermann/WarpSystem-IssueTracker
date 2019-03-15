package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GChooseIconType extends GUI {
    private Callback<Boolean> callback;
    private boolean set = false;

    public GChooseIconType(Player p, Callback<Boolean> callback) {
        super(p, "§c" + Lang.get("GUI_Choose_Icon"), 9, WarpSystem.getInstance(), false);

        this.callback = callback;

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!set) {
                    Sound.ITEM_BREAK.playSound(p);
                    callback.accept(null);
                }
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

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        addButton(new ItemButton(2, new ItemBuilder(Material.ENDER_PEARL).setName("§c" + Lang.get("Icon")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(false);
            }
        }.setOption(option));

        addButton(new ItemButton(6, new ItemBuilder(Material.CHEST).setName("§c" + Lang.get("Category")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(true);
            }
        }.setOption(option));
    }
}
