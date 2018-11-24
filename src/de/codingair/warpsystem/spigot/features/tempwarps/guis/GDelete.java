package de.codingair.warpsystem.spigot.features.tempwarps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Layout;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.codingapi.tools.time.TimeListener;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GDelete extends SimpleGUI {
    private static TimeList<Player> timeOut = new TimeList<>();
    private TempWarp warp;

    public GDelete(Player p, TempWarp warp) {
        super(p, new StandardLayout(), new Page(p, Lang.get("TempWarp_Delete"), new Layout(27) {
            @Override
            public void initialize() {
                this.setItem(4, new ItemBuilder(XMaterial.NETHER_STAR).setText(Lang.get("TempWarp_Confirm_Delete").replace("%COINS%", getRefund(warp) + ""), 100).getItem());
            }
        }) {
            @Override
            public void initialize(Player p) {
                ItemButtonOption option = new ItemButtonOption();
                option.setCloseOnClick(false);
                option.setClickSound(Sound.CLICK.bukkitSound());
                option.setOnlyLeftClick(true);

                this.addButton(new Button(3, 2, new ItemBuilder(XMaterial.LIME_TERRACOTTA).setText(Lang.get("No_Keep")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Deleted_Cancel").replace("%TEMP_WARP%", warp.getName()));
                    }
                }.setOption(option).setCloseOnClick(true));

                this.addButton(new Button(5, 2, new ItemBuilder(XMaterial.RED_TERRACOTTA).setText(Lang.get("Yes_Delete")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        TempWarpManager.getManager().delete(warp);

                        int costs = getRefund(warp);
                        AdapterType.getActive().setMoney(p, AdapterType.getActive().getMoney(p) + costs);

                        if(costs > 0) {
                            p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Deleted_Refund").replace("%TEMP_WARP%", warp.getName()).replace("%COINS%", costs + ""));
                        } else {
                            p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Deleted").replace("%TEMP_WARP%", warp.getName()));
                        }
                    }
                }.setOption(option).setCloseOnClick(true));
            }
        }, WarpSystem.getInstance());

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) { }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) { }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                timeOut.remove(p);
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) { }

            @Override
            public void onMoveToTopInventory(ItemStack item, int oldRawSlot, List<Integer> newRawSlots) { }

            @Override
            public void onCollectToCursor(ItemStack item, List<Integer> oldRawSlots, int newRawSlot) { }
        });

        this.warp = warp;
    }

    public void cancel() {
        getPlayer().sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Delete_TimeOut"));
        getPlayer().closeInventory();
    }

    @Override
    public void open() {
        super.open();
        timeOut.add(getPlayer(), 30);

        timeOut.addListener(new TimeListener() {
            @Override
            public void onRemove(Object item) {
                if(item instanceof Player && item.equals(getPlayer())) {
                    cancel();
                    timeOut.removeListener(this);
                }
            }

            @Override
            public void onTick(Object item, int timeLeft) {
                setItem(4, new ItemBuilder(XMaterial.NETHER_STAR).setText(Lang.get("TempWarp_Confirm_Delete").replace("%COINS%", getRefund(warp) + ""), 100).getItem());
                getPlayer().updateInventory();
            }
        });
    }
    
    private static int getRefund(TempWarp warp) {
        return TempWarpManager.getManager().isRefund() ? warp.getRemainingCosts() : 0;
    }
}
