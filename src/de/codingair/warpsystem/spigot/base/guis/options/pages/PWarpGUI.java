package de.codingair.warpsystem.spigot.base.guis.options.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpGUIOptions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PWarpGUI extends PageItem {
    private WarpGUIOptions options;

    public PWarpGUI(Player p, WarpGUIOptions options) {
        super(p, Editor.TITLE_COLOR + "WarpSystem§r §7- §6" + Lang.get("Config"), new ItemBuilder(XMaterial.ENDER_EYE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpGUI")).getItem(), false);

        this.options = options;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(!options.getEnabled().getValue() ? XMaterial.ROSE_RED : XMaterial.LIME_DYE)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Status"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (!options.getEnabled().getValue() ?
                                        "§c" + Lang.get("Disabled") :
                                        "§a" + Lang.get("Enabled")), "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && !e.isShiftClick()) {
                    options.getEnabled().setValue(!options.getEnabled().getValue());
                    update();
                    getLast().updateControllButtons();
                }
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.BOOK)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Message"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (!options.getSendTeleportMessage().getValue() ?
                                        "§c" + Lang.get("Disabled") :
                                        "§a" + Lang.get("Enabled")), "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && !e.isShiftClick()) {
                    options.getSendTeleportMessage().setValue(!options.getSendTeleportMessage().getValue());
                    update();
                }
            }
        }.setOption(option));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.CHEST)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Size"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("User") + ": §7" + options.getUserSize().getValue() + " §8| " + Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Admin") + ": §7" + options.getAdminSize().getValue() + " §8(§7" + Lang.get("Shift") + "§8)", "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Reduce") + " §8(§7" + Lang.get("Shift") + "§8)",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Enlarge") + " §8(§7" + Lang.get("Shift") + "§8)")
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    if(e.isShiftClick()) {
                        //Admin verringern
                        int size = options.getAdminSize().getValue();
                        size -= 9;
                        if(size == 0) size = 9;
                        options.getAdminSize().setValue(size);
                    } else {
                        //User verringern
                        int size = options.getUserSize().getValue();
                        size -= 9;
                        if(size == 0) size = 9;
                        options.getUserSize().setValue(size);
                    }
                } else if(e.isRightClick()) {
                    if(e.isShiftClick()) {
                        //Admin vergrößern
                        int size = options.getAdminSize().getValue();
                        size += 9;
                        if(size == 63) size = 54;
                        options.getAdminSize().setValue(size);
                    } else {
                        //User vergrößern
                        int size = options.getUserSize().getValue();
                        size += 9;
                        if(size == 63) size = 54;
                        options.getUserSize().setValue(size);
                    }
                }

                update();
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(4, 2, ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NAME_TAG)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Title"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("User") + ": §7\"§r" + ChatColor.translateAlternateColorCodes('&', options.getUserStandardTitle().getValue()) + "§7\" §8(§7\"" + ChatColor.translateAlternateColorCodes('&', options.getUserPageTitle().getValue()) + "§7\"§8)",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Change") + " §8(§7" + Lang.get("Shift") + "§8)", "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Admin") + ": §7\"§r" + ChatColor.translateAlternateColorCodes('&', options.getAdminStandardTitle().getValue()) + "§7\" §8(§7\"" + ChatColor.translateAlternateColorCodes('&', options.getAdminPageTitle().getValue()) + "§7\"§8)",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Change") + " §8(§7" + Lang.get("Shift") + "§8)")
                        .getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(e.getSlot() != AnvilSlot.OUTPUT) return;
                if(e.getInput() == null) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Something"));
                    return;
                }

                switch(getLastTrigger()) {
                    case LEFT: {
                        options.getUserStandardTitle().setValue(e.getInput());
                        break;
                    }

                    case SHIFT_LEFT: {
                        options.getUserPageTitle().setValue(e.getInput());
                        break;
                    }

                    case RIGHT: {
                        options.getAdminStandardTitle().setValue(e.getInput());
                        break;
                    }

                    default: {
                        options.getAdminPageTitle().setValue(e.getInput());
                        break;
                    }
                }

                update();
                e.setClose(true);
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                String title;
                switch(trigger) {
                    case LEFT: {
                        title = options.getUserStandardTitle().getValue();
                        break;
                    }

                    case SHIFT_LEFT: {
                        title = options.getUserPageTitle().getValue();
                        break;
                    }

                    case RIGHT: {
                        title = options.getAdminStandardTitle().getValue();
                        break;
                    }

                    default: {
                        title = options.getAdminPageTitle().getValue();
                        break;
                    }
                }

                return new ItemBuilder(XMaterial.NAME_TAG).setName(title).getItem();
            }
        }.setOption(option));
    }
}
