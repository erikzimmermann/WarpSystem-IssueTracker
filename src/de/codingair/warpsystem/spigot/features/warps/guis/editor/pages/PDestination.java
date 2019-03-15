package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.guis.GSimpleWarpList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PDestination extends PMain {
    public PDestination(Player player, PMain main) {
        super(player, main);
    }

    public PDestination(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, item, name, slot, category, isCategory, true);
        super.setDestination(this);
        super.initialize(p);
        initialize(p);
    }

    public PDestination(Player p, Icon icon) {
        super(p, icon, true);
        super.setDestination(this);
        super.initialize(p);
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        Button aB = getButton(1);
        Button fB = getButton(2);
        Button dB = getButton(3);

        aB.setItem(new ItemBuilder(aB.getItem()).removeEnchantments().getItem());
        fB.setItem(new ItemBuilder(fB.getItem()).removeEnchantments().getItem());
        dB.setItem(new ItemBuilder(dB.getItem()).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem());

        if(aB.getLink() == null) aB.setLink(getAppearance());
        if(fB.getLink() == null) fB.setLink(getFunctions());
        if(dB.getLink() != null) dB.setLink(null);

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                WarpAction warp = getIcon().getAction(Action.WARP);
                String name = null;
                if(warp != null && warp.getValue().getType() == DestinationType.SimpleWarp) name = warp.getValue().getId();

                List<String> lore = warp == null ? null : new ArrayList<>();
                if(lore != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("SimpleWarps"))
                        .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name) + "§7'"),
                                "", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                        .addLore(lore)
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    getLast().setClosingForAnvil(true);
                    new GSimpleWarpList(p, new GSimpleWarpList.Listener() {
                        @Override
                        public void onClickOnWarp(String warp, InventoryClickEvent e) {
                            getIcon().addAction(new WarpAction(new Destination(warp, DestinationType.SimpleWarp)));
                            update();

                            getLast().open();
                            getLast().setClosingForAnvil(false);
                        }

                        @Override
                        public void onClose() {
                            getLast().open();
                            getLast().setClosingForAnvil(false);
                        }

                        @Override
                        public String getLeftclickDescription() {
                            return Lang.get("Leftclick_To_Choose");
                        }
                    }).open();
                } else if(e.isRightClick()) {
                    getIcon().removeAction(Action.WARP);
                    if(WarpSystem.getInstance().isOnBungeeCord()) ((SyncButton) getButton(2, 2)).update();
                    update();
                }
            }
        }.setOption(option));

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            addButton(new SyncButton(2, 2) {
                @Override
                public ItemStack craftItem() {
                    WarpAction warp = getIcon().getAction(Action.WARP);
                    String name = null;
                    if(warp != null && warp.getValue().getType() == DestinationType.GlobalWarp) name = warp.getValue().getId();

                    List<String> lore = warp == null ? null : new ArrayList<>();
                    if(lore != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("GlobalWarps"))
                            .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name) + "§7'"),
                                    "", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore)
                            .getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) {
                        getLast().setClosingForAnvil(true);
                        new GGlobalWarpList(p, new GGlobalWarpList.Listener() {
                            @Override
                            public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                                getIcon().addAction(new WarpAction(new Destination(warp, DestinationType.GlobalWarp)));
                                update();

                                getLast().open();
                                getLast().setClosingForAnvil(false);
                            }

                            @Override
                            public void onClose() {
                                getLast().open();
                                getLast().setClosingForAnvil(false);
                            }

                            @Override
                            public String getLeftclickDescription() {
                                return Lang.get("Leftclick_To_Choose");
                            }
                        }).open();
                    } else if(e.isRightClick()) {
                        getIcon().removeAction(Action.WARP);
                        ((SyncButton) getButton(1, 2)).update();
                        update();
                    }
                }
            }.setOption(option));
        }
    }
}
