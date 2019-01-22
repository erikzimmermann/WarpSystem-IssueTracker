package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.InterfaceListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.*;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GEditIcon extends GUI {
    private ItemStack item;
    private String name;
    private String permission = null;
    private String command = null;
    private double costs = 0;
    private boolean disabled = false;
    private List<String> mainDescription = new ArrayList<>();

    private int slot;
    private Category category;

    private boolean isCategory;
    private Icon editing;

    private boolean quit = true;

    private IconType type;
    private String extra;

    public GEditIcon(Player p, Category category, ItemStack item, String name, int slot, IconType type) {
        this(p, category, item, name, slot, type, null);
    }

    public GEditIcon(Player p, Category category, ItemStack item, String name, int slot, IconType type, String extra) {
        super(p, "§c§l§n" + Lang.get("Item_Editing"), 45, WarpSystem.getInstance(), false);

        name = (name == null ? null : ChatColor.translateAlternateColorCodes('&', name));
        if(type.equals(IconType.CATEGORY) && name != null) name = ChatColor.UNDERLINE + name;

                this.item = new ItemBuilder(item).setName(name).setHideStandardLore(true).getItem();
        this.name = name;

        this.slot = slot;
        this.category = category;
        this.type = type;
        this.isCategory = this.type.equals(IconType.CATEGORY);
        this.extra = extra;

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

    public GEditIcon(Player p, Category category, Icon editing) {
        super(p, "§c§l§n" + Lang.get("Item_Editing"), 45, WarpSystem.getInstance(), false);
        this.editing = editing;

        this.item = editing.getItem();
        this.name = editing.getName();

        this.slot = editing.getSlot();
        this.category = category;

        if(editing instanceof ActionIcon) {
            this.command = ((ActionIcon) editing).getAction(Action.RUN_COMMAND) == null ? null : ((ActionIcon) editing).getAction(Action.RUN_COMMAND).getValue();
            this.permission = ((ActionIcon) editing).getPermission();
            this.costs = ((ActionIcon) editing).getAction(Action.PAY_MONEY) == null ? 0.0 : ((ActionIcon) editing).getAction(Action.PAY_MONEY).getValue();
        }

        this.disabled = this.editing.isDisabled();

        this.isCategory = editing instanceof Category;
        this.type = editing.getType();

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

    private void setupMainIcon() {
        if(mainDescription.isEmpty()) {
            mainDescription.add("§8------------");
            mainDescription.add("");

            switch(this.type) {
                case CATEGORY:
                case WARP:
                case GLOBAL_WARP:
                    mainDescription.add(Lang.get("Change_Name_Leftclick"));
                default:
                    mainDescription.add(Lang.get("Change_Name_Rightclick"));
                    break;
            }
        }

        ItemBuilder builder = new ItemBuilder(item);
        builder.addText(mainDescription);

        this.item = builder.getItem();
    }

    @Override
    public void initialize(Player p) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        setupMainIcon();

        ItemStack leaves = new ItemBuilder(XMaterial.OAK_LEAVES).setName("§0").getItem();
        ItemStack glass = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("§0").getItem();
        ItemStack bars = new ItemBuilder(XMaterial.IRON_BARS).setName("§0").getItem();

        ItemStack cancel = new ItemBuilder(XMaterial.RED_WOOL).setName("§c" + Lang.get("Cancel")).getItem();
        ItemStack ready = new ItemBuilder(XMaterial.LIME_WOOL).setName("§a" + Lang.get("Ready")).getItem();

        ItemStack sparkle;
        if(this.item.getEnchantments().size() == 0) {
            sparkle = new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                    .setLore("", Lang.get("Leftclick_Enable"))
                    .getItem();
        } else {
            sparkle = new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                    .setLore("", Lang.get("Leftclick_Disable"))
                    .getItem();
        }

        ItemStack lore = new ItemBuilder(Material.PAPER).setName("§6§n" + Lang.get("Description"))
                .setLore("", Lang.get("Leftclick_Add_Line"),
                        Lang.get("Rightclick_Reset_Lines"))
                .getItem();

        ItemStack command = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command"))
                .setLore("§8" + Lang.get("Current") + ": §7" + (this.command == null ? "-" : this.command), "",
                        this.command == null ? Lang.get("Leftclick_Add")
                                : Lang.get("Leftclick_Remove"))
                .getItem();

        ItemStack permissionIcon = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Permission"))
                .setLore("§8" + Lang.get("Current") + ": §7" + (this.permission == null ? "-" : this.permission), "",
                        this.permission == null ? Lang.get("Leftclick_Add")
                                : Lang.get("Leftclick_Remove"))
                .getItem();

        ItemStack costsIcon = new ItemBuilder(Material.GOLD_NUGGET).setName("§6§n" + Lang.get("Costs"))
                .setLore("§8" + Lang.get("Current") + ": §7" + costs + " " + Lang.get("Coins"), "",
                        Lang.get("Leftclick_Set"))
                .getItem();

        ItemStack disableIcon = new ItemBuilder(this.disabled ? XMaterial.ROSE_RED : XMaterial.LIME_DYE)
                .setName("§6§n" + Lang.get("Status") + ": " + (this.disabled ?
                        Lang.get("Disabled") :
                        Lang.get("Enabled")))
                .setLore("",
                        this.disabled ? Lang.get("Leftclick_Enable_This_Icon") :
                                Lang.get("Disable_This_Icon"))
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
                if(e.getClick().isLeftClick()) {
                    switch(type) {
                        case DECORATION:
                            return;
                    }

                    Sound.CLICK.playSound(getPlayer());

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
                                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                    return;
                                }

                                if(input.contains("@")) {
                                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Correct_Name"));
                                    return;
                                }

                                input = ChatColor.translateAlternateColorCodes('&', input);

                                switch(type) {
                                    case WARP:
                                        if(manager.existsWarp(input, category)) {
                                            if(editing == null || !editing.getNameWithoutColor().equals(Color.removeColor(input))) {
                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                                return;
                                            }
                                        }
                                        break;

                                    case CATEGORY:
                                        if(manager.existsCategory(input)) {
                                            if(editing == null || !editing.getNameWithoutColor().equals(Color.removeColor(input))) {
                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                                return;
                                            }
                                        }
                                        break;

                                    case GLOBAL_WARP:
                                        if(manager.existsGlobalWarp(input)) {
                                            if(editing == null || !editing.getNameWithoutColor().equals(Color.removeColor(input))) {
                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                                return;
                                            }
                                        }
                                        break;
                                }

                                input = input.replace("§", "&");

                                e.setClose(true);
                                Sound.CLICK.playSound(getPlayer());
                            }
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(e.isSubmitted()) {
                                name = input == null ? null : (isCategory ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', input);
                                item = new ItemBuilder(item).setName(name).getItem();
                                setItem(item);
                            } else Sound.ITEM_BREAK.playSound(p);


                            e.setPost(GEditIcon.this::open);
                            quit = true;
                        }
                    }, new ItemBuilder(item).setHideStandardLore(true).removeEnchantments().setName(name.replace("§", "&")).getItem());
                } else if(e.getClick().isRightClick()) {
                    Sound.CLICK.playSound(getPlayer());
                    ItemStack item = p.getInventory().getItem(p.getInventory().getHeldItemSlot());

                    if(item == null || item.getType().equals(Material.AIR)) {
                        p.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand"));
                        return;
                    }

                    GEditIcon.this.item.setType(item.getType());
                    GEditIcon.this.item.setData(item.getData());
                    GEditIcon.this.item.setDurability(item.getDurability());
                    GEditIcon.this.item.setAmount(item.getAmount());

                    setItem(GEditIcon.this.item);
                    p.updateInventory();
                }
            }
        }.setOption(option).setClickSound(null);

        addButton(iconButton);


        //cancel
        addButton(new ItemButton(0, 2, cancel) {
            @Override
            public void onClick(InventoryClickEvent e) {
                new GWarps(p, category, true).open();
            }
        }.setOption(option.clone()).setOnlyLeftClick(true).setCloseOnClick(true).setClickSound(Sound.ITEM_BREAK.bukkitSound()));

        //ready
        addButton(new ItemButton(8, 2, ready) {
            @Override
            public void onClick(InventoryClickEvent e) {
                quit = false;

                ItemBuilder builder = new ItemBuilder(item);
                builder.removeText(mainDescription);
                item = builder.getItem();

                if(editing != null) {
                    editing.setName(name);
                    editing.setItem(item);
                    editing.setDisabled(disabled);

                    if(editing instanceof ActionIcon) {
                        ((ActionIcon) editing).setPermission(permission);
                        ((ActionIcon) editing).removeAction(Action.RUN_COMMAND);
                        if(GEditIcon.this.command != null) ((ActionIcon) editing).addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));

                        ((ActionIcon) editing).removeAction(Action.PAY_MONEY);
                        if(GEditIcon.this.costs > 0) ((ActionIcon) editing).addAction(new ActionObject(Action.PAY_MONEY, GEditIcon.this.costs));
                    }

                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Configured"));
                } else {
                    switch(type) {
                        case WARP:
                            Warp warp = new Warp(name, item, slot, permission, category, new ActionObject(Action.TELEPORT_TO_WARP, new SerializableLocation(p.getLocation())));
                            if(GEditIcon.this.command != null) warp.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));
                            if(GEditIcon.this.costs > 0) warp.addAction(new ActionObject(Action.PAY_MONEY, GEditIcon.this.costs));
                            warp.setDisabled(disabled);

                            manager.getWarps().add(warp);

                            p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Warp"));
                            break;

                        case CATEGORY:
                            Category category = new Category(name, item, slot, permission);
                            if(GEditIcon.this.command != null) category.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));
                            if(GEditIcon.this.costs > 0) category.addAction(new ActionObject(Action.PAY_MONEY, GEditIcon.this.costs));
                            category.setDisabled(disabled);

                            manager.getCategories().add(category);

                            p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Category"));
                            break;

                        case GLOBAL_WARP:
                            GlobalWarp gWarp = new GlobalWarp(name, item, slot, permission, GEditIcon.this.category, new ActionObject(Action.SWITCH_SERVER, extra));
                            if(GEditIcon.this.command != null) gWarp.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));
                            if(GEditIcon.this.costs > 0) gWarp.addAction(new ActionObject(Action.PAY_MONEY, GEditIcon.this.costs));
                            gWarp.setDisabled(disabled);

                            manager.getGlobalWarps().add(gWarp);

                            p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_GlobalWarp"));
                            break;

                        case DECORATION:
                            DecoIcon deco = new DecoIcon(name, item, slot, permission, GEditIcon.this.category);
                            if(GEditIcon.this.command != null) deco.addAction(new ActionObject(Action.RUN_COMMAND, GEditIcon.this.command));
                            if(GEditIcon.this.costs > 0) deco.addAction(new ActionObject(Action.PAY_MONEY, GEditIcon.this.costs));
                            manager.getDecoIcons().add(deco);
                            deco.setDisabled(disabled);

                            p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Decoration"));
                            break;

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
                if(iconButton.getItem().getEnchantments().size() == 0) {
                    item = new ItemBuilder(item).setHideStandardLore(true).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem();

                    setItem(new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                            .setLore("", Lang.get("Leftclick_Disable"))
                            .getItem());
                } else {
                    ItemBuilder builder = new ItemBuilder(item).setHideStandardLore(true);
                    builder.removeEnchantments();
                    item = builder.getItem();

                    setItem(new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                            .setLore("", Lang.get("Leftclick_Enable")).getItem());
                }

                iconButton.setItem(item);
                p.updateInventory();
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
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Lore"));
                                return;
                            }

                            e.setClose(true);
                            playSound(p);

                            ItemBuilder builder = new ItemBuilder(item);

                            builder.removeText(mainDescription);
                            builder.addText(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', input));
                            name = builder.getName();
                            builder.addText(mainDescription);

                            item = builder.getItem();
                            iconButton.setItem(item);
                            p.updateInventory();
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(!e.isSubmitted()) Sound.ITEM_BREAK.playSound(p);

                            e.setPost(GEditIcon.this::open);
                            quit = true;
                        }
                    }, new ItemBuilder(Material.PAPER).setName(Lang.get("Line") + "...").getItem());
                } else {
                    switch(type) {
                        case DECORATION:
                            name = null;
                            break;
                    }

                    item = new ItemBuilder(item).setName(name).removeLore().getItem();

                    setupMainIcon();

                    iconButton.setItem(item);
                    p.updateInventory();
                }
            }
        }.setOption(option));

        //commands
        addButton(new ItemButton(5, 2, command) {
            @Override
            public void onClick(InventoryClickEvent e) {

                if(GEditIcon.this.command != null) {
                    GEditIcon.this.command = null;

                    ItemBuilder commandBuilder = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command"));
                    commandBuilder.setLore("§8" + Lang.get("Current") + ": §7-");
                    commandBuilder.addLore("", Lang.get("Leftclick_Add"));

                    ItemStack command = commandBuilder.getItem();
                    setItem(command);
                    p.updateInventory();
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
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Command"));
                                return;
                            }

                            if(!input.startsWith("/")) input = "/" + input;

                            ItemBuilder commandBuilder = new ItemBuilder(Material.REDSTONE).setName("§6§n" + Lang.get("Command"));
                            commandBuilder.setLore("§8" + Lang.get("Current") + ": §7" + input);
                            commandBuilder.addLore("", Lang.get("Leftclick_Remove"));

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
                    }, new ItemBuilder(Material.REDSTONE).setName(Lang.get("Command") + "...").getItem());
                }
            }
        }.setOption(option).setOnlyLeftClick(true));

        //permission
        addButton(new ItemButton(6, 3, permissionIcon) {
            @Override
            public void onClick(InventoryClickEvent e) {

                if(permission != null) {
                    permission = null;

                    ItemBuilder permissionIconBuilder = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Permission"))
                            .setLore("§8" + Lang.get("Current") + ": §7-", "", Lang.get("Leftclick_Add"));

                    ItemStack permissionIcon = permissionIconBuilder.getItem();
                    setItem(permissionIcon);
                    p.updateInventory();
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
                                p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Permission"));
                                return;
                            }

                            ItemBuilder permissionIconBuilder = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Permission"));
                            permissionIconBuilder.setLore("§8" + Lang.get("Current") + ": §7" + input);
                            permissionIconBuilder.addLore("", Lang.get("Leftclick_Remove"));

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
                    }, new ItemBuilder(XMaterial.ENDER_EYE).setName(Lang.get("Permission") + "...").getItem());
                }
            }
        }.setOption(option).setOnlyLeftClick(true));

        //update globalwarp
        if(type.equals(IconType.GLOBAL_WARP)) {
            ItemStack globalWarpIcon = new ItemBuilder(Material.ENDER_CHEST).setName("§6§n" + Lang.get("GlobalWarp"))
                    .setLore(Lang.get("GlobalWarp_Current_Warp").replace("%GlobalWarp%", editing == null ? extra : ((GlobalWarp) editing).getAction(Action.SWITCH_SERVER).getValue()), "",
                            "§8" + Lang.get("Leftclick_Change"))
                    .getItem();

            addButton(new ItemButton(4, 4, globalWarpIcon) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    quit = false;
                    p.closeInventory();
                    new GGlobalWarpList(getPlayer(), new GGlobalWarpList.Listener() {
                        @Override
                        public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                            if(editing != null) {
                                ((GlobalWarp) editing).removeAction(Action.SWITCH_SERVER);
                                ((GlobalWarp) editing).addAction(new ActionObject(Action.SWITCH_SERVER, warp));
                            } else extra = warp;

                            quit = false;
                            p.closeInventory();
                            GEditIcon.this.reinitialize();
                            GEditIcon.this.open();
                            quit = true;
                        }

                        @Override
                        public void onClose() {
                            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                                GEditIcon.this.reinitialize();
                                GEditIcon.this.open();
                                Sound.ITEM_BREAK.playSound(p);
                                quit = true;
                            }, 1);
                        }

                        @Override
                        public String getLeftclickDescription() {
                            return ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + Lang.get("Leftclick_To_Choose");
                        }
                    }).open();
                }
            }.setOption(option).setOnlyLeftClick(true));
        }

        //costs
        if(AdapterType.canEnable()) {
            switch(this.type) {
                case WARP:
                case GLOBAL_WARP:
                    addButton(new ItemButton(4, 2, costsIcon) {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            if(e.isLeftClick()) {
                                quit = false;
                                p.closeInventory();
                                AnvilGUI.openAnvil(WarpSystem.getInstance(), getPlayer(), new AnvilListener() {
                                    @Override
                                    public void onClick(AnvilClickEvent e) {
                                        e.setClose(false);
                                        e.setCancelled(true);

                                        if(e.getInput() != null) {
                                            double costs;

                                            String in = e.getInput().replace(",", ".");
                                            if(!in.contains(".")) in += ".0";

                                            try {
                                                costs = Double.parseDouble(in);
                                            } catch(NumberFormatException ex) {
                                                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                                                return;
                                            }

                                            if(costs < 0) {
                                                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                                                return;
                                            }

                                            GEditIcon.this.costs = costs;
                                            e.setClose(true);
                                        }
                                    }

                                    @Override
                                    public void onClose(AnvilCloseEvent e) {
                                        e.setPost(() -> {
                                            if(e.isSubmitted()) {
                                                ItemStack costsIcon = new ItemBuilder(Material.GOLD_NUGGET).setName("§6§n" + Lang.get("Costs"))
                                                        .setLore("§8" + Lang.get("Current") + ": §7" + costs + " " + Lang.get("Coins"), "",
                                                                Lang.get("Leftclick_Set"))
                                                        .getItem();
                                                setItem(costsIcon);

                                                playSound(getPlayer());
                                            } else {
                                                Sound.ITEM_BREAK.playSound(p);
                                            }

                                            GEditIcon.this.open();
                                            quit = true;
                                        });
                                    }
                                }, new ItemBuilder(Material.PAPER).setName(Lang.get("Costs") + "...").getItem());
                            }
                        }
                    }.setOption(option).setOnlyLeftClick(true));
                    break;
            }
        }

        //disable/enable
        addButton(new ItemButton(4, 3, disableIcon) {
            @Override
            public void onClick(InventoryClickEvent e) {
                disabled = !disabled;

                ItemStack disableIcon = new ItemBuilder(disabled ? XMaterial.ROSE_RED : XMaterial.LIME_DYE)
                        .setName("§6§n" + Lang.get("Status") + ": " + (disabled ?
                                Lang.get("Disabled") :
                                Lang.get("Enabled")))
                        .setLore("",
                                disabled ? Lang.get("Leftclick_Enable_This_Icon") :
                                        Lang.get("Disable_This_Icon"))
                        .getItem();

                setItem(disableIcon);
                p.updateInventory();
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

    public Icon getEditing() {
        return editing;
    }

    public String getCommand() {
        return command;
    }
}
