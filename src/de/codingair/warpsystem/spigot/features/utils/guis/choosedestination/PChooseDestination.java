package de.codingair.warpsystem.spigot.features.utils.guis.choosedestination;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.guis.GSimpleWarpList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PChooseDestination extends Page {
    private Callback<Destination> callback;

    public PChooseDestination(Player p, String title, Callback<Destination> callback) {
        super(p, title, new LChooseDestination(), false);
        this.callback = callback;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setCloseOnClick(true);

        addButton(new SyncButton(2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_PEARL).setName("§b" + Lang.get("SimpleWarps")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                new GSimpleWarpList(p, new GSimpleWarpList.Listener() {
                    boolean got = false;

                    @Override
                    public void onClickOnWarp(String warp, InventoryClickEvent e) {
                        got = true;
                        p.closeInventory();
                        callback.accept(new Destination(warp, DestinationType.SimpleWarp));
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> getLast().open());
                    }

                    @Override
                    public String getLeftclickDescription() {
                        return "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose");
                    }
                }).open();
            }
        }.setOption(option));

        addButton(new SyncButton(4) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_EYE).setName("§b" + Lang.get("GlobalWarps")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                new GGlobalWarpList(p, new GGlobalWarpList.Listener() {
                    boolean got = false;

                    @Override
                    public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                        got = true;
                        p.closeInventory();
                        callback.accept(new Destination(warp, DestinationType.GlobalWarp));
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> getLast().open());
                    }

                    @Override
                    public String getLeftclickDescription() {
                        return "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose");
                    }
                }).open();
            }
        }.setOption(option));

        addButton(new SyncButton(6) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                        String input = e.getInput();

                        if(input == null) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        e.setClose(true);
                        callback.accept(new Destination(input, DestinationType.Server));
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if(e.getSubmittedText() == null)
                            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> getLast().open());
                    }
                }, new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Server") + "...").getItem());
            }

            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_CHEST).setName("§b" + Lang.get("Server")).getItem();
            }
        }.setOption(option));
    }
}
