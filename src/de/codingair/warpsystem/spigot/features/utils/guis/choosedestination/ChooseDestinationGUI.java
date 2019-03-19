package de.codingair.warpsystem.spigot.features.utils.guis.choosedestination;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.guis.GSimpleWarpList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChooseDestinationGUI extends SimpleGUI {
    private Callback<Destination> callback;

    public ChooseDestinationGUI(Player p, Callback<Destination> callback) {
        this(p, Lang.get("Choose_A_Destination"), callback);
    }

    public ChooseDestinationGUI(Player p, String title, Callback<Destination> callback) {
        super(p, new PChooseDestination(p, title, callback), WarpSystem.getInstance());

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
                if(isClosingForAnvil() || isClosingByButton()) return;
                callback.accept(null);
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

    @Override
    public void open() {
        if(WarpSystem.getInstance().isOnBungeeCord()) super.open();
        else {
            new GSimpleWarpList(getPlayer(), new GSimpleWarpList.Listener() {
                boolean got = false;

                @Override
                public void onClickOnWarp(String warp, InventoryClickEvent e) {
                    got = true;
                    getPlayer().closeInventory();
                    callback.accept(new Destination(warp, DestinationType.SimpleWarp));
                }

                @Override
                public void onClose() {
                    if(got) return;
                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> callback.accept(null));
                }

                @Override
                public String getLeftclickDescription() {
                    return "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose");
                }
            }).open();
        }
    }
}
