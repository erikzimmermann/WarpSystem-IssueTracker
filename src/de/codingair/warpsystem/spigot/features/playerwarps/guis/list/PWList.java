package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Layout;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PWList extends SimpleGUI {
    public PWList(Player p) {
        super(p,
                new PWLayout(27)
                , new PWPage(p, 27)
                , WarpSystem.getInstance(), false);

        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.5F));

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!isClosingForGUI() && getMain().filter.deleteExtraBeforeChangeFilter() && getMain().extra != null) {
                    getMain().extra = null;
                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> open(), 1);
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
    public void open() {
        super.open();
        setOpenSound(null);
    }

    public void updateList() {
        getMain().updateEntries();
    }

    @Override
    public void initialize(Player p) {
        getMain().initialize(p);
        super.initialize(p);
    }

    @Override
    public PWPage getMain() {
        return (PWPage) super.getMain();
    }

    private static class PWLayout extends Layout {
        public PWLayout(int size) {
            super(size);
        }

        @Override
        public void initialize() {
            addLine(7, 0, 7, getSize() / 9 - 1, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem(), true);
        }
    }
}
