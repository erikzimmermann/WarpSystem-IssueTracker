package de.codingair.warpsystem.spigot.gui.guis;

import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.MultiItemType;
import de.codingair.warpsystem.gui.affiliations.Category;
import de.codingair.warpsystem.gui.affiliations.IconType;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
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
        super(p, "§c" + Lang.get("GUI_Choose_Icon", new Example("ENG", "Choose your IconType"), new Example("GER", "Wähle deinen Symbol-Typen")), 9, WarpSystem.getInstance(), false);

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

        addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.ENDER_PEARL).setName("§c" + Lang.get("Warp", new Example("ENG", "Warp"), new Example("GER", "Warp"))).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(IconType.WARP);
            }
        }.setOption(option));

        if(this.category == null) {
            addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.CHEST).setName("§c" + Lang.get("Category", new Example("ENG", "Category"), new Example("GER", "Kategorie"))).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    set = true;
                    p.closeInventory();
                    callback.accept(IconType.CATEGORY);
                }
            }.setOption(option));
        }

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            addButton(new ItemButton(slots.remove(0), new ItemBuilder(Material.ENDER_CHEST).setName("§c" + Lang.get("GlobalWarp", new Example("ENG", "GlobalWarp"), new Example("GER", "GlobalWarp"))).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().isEmpty()) {
                        getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarps_Not_Available", new Example("ENG", "&cThere are no GlobalWarps, which can be linked!"), new Example("GER", "&cEs existieren keine GlobalWarps, die verknüpft werden können!")));
                    } else {
                        set = true;
                        p.closeInventory();
                        callback.accept(IconType.GLOBAL_WARP);
                    }
                }
            }.setOption(option));
        }

        addButton(new ItemButton(slots.remove(0), new ItemBuilder(MultiItemType.FLOWER_POT_ITEM).setName("§c" + Lang.get("Decoration", new Example("ENG", "Decoration"), new Example("GER", "Dekoration"))).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                set = true;
                p.closeInventory();
                callback.accept(IconType.DECORATION);
            }
        }.setOption(option));
    }
}
