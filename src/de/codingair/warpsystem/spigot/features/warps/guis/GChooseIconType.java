package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.IconType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GChooseIconType extends GUI {
    private Callback<IconType> callback;
    private boolean set = false;
    private Category category;

    public GChooseIconType(Player p, Category category, Callback<IconType> callback) {
        super(p, "§c" + Lang.get("GUI_Choose_Icon"), 9, WarpSystem.getInstance(), false);

        this.callback = callback;
        this.category = category;

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

        int items = 2;
        if(this.category == null) items++;
        if(WarpSystem.getInstance().isOnBungeeCord()) items++;

        List<Integer> slots = new ArrayList<>();
        switch(items) {
            case 2:
                slots.add(2);
                slots.add(6);
                break;

            case 3:
                slots.add(1);
                slots.add(4);
                slots.add(7);
                break;

            case 4:
                slots.add(1);
                slots.add(3);
                slots.add(5);
                slots.add(7);
                break;
        }

        addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.ENDER_PEARL).setName("§c" + Lang.get("Warp")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(IconType.WARP);
            }
        }.setOption(option));

        if(this.category == null) {
            addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.CHEST).setName("§c" + Lang.get("Category")).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    set = true;
                    p.closeInventory();
                    callback.accept(IconType.CATEGORY);
                }
            }.setOption(option));
        }

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.ENDER_CHEST).setName("§c" + Lang.get("GlobalWarp")).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().isEmpty()) {
                        getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarps_Not_Available"));
                    } else {
                        set = true;
                        p.closeInventory();
                        callback.accept(IconType.GLOBAL_WARP);
                    }
                }
            }.setOption(option));
        }

        addButton(new ItemButton(slots.remove(0), new ItemBuilder(XMaterial.FLOWER_POT).setName("§c" + Lang.get("Decoration")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(IconType.DECORATION);
            }
        }.setOption(option));
    }
}
