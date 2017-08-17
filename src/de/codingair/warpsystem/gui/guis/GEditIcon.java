package de.codingair.warpsystem.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Player.GUI.Anvil.*;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.InterfaceListener;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButtonOption;
import de.CodingAir.v1_6.CodingAPI.Serializable.SerializableLocation;
import de.CodingAir.v1_6.CodingAPI.Server.Environment;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.Language.Example;
import de.codingair.warpsystem.Language.Lang;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class GEditIcon extends GUI {
    private ItemStack item;
    private String name;
    private String permission = null;
    private String command = null;

    private int slot;
    private Category category;

    private boolean isCategory;
    private ActionIcon editing;

    private boolean quit = true;

    public GEditIcon(Player p, Category category, ItemStack item, String name, int slot, boolean isCategory) {
        super(p, "§c§l§n" + Lang.get("Item_Editing", new Example("ENG", "Item-Editing"), new Example("GER", "Item-Bearbeitung")), 45, WarpSystem.getInstance(), false);

        this.item = item;
        this.name = name;

        this.slot = slot;
        this.category = category;

        this.isCategory = isCategory;

        addListener(new InterfaceListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {
            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {
            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!quit) return;

                Sound.ITEM_BREAK.playSound(p);
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new GWarps(p, category, true).open(), 1L);
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {
            }
        });

        initialize(p);
    }

    public GEditIcon(Player p, Category category, ActionIcon editing) {
        super(p, "§c§l§n" + Lang.get("Item_Editing", new Example("ENG", "Item-Editing"), new Example("GER", "Item-Bearbeitung")), 45, WarpSystem.getInstance(), false);
        this.editing = editing;

        this.item = editing.getItem();
        this.name = editing.getName();

        this.slot = editing.getSlot();
        this.category = category;
        this.command = editing.getAction(Action.RUN_COMMAND) == null ? null : editing.getAction(Action.RUN_COMMAND).getValue();
        this.permission = editing.getPermission();

        this.isCategory = editing instanceof Category;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        this.item = new ItemBuilder(this.item).setName("§b" + (isCategory ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', name)).setHideStandardLore(true)
                .setLore("§8------------", "", Lang.get("Change_Name", new Example("ENG", "&8> Click here to change the name."), new Example("GER", "&8> Klicke hier um den Namen zu ändern.")))
                .getItem();

        ItemStack leaves = new ItemBuilder(Material.LEAVES).setName("§0").getItem();
        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setName("§0").getItem();
        ItemStack bars = new ItemBuilder(Material.IRON_FENCE).setName("§0").getItem();

        ItemStack cancel = new ItemBuilder(Material.WOOL).setColor(DyeColor.RED).setName("§c" + Lang.get("Cancel", new Example("ENG", "Cancel"), new Example("GER", "Abbrechen"))).getItem();
        ItemStack ready = new ItemBuilder(Material.WOOL).setColor(DyeColor.LIME).setName("§a" + Lang.get("Ready", new Example("ENG", "Ready"), new Example("GER", "Fertig"))).getItem();

        ItemStack sparkle = new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle", new Example("ENG", "Sparkle"), new Example("GER", "Funkeln")))
                .setLore("", Lang.get("Leftclick_Enable", new Example("ENG", "&3Leftclick: &aEnable"), new Example("GER", "&3Linksklick: &aAktivieren")))
                .getItem();

        ItemStack lore = new ItemBuilder(Material.PAPER).setName("§6§n" + Lang.get("Description", new Example("ENG", "Description"), new Example("GER", "Beschreibung")))
                .setLore("", Lang.get("Leftclick_Add_Line", new Example("ENG", "&3Leftclick: &aAdd line"), new Example("GER", "&3Linksklick: &aZeile hinzufügen")),
                        Lang.get("Rightclick_Reset_Lines", new Example("ENG", "&3Rightclick: &cReset lines"), new Example("GER", "&3Rechtsklick: &cZeilen zurücksetzen")))
                .getItem();

        ItemStack command = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")))
                .setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + (this.command == null ? "-" : this.command), "",
                        this.command == null ? Lang.get("Leftclick_Add", new Example("ENG", "&3Leftclick: &aAdd"), new Example("GER", "&3Linksklick: &aHinzufügen"))
                                : Lang.get("Leftclick_Remove", new Example("ENG", "&3Leftclick: &cRemove"), new Example("GER", "&3Linksklick: &cEntfernen")))
                .getItem();

        ItemStack permissionIcon = new ItemBuilder(Material.EYE_OF_ENDER).setName("§6§n" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")))
                .setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + (this.permission == null ? "-" : this.permission), "",
                        this.permission == null ? Lang.get("Leftclick_Add", new Example("ENG", "&3Leftclick: &aAdd"), new Example("GER", "&3Linksklick: &aHinzufügen"))
                                : Lang.get("Leftclick_Remove", new Example("ENG", "&3Leftclick: &cRemove"), new Example("GER", "&3Linksklick: &cEntfernen")))
                .getItem();

        //decoration

        setItem(0, 0, leaves);
        setItem(1, 0, leaves);
        setItem(7, 0, leaves);
        setItem(8, 0, leaves);
        setItem(0, 4, leaves);
        setItem(1, 4, leaves);
        setItem(7, 4, leaves);
        setItem(8, 4, leaves);

        setItem(2, 0, glass);
        setItem(6, 0, glass);
        setItem(0, 1, glass);
        setItem(1, 1, glass);
        setItem(7, 1, glass);
        setItem(8, 1, glass);
        setItem(2, 4, glass);
        setItem(6, 4, glass);
        setItem(0, 3, glass);
        setItem(1, 3, glass);
        setItem(7, 3, glass);
        setItem(8, 3, glass);

        setItem(3, 0, bars);
        setItem(5, 0, bars);
        setItem(3, 1, bars);
        setItem(4, 1, bars);
        setItem(5, 1, bars);
        setItem(3, 4, bars);
        setItem(4, 4, bars);
        setItem(5, 4, bars);

        //functions

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());

        //icon
        final ItemButton iconButton = new ItemButton(4, 0, item) {
            @Override
            public void onClick(InventoryClickEvent e) {
                quit = false;
                p.closeInventory();

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

                            input = ChatColor.translateAlternateColorCodes('&', input);

                            if(isCategory) {
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

                            if(!isCategory) {
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
                            playSound(p);
                        }
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if(e.isSubmitted()) {
                            name = input;
                            item = new ItemBuilder(item).setName("§b" + (isCategory ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', name)).getItem();
                            setItem(item);
                        } else Sound.ITEM_BREAK.playSound(p);


                        e.setPost(GEditIcon.this::open);
                        quit = true;
                    }
                }, new ItemBuilder(item).setHideStandardLore(true).removeEnchantments().setName(name).getItem());
            }
        }.setOption(option).setOnlyLeftClick(true);

        addButton(iconButton);


        //cancel
        addButton(new ItemButton(0, 2, cancel) {
            @Override
            public void onClick(InventoryClickEvent e) {
                new GWarps(p, category, true).open();
            }
        }.setOption(option.clone()).setOnlyLeftClick(true).setCloseOnClick(true).setClickSound(null));

        //ready
        addButton(new ItemButton(8, 2, ready) {
            @Override
            public void onClick(InventoryClickEvent e) {
                quit = false;

                ItemBuilder builder = new ItemBuilder(item);
                builder.removeLore(builder.getLore().size() - 3, builder.getLore().size());
                item = builder.getItem();

                if(editing != null) {
                    editing.setName(name);
                    editing.setItem(item);
                    editing.setPermission(permission);
                    editing.removeAction(Action.RUN_COMMAND);

                    if(GEditIcon.this.command != null) editing.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));

                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Configured", new Example("ENG", "&aYou have configured the icon successfully."), new Example("GER", "&aDu hast das Symbol erfolgreich bearbeitet")));
                } else {
                    if(isCategory) {
                        Category category = new Category(name, item, slot, permission);

                        if(GEditIcon.this.command != null) category.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));

                        WarpSystem.getInstance().getIconManager().getCategories().add(category);

                        p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Category", new Example("ENG", "&aYou have created a &bcategory &asuccessfully."), new Example("GER", "&aDu hast erfolgreich eine &bKategorie &aerstellt.")));
                    } else {
                        Warp warp = new Warp(name, item, slot, permission, category, new ActionObject(Action.TELEPORT_TO_WARP, new SerializableLocation(p.getLocation())));
                        WarpSystem.getInstance().getIconManager().getWarps().add(warp);

                        p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Warp", new Example("ENG", "&aYou have created a &bwarp &asuccessfully."), new Example("GER", "&aDu hast erfolgreich ein &bWarp &aerstellt.")));
                    }

                    Environment.playRandomFireworkEffect(p.getLocation());
                }

                Sound.LEVEL_UP.playSound(p);
                p.closeInventory();
                new GWarps(p, category, true).open();
            }
        }.setOption(option).setOnlyLeftClick(true));

        //sparkle
        addButton(new ItemButton(2, 3, sparkle) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(item.getEnchantments().size() == 0) {
                    item = new ItemBuilder(item).setHideStandardLore(true).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem();

                    setItem(new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle", new Example("ENG", "Sparkle"), new Example("GER", "Funkeln")))
                            .setLore("", Lang.get("Leftclick_Disable", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")))
                            .getItem());
                } else {
                    ItemBuilder builder = new ItemBuilder(item).setHideStandardLore(true);
                    builder.getEnchantments().clear();
                    item = builder.getItem();

                    setItem(new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle", new Example("ENG", "Sparkle"), new Example("GER", "Funkeln")))
                            .setLore("", Lang.get("Leftclick_Enable")).getItem());
                }

                iconButton.setItem(item);
            }
        }.setOption(option).setOnlyLeftClick(true));

        //lore
        addButton(new ItemButton(3, 2, lore) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(e.isLeftClick()) {
                    quit = false;
                    p.closeInventory();

                    AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            e.setCancelled(true);
                            e.setClose(false);

                            if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                            String input = e.getInput();

                            if(input == null) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Lore", new Example("ENG", "&cPlease enter a lore."), new Example("GER", "&cBitte gib eine Beschreibung ein.")));
                                return;
                            }

                            e.setClose(true);
                            playSound(p);

                            ItemBuilder builder = new ItemBuilder(item).setHideStandardLore(true).setHideEnchantments(true);
                            builder.addLore(builder.getLore().size() - 3, ChatColor.translateAlternateColorCodes('&', input));

                            item = builder.getItem();
                            iconButton.setItem(item);
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(!e.isSubmitted()) Sound.ITEM_BREAK.playSound(p);

                            e.setPost(GEditIcon.this::open);
                            quit = true;
                        }
                    }, new ItemBuilder(Material.PAPER).setName(Lang.get("Line", new Example("ENG", "Line"), new Example("GER", "Linie")) + "...").getItem());
                } else {
                    item = new ItemBuilder(item).setHideStandardLore(true).setHideEnchantments(true).removeLore()
                            .setLore("§8------------", "", Lang.get("Change_Name", new Example("ENG", "&8»Click here to change the name."), new Example("GER", "&8»Klicke hier um den Namen zu ändern.")))
                            .getItem();
                    iconButton.setItem(item);
                }
            }
        }.setOption(option));

        //commands
        addButton(new ItemButton(5, 2, command) {
            @Override
            public void onClick(InventoryClickEvent e) {

                if(GEditIcon.this.command != null) {
                    GEditIcon.this.command = null;

                    ItemBuilder commandBuilder = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")));
                    commandBuilder.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7-");
                    commandBuilder.addLore("", Lang.get("Leftclick_Add", new Example("ENG", "&3Leftclick: &aAdd"), new Example("GER", "&3Linksklick: &aHinzufügen")));

                    ItemStack command = commandBuilder.getItem();
                    setItem(command);
                } else {
                    quit = false;
                    p.closeInventory();

                    AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            e.setCancelled(true);
                            e.setClose(false);

                            if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                            String input = e.getInput();

                            if(input == null) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Command", new Example("ENG", "&cPlease enter a command."), new Example("GER", "&cBitte gib einen Befehl ein.")));
                                return;
                            }

                            ItemBuilder commandBuilder = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")));
                            commandBuilder.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + input);
                            commandBuilder.addLore("", Lang.get("Leftclick_Remove", new Example("ENG", "&3Leftclick: &cRemove"), new Example("GER", "&3Linksklick: &cEntfernen")));

                            ItemStack command = commandBuilder.getItem();
                            setItem(command);

                            GEditIcon.this.command = input;

                            e.setClose(true);
                            playSound(p);
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(!e.isSubmitted()) Sound.ITEM_BREAK.playSound(p);

                            e.setPost(GEditIcon.this::open);
                            quit = true;
                        }
                    }, new ItemBuilder(Material.REDSTONE).setName(Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")) + "...").getItem());
                }
            }
        }.setOption(option).setOnlyLeftClick(true));

        //permission
        addButton(new ItemButton(6, 3, permissionIcon) {
            @Override
            public void onClick(InventoryClickEvent e) {

                if(permission != null) {
                    permission = null;

                    ItemBuilder permissionIconBuilder = new ItemBuilder(Material.EYE_OF_ENDER).setName("§6§n" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")))
                            .setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7-", "", Lang.get("Leftclick_Add", new Example("ENG", "&3Leftclick: &aAdd"), new Example("GER", "&3Linksklick: &aHinzufügen")));

                    ItemStack permissionIcon = permissionIconBuilder.getItem();
                    setItem(permissionIcon);
                } else {
                    quit = false;
                    p.closeInventory();

                    AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            e.setCancelled(true);
                            e.setClose(false);

                            if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                            String input = e.getInput();

                            if(input == null) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Command", new Example("ENG", "&cPlease enter a command."), new Example("GER", "&cBitte gib einen Befehl ein.")));
                                return;
                            }

                            ItemBuilder permissionIconBuilder = new ItemBuilder(Material.EYE_OF_ENDER).setName("§6§n" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")));
                            permissionIconBuilder.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + input);
                            permissionIconBuilder.addLore("", Lang.get("Leftclick_Remove", new Example("ENG", "&3Leftclick: &cRemove"), new Example("GER", "&3Linksklick: &cEntfernen")));

                            ItemStack permissionIcon = permissionIconBuilder.getItem();
                            setItem(permissionIcon);

                            permission = input;
                            e.setClose(true);
                            playSound(p);
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(!e.isSubmitted()) Sound.ITEM_BREAK.playSound(p);

                            e.setPost(GEditIcon.this::open);
                            quit = true;
                        }
                    }, new ItemBuilder(Material.EYE_OF_ENDER).setName(Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")) + "...").getItem());
                }
            }
        }.setOption(option).setOnlyLeftClick(true));
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public Category getCategory() {
        return category;
    }

    public int getSlot() {
        return slot;
    }

    public String getPermission() {
        return permission;
    }

    public ActionIcon getEditing() {
        return editing;
    }

    public String getCommand() {
        return command;
    }
}
