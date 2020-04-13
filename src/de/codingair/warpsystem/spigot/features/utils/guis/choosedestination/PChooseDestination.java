package de.codingair.warpsystem.spigot.features.utils.guis.choosedestination;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.guis.GSimpleWarpList;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

        addButton(new SyncButton(2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_PEARL).setName("§b" + Lang.get("SimpleWarps")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                getLast().changeGUI(new GSimpleWarpList(p) {
                    boolean got = false;

                    @Override
                    public void onClick(SimpleWarp warp, ClickType clickType) {
                        got = true;
                        setUseFallbackGUI(false);
                        getPlayer().closeInventory();
                        callback.accept(new Destination(warp.getName(), DestinationType.SimpleWarp));
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        fallBack();
                    }

                    @Override
                    public void buildItemDescription(List<String> lore) {
                        lore.add("");
                        lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                    }
                }, true);
            }
        }.setOption(option));

        addButton(new SyncButton(4) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_EYE).setName("§b" + Lang.get("GlobalWarps")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                getLast().changeGUI(new GGlobalWarpList(player) {
                    boolean got = false;

                    @Override
                    public void onClick(String warp, ClickType clickType) {
                        got = true;
                        setUseFallbackGUI(false);
                        getPlayer().closeInventory();
                        callback.accept(new Destination(warp, DestinationType.GlobalWarp));
                    }

                    @Override
                    public void onClose() {
                        if(got) return;
                        fallBack();
                    }

                    @Override
                    public void buildItemDescription(List<String> lore) {
                        lore.add("");
                        lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                    }
                }, true);
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(6, ClickType.LEFT) {
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
                e.setPost(() -> {
                    if(e.getSubmittedText() == null) getLast().open();
                });
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Server") + "...").getItem();
            }

            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_CHEST).setName("§b" + Lang.get("Server")).getItem();
            }
        }.setOption(option));
    }
}
