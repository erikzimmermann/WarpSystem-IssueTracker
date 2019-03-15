package de.codingair.warpsystem.spigot.features.warps.guis.editor;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.pages.PMain;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GEditor extends SimpleGUI {
    public GEditor(Player p, String name, int slot, Icon category, ItemStack item, boolean isCategory) {
        super(p, new LEditor(), new PAppearance(p, item, name, slot, category, isCategory), WarpSystem.getInstance());

//        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.7F, 1F));
        setCancelSound(new SoundData(Sound.ITEM_BREAK, 0.7F, 1F));

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(isClosingByButton() || isClosingForAnvil() || isClosingByOperation()) return;
                Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> new GWarps(p, category, true).open());
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

    public GEditor(Player p, Icon toEdit) {
        super(p, new LEditor(), new PAppearance(p, toEdit), WarpSystem.getInstance());

//        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.7F, 1F));
        setCancelSound(new SoundData(Sound.ITEM_BREAK, 0.7F, 1F));

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(isClosingByButton() || isClosingForAnvil() || isClosingByOperation()) return;
                Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> new GWarps(p, toEdit.getCategory(), true).open());
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
}
