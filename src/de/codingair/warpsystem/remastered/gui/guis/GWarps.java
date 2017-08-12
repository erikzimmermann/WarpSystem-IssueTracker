package de.codingair.warpsystem.remastered.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Player.GUI.Anvil.*;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.GUIs.ConfirmGUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButtonOption;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.remastered.Language.Example;
import de.codingair.warpsystem.remastered.Language.Lang;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.affiliations.*;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GWarps extends GUI {
    private Category category;
    private boolean editing;

    public GWarps(Player p, Category category, boolean editing) {
        super(p, "§c§l§nWarps§r" + (category != null ? " §8@" + category.getName() : ""), 54, WarpSystem.getInstance(), false);
        this.category = category;
        this.editing = editing;

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
                    if(e.isLeftClick()) {
                        new GWarps(p, category, !editing).open();
                    } else {
                        //TODO: Rightclick --> Config (Teleport-Animation, Teleport-Delay, Teleport-Delay-ByPass-Permission, Language, Plugin in edit mode [Player without perm cannot open the GUI])
                        p.sendMessage("Config");
                    }
                }
            }.setOption(option).setOnlyLeftClick(false).setCloseOnClick(true));

        } else {
            setItem(0, edge);
        }

        if(category != null) {
            addButton(new ItemButton(0, 5, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back", new Example("ENG", "Back"), new Example("GER", "Zurück"))).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    new GWarps(p, null, editing).open();
                }
            }.setOption(option).setCloseOnClick(true));
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
                            ItemStack item = p.getItemInHand();

                            if(item == null || item.getType().equals(Material.AIR)) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand", new Example("ENG", "&cYou have to hold an item!"), new Example("GER", "&cDu musst ein Item halten!")));
                                return;
                            }

                            AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                                private String input;

                                @Override
                                public void onClick(AnvilClickEvent e) {
                                    e.setCancelled(true);
                                    e.setClose(false);

                                    if(e.getSlot().equals(AnvilSlot.OUTPUT)) {
                                        input = e.getInput();

                                        if(input == null) {
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name", new Example("ENG", "&cPlease enter a name."), new Example("GER", "&cBitte gib einen Namen ein.")));
                                            return;
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

                                        e.setClose(true);
                                        playSound(p);
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
            ItemBuilder iconBuilder = new ItemBuilder(icon.getItem());

            if(editing) {
                String command = icon.getAction(Action.RUN_COMMAND) == null ? "-" : icon.getAction(Action.RUN_COMMAND).getValue();
                String permission = icon.getPermission() == null ? "-" : icon.getPermission();

                iconBuilder.addLore("§8------------");
                iconBuilder.addLore("§7" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")) + ": " + command);
                iconBuilder.addLore("§7" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")) + ": " + permission);
                iconBuilder.addLore("§8------------");
                iconBuilder.addLore(Lang.get("Leftclick_Edit", new Example("ENG", "&7Leftclick: Configure"), new Example("GER", "&7Linksklick: Bearbeiten")));
                iconBuilder.addLore(Lang.get("Rightclick_Delete", new Example("ENG", "&7Rightclick: Delete"), new Example("GER", "&7Rechtsklick: Löschen")));
            }

            addButton(new ItemButton(icon.getSlot(), iconBuilder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(editing) {
                        //TODO: Shift-Leftclick » Move icon

                        if(e.isLeftClick()) {
                            new GEditIcon(p, category, icon).open();
                        } else {
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
                            }).open();
                        }
                    } else if(e.isLeftClick()) icon.perform(p, editing);
                }
            }.setOption(option).setOnlyLeftClick(false));
        }
    }
}
