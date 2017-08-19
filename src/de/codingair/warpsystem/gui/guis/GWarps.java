package de.codingair.warpsystem.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Player.GUI.Anvil.*;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.GUIs.ConfirmGUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.InterfaceListener;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButtonOption;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Action;
import de.codingair.warpsystem.gui.affiliations.ActionIcon;
import de.codingair.warpsystem.gui.affiliations.Category;
import de.codingair.warpsystem.gui.affiliations.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class GWarps extends GUI {
    private Category category;
    private boolean editing;

    private boolean moving = false;
    private ItemStack cursor = null;
    private int oldSlot = -999;
    private ActionIcon cursorIcon = null;

    public GWarps(Player p, Category category, boolean editing) {
        super(p, "§c§l§nWarps§r" + (category != null ? " §8@" + category.getNameWithoutColor() : ""), 54, WarpSystem.getInstance(), false);
        this.category = category;
        this.editing = editing;

        Listener listener;
        Bukkit.getPluginManager().registerEvents(listener = new Listener() {

            @EventHandler
            public void onDrop(PlayerDropItemEvent e) {
                Player p = e.getPlayer();

                if(!p.getName().equals(getPlayer().getName()) || !moving) return;

                if(cursor != null && !cursor.getType().equals(Material.AIR) && cursor.getType().equals(e.getItemDrop().getItemStack().getType())) {
                    e.getItemDrop().remove();
                    HandlerList.unregisterAll(this);
                    cursor = null;
                }
            }

        }, WarpSystem.getInstance());

        addListener(new InterfaceListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(cursor == null) HandlerList.unregisterAll(listener);
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {

            }
        });

        initialize(p);
    }

    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        ItemStack edge = new ItemBuilder(Material.IRON_FENCE).setHideName(true).setHideStandardLore(true).getItem();
        ItemBuilder noneBuilder;

        if(editing) {
            noneBuilder = new ItemBuilder(Material.BARRIER).setHideStandardLore(true)
                    .setName(Lang.get("Edit_Mode_Set_ActionIcon", new Example("ENG", "&3Leftclick: &bSet Warp"), new Example("GER", "&3Linksklick: &bWarp setzen")));

            if(category == null)
                noneBuilder.setLore(Lang.get("Edit_Mode_Set_Category", new Example("ENG", "&3Rightclick: &bSet Category"), new Example("GER", "&3Rechtsklick: &bKategorie setzen")));
        } else {
            noneBuilder = new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).setHideStandardLore(true);
        }

        ItemStack none = noneBuilder.getItem();

        if(p.hasPermission(WarpSystem.PERMISSION_MODIFY)) {
            ItemBuilder builder = new ItemBuilder(Material.NETHER_STAR).setName(Lang.get("Menu_Help", new Example("ENG", "&c&nHelp"), new Example("GER", "&c&nHilfe")));

            if(editing) {
                builder.setLore("§0",
                        Lang.get("Menu_Help_Leftclick_Edit_Mode", new Example("ENG", "&3Leftclick: &bQuit Edit-Mode"), new Example("GER", "&3Linksklick: &bBearbeitungs-Modus verlassen")));
            } else {
                builder.setLore("§0",
                        Lang.get("Menu_Help_Leftclick", new Example("ENG", "&3Leftclick: &bEdit-Mode"), new Example("GER", "&3Linksklick: &bBearbeitungs-Modus")));
            }

            builder.addLore(Lang.get("Menu_Help_Rightclick", new Example("ENG", "&3Rightclick: &bOptions"), new Example("GER", "&3Rechtsklick: &bOptionen")));

            builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            builder.setHideEnchantments(true);

            addButton(new ItemButton(0, builder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(moving) return;

                    if(e.isLeftClick()) {
                        editing = !editing;
                        reinitialize();
                    } else {
                        p.closeInventory();
                        new GConfig(p, category, editing).open();
                    }
                }
            }.setOption(option).setOnlyLeftClick(false));

        } else {
            setItem(0, edge);
        }

        if(category != null) {
            addButton(new ItemButton(0, 5, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back", new Example("ENG", "Back"), new Example("GER", "Zurück"))).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    GWarps.this.category = null;
                    setTitle("§c§l§nWarps§r" + (category != null ? " §8@" + category.getNameWithoutColor() : ""));
                    reinitialize();
                }
            }.setOption(option));
        }

        setItem(8, edge);
        if(category == null) setItem(45, edge);
        setItem(53, edge);

        for(Warp warp : WarpSystem.getInstance().getIconManager().getWarps(category)) {
            if(editing || (!warp.hasPermission() || p.hasPermission(warp.getPermission()))) {
                addToGUI(p, warp);
            }
        }

        if(category == null) {
            for(Category c : WarpSystem.getInstance().getIconManager().getCategories()) {
                if(editing || (!c.hasPermission() || p.hasPermission(c.getPermission()))) {
                    addToGUI(p, c);
                }
            }
        }

        for(int i = 0; i < 54; i++) {
            if(editing) {
                final int slot = i;

                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) {
                    addButton(new ItemButton(i, none.clone()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            if(moving) {
                                if(clickEvent.isLeftClick()) {
                                    cursorIcon.setSlot(clickEvent.getSlot());
                                    clickEvent.setCursor(new ItemStack(Material.AIR));
                                    setMoving(false, clickEvent.getSlot());
                                }

                                return;
                            }

                            ItemStack item = p.getItemInHand();

                            if(item == null || item.getType().equals(Material.AIR)) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand", new Example("ENG", "&cYou have to hold an item!"), new Example("GER", "&cDu musst ein Item halten!")));
                                return;
                            }

                            p.closeInventory();

                            AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                                private String input;

                                @Override
                                public void onClick(AnvilClickEvent e) {
                                    e.setCancelled(true);
                                    e.setClose(false);

                                    if(e.getSlot().equals(AnvilSlot.OUTPUT)) {
                                        playSound(p);
                                        input = e.getInput();

                                        if(input == null) {
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name", new Example("ENG", "&cPlease enter a name."), new Example("GER", "&cBitte gib einen Namen ein.")));
                                            return;
                                        }

                                        input = ChatColor.translateAlternateColorCodes('&', input);

                                        if(clickEvent.isRightClick()) {
                                            StringBuilder builder = new StringBuilder();

                                            boolean color = false;
                                            for(char c : input.toCharArray()) {
                                                builder.append(c);

                                                if(c == '§') color = true;
                                                else if(color) {
                                                    builder.append("§n");
                                                    color = false;
                                                }
                                            }

                                            input = builder.toString();
                                        }

                                        if(clickEvent.isLeftClick()) {
                                            if(WarpSystem.getInstance().getIconManager().existsWarp(input, category)) {
                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists", new Example("ENG", "&cThis name already exists."), new Example("GER", "&cDieser Name existiert bereits.")));
                                                return;
                                            }
                                        } else {
                                            if(WarpSystem.getInstance().getIconManager().existsCategory(input)) {
                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists", new Example("ENG", "&cThis name already exists."), new Example("GER", "&cDieser Name existiert bereits.")));
                                                return;
                                            }
                                        }

                                        input = input.replace("§", "&");

                                        e.setClose(true);
                                    }
                                }

                                @Override
                                public void onClose(AnvilCloseEvent e) {
                                    if(e.isSubmitted())
                                        e.setPost(() -> new GEditIcon(p, category, item, input, slot, clickEvent.isRightClick()).open());
                                    else {
                                        Sound.ITEM_BREAK.playSound(p);
                                        e.setPost(() -> new GWarps(p, category, editing).open());
                                    }
                                }
                            }, new ItemBuilder(Material.PAPER).setName(Lang.get("Name", new Example("ENG", "Name"), new Example("GER", "Name")) + "...").getItem());
                        }
                    }.setOption(option).setOnlyLeftClick(category != null));
                }
            } else {
                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) setItem(i, none);
            }
        }
    }

    private void addToGUI(Player p, ActionIcon icon) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        if(editing || (!icon.hasPermission() || p.hasPermission(icon.getPermission()))) {
            ItemBuilder iconBuilder = new ItemBuilder(icon.getItem()).setName("§b" + (icon instanceof Category ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', icon.getName()));

            if(editing) {
                String command = icon.getAction(Action.RUN_COMMAND) == null ? "-" : icon.getAction(Action.RUN_COMMAND).getValue();
                String permission = icon.getPermission() == null ? "-" : icon.getPermission();

                iconBuilder.addLore("§8------------");
                iconBuilder.addLore("§7" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")) + ": " + command);
                iconBuilder.addLore("§7" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")) + ": " + permission);
                iconBuilder.addLore("§8------------");
                iconBuilder.addLore(Lang.get("Shift_Leftclick_Edit", new Example("ENG", "&7Shift-Leftclick: Move"), new Example("GER", "&7Shift-Linksklick: Bewegen")));
                iconBuilder.addLore(Lang.get("Leftclick_Edit", new Example("ENG", "&7Leftclick: Configure"), new Example("GER", "&7Linksklick: Bearbeiten")));
                iconBuilder.addLore(Lang.get("Rightclick_Delete", new Example("ENG", "&7Rightclick: Delete"), new Example("GER", "&7Rechtsklick: Löschen")));
            }

            addButton(new ItemButton(icon.getSlot(), iconBuilder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(editing) {

                        if(e.isLeftClick()) {
                            if(moving) {
                                icon.setSlot(oldSlot);
                                cursorIcon.setSlot(e.getSlot());
                                e.setCursor(new ItemStack(Material.AIR));
                                setMoving(false, e.getSlot());
                            } else {
                                if(e.isShiftClick()) {
                                    cursorIcon = icon;
                                    cursor = e.getCurrentItem().clone();
                                    e.setCursor(cursor.clone());
                                    e.setCurrentItem(new ItemStack(Material.AIR));
                                    setMoving(true, e.getSlot());
                                } else {
                                    p.closeInventory();
                                    new GEditIcon(p, category, icon).open();
                                }
                            }
                        } else {
                            p.closeInventory();

                            new ConfirmGUI(p,
                                    Lang.get("Delete", new Example("ENG", "&cDelete"), new Example("GER", "&cLöschen")),
                                    "§a" + Lang.get("Yes", new Example("ENG", "Yes"), new Example("GER", "Ja")),
                                    Lang.get("Confirm_Delete", new Example("ENG", "&7Are you sure you want to &cdelete &7this icon?"), new Example("GER", "&7Möchten Sie dieses Symbol wirklich löschen?")),
                                    "§c" + Lang.get("No", new Example("ENG", "No"), new Example("GER", "Nein")),
                                    WarpSystem.getInstance(), new Callback<Boolean>() {
                                @Override
                                public void accept(Boolean accepted) {
                                    if(accepted) {
                                        WarpSystem.getInstance().getIconManager().remove(icon);
                                        p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Deleted", new Example("ENG", "&cThe icon was deleted successfully."), new Example("GER", "&cDas Symbol wurde erfolgreich gelöscht.")));
                                    } else {
                                        p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Not_Deleted", new Example("ENG", "&7The icon was &cnot &7deleted."), new Example("GER", "&7Das Symbol wurde &cnicht &7gelöscht.")));
                                    }

                                    new GWarps(p, category, editing).open();
                                }
                            }, () -> new GWarps(p, category, editing).open()).open();
                        }
                    } else if(e.isLeftClick()) {
                        if(icon instanceof Category) {
                            icon.perform(p, editing, Action.OPEN_CATEGORY);

                            GWarps.this.category = (Category) icon;
                            reinitialize();
                        } else icon.perform(p, editing);
                    }
                }
            }.setOption(option).setOnlyLeftClick(!editing));
        }
    }

    private void setMoving(boolean moving, int slot) {
        if(!moving) {

            if(oldSlot != slot) {
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Success_Icon_Moved", new Example("ENG", "&aThe icon was moved successfully."), new Example("GER", "&aDas Symbol wurde erfolgreich bewegt.")));
            }

            cursor = null;
            cursorIcon = null;
            reinitialize();
        }

        this.moving = moving;
        this.oldSlot = slot;

        if(moving) {
            for(int i = 0; i < getSize(); i++) {
                if(i == slot || getItem(i) == null || getItem(i).getType().equals(Material.AIR)) continue;

                setItem(i, new ItemBuilder(getItem(i)).setLore("", Lang.get("Leftclick_Move_Icon", new Example("ENG", "&3Leftclick: &bMove icon"), new Example("GER", "&3Linksklick: &bSymbol bewegen"))).getItem());
            }
        }
    }
}
