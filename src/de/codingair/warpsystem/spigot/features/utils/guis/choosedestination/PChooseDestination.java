package de.codingair.warpsystem.spigot.features.utils.guis.choosedestination;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.destinations.Destination;
import de.codingair.warpsystem.spigot.base.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.guis.list.GHiddenWarpList;
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

        addButton(new SyncButton(1) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.CHEST).setName("§b" + Lang.get("WarpGUI")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                new GWarps(p, null, false, new GUIListener() {
                    boolean got = false;

                    @Override
                    public String getTitle() {
                        return PChooseDestination.this.getTitle();
                    }

                    @Override
                    public Task onClickOnIcon(Icon icon, boolean editing) {
                        if(icon != null) {
                            if(icon instanceof Warp) {
                                got = true;
                                p.closeInventory();
                                callback.accept(new Destination(((Warp) icon).getIdentifier(), DestinationType.WarpIcon));
                            } else if(icon instanceof GlobalWarp) {
                                got = true;
                                p.closeInventory();
                                callback.accept(new Destination(icon.getName(), DestinationType.GlobalWarpIcon));
                            }
                        }

                        return null;
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> getLast().open());
                    }
                }, false, DecoIcon.class).open();
            }
        }.setOption(option));

        addButton(new SyncButton(4) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_CHEST).setName("§b" + Lang.get("GlobalWarps")).getItem();
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
                        return Lang.get("Leftclick_To_Choose");
                    }
                }).open();
            }
        }.setOption(option));

        addButton(new SyncButton(7) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_CHEST).setName("§b" + Lang.get("HiddenWarps")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                new GHiddenWarpList(p, new GHiddenWarpList.Listener() {
                    boolean got = false;

                    @Override
                    public void onClickOnWarp(String warp, InventoryClickEvent e) {
                        got = true;
                        p.closeInventory();
                        callback.accept(new Destination(warp, DestinationType.HiddenWarp));
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> getLast().open());
                    }

                    @Override
                    public String getLeftclickDescription() {
                        return Lang.get("Leftclick_To_Choose");
                    }
                }).open();
            }
        }.setOption(option));
    }
}
