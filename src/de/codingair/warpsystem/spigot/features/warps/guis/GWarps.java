package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.InterfaceListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.IconType;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

import java.util.ArrayList;
import java.util.List;

public class GWarps extends GUI {
    private Category category;
    private boolean editing;

    private boolean moving = false;
    private ItemStack cursor = null;
    private int oldSlot = -999;
    private Icon cursorIcon = null;
    private boolean showMenu = true;

    private GUIListener listener;

    private static String getTitle(Category category, Player p) {
        return getTitle(category, null, p);
    }

    private static String getTitle(Category category, GUIListener listener, Player player) {
        FileConfiguration config = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig();
        String key = player.hasPermission(IconManager.getInstance().getAdminPermission()) ? "Admin" : "User";

        return listener == null || listener.getTitle() == null ?
                ChatColor.translateAlternateColorCodes('&', (category == null ?
                        config.getString("WarpSystem.GUI." + key + ".Title.Standard", "&c&l&nWarps&r") :
                        config.getString("WarpSystem.GUI." + key + ".Title.In_Category", "&c&l&nWarps&r &c@%CATEGORY%").replace("%CATEGORY%", category.getNameWithoutColor())))
                : listener.getTitle();
    }

    private static int getSize(Player player) {
        return player.hasPermission(IconManager.getInstance().getAdminPermission()) ? IconManager.getInstance().getAdminSize() : IconManager.getInstance().getUserSize();
    }

    public GWarps(Player p, Category category, boolean editing) {
        this(p, category, editing, null);
    }

    public GWarps(Player p, Category category, boolean editing, GUIListener guiListener) {
        super(p, getTitle(category, guiListener, p), getSize(p), WarpSystem.getInstance(), false);
        this.listener = guiListener;
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
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        ItemBuilder noneBuilder;

        if(editing) {
            noneBuilder = new ItemBuilder(Material.BARRIER).setHideStandardLore(true)
                    .setName(Lang.get("Edit_Mode_Set_Icon", new Example("ENG", "&3Leftclick: &bSet Icon"), new Example("GER", "&3Linksklick: &bIcon setzen")));
        } else {
            noneBuilder = new ItemBuilder(IconManager.getInstance().getBackground()).setHideName(true).setHideStandardLore(true).setHideEnchantments(true);
        }

        ItemStack none = noneBuilder.getItem();

        if(p.hasPermission(WarpSystem.PERMISSION_MODIFY_ICONS) && showMenu) {
            ItemBuilder builder = new ItemBuilder(Material.NETHER_STAR).setName(Lang.get("Menu_Help", new Example("ENG", "&c&nHelp"), new Example("GER", "&c&nHilfe")));

            if(editing) {
                builder.setLore("§0",
                        Lang.get("Menu_Help_Leftclick_Edit_Mode", new Example("ENG", "&3Leftclick: &bQuit Edit-Mode"), new Example("GER", "&3Linksklick: &bBearbeitungs-Modus verlassen")));
            } else {
                builder.setLore("§0",
                        Lang.get("Menu_Help_Leftclick", new Example("ENG", "&3Leftclick: &bEdit-Mode"), new Example("GER", "&3Linksklick: &bBearbeitungs-Modus")));
            }
            builder.addLore(Lang.get("Menu_Help_Shift_Leftclick", new Example("ENG", "&3Shift-Leftclick: &bSet background"), new Example("GER", "&3Shift-Linksklick: &bHintergrund setzen")));
            builder.addLore("");
            builder.addLore(Lang.get("Menu_Help_Rightclick", new Example("ENG", "&3Rightclick: &bOptions"), new Example("GER", "&3Rechtsklick: &bOptionen")));
            builder.addLore(Lang.get("Menu_Help_Shift_Rightclick_Show_Icon", new Example("ENG", "&3Shift-Rightclick: &bShow icon"), new Example("GER", "&3Shift-Rechtsklick: &bZeige Icon")));

            builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            builder.setHideEnchantments(true);

            addButton(new ItemButton(0, builder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(moving) return;

                    if(e.isLeftClick()) {
                        if(e.isShiftClick()) {
                            IconManager.getInstance().setBackground(getPlayer().getInventory().getItem(getPlayer().getInventory().getHeldItemSlot()));
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, getPlayer()));
                        } else {
                            editing = !editing;
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, getPlayer()));
                        }
                    } else {
                        if(e.isShiftClick()) {
                            showMenu = !showMenu;
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, getPlayer()));
                        } else {
                            p.closeInventory();
                            new GConfig(p, category, editing).open();
                        }
                    }
                }
            }.setOption(option).setOnlyLeftClick(false));

        }

        int size = getSize(getPlayer());
        if(category != null) {
            addButton(new ItemButton(size - 9, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back", new Example("ENG", "Back"), new Example("GER", "Zurück"))).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    GWarps.this.category = null;
                    reinitialize();
                    setTitle(getTitle(GWarps.this.category, getPlayer()));
                }
            }.setOption(option));
        }

        for(Warp warp : manager.getWarps(category)) {
            if((editing || (!warp.hasPermission() || p.hasPermission(warp.getPermission()))) && this.cursorIcon != warp) {
                addToGUI(p, warp);
            }
        }

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            for(GlobalWarp warp : manager.getGlobalWarps(category)) {
                if((editing || (!warp.hasPermission() || p.hasPermission(warp.getPermission()))) && this.cursorIcon != warp) {
                    addToGUI(p, warp);
                }
            }
        }

        for(DecoIcon icon : manager.getDecoIcons(category)) {
            if(this.cursorIcon != icon) {
                addToGUI(p, icon);
            }
        }

        if(category == null) {
            for(Category c : manager.getCategories()) {
                if(editing || (!c.hasPermission() || p.hasPermission(c.getPermission()))) {
                    addToGUI(p, c);
                }
            }
        }

        for(int i = 0; i < size; i++) {
            if(editing) {
                final int slot = i;
                if(slot == oldSlot && cursorIcon instanceof Warp && ((Warp) cursorIcon).getCategory() == this.category) continue;

                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) {
                    addButton(new ItemButton(i, none.clone()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            if(moving) {
                                if(clickEvent.isLeftClick()) {
                                    if(cursorIcon instanceof Warp) {
                                        Warp icon = (Warp) cursorIcon;
                                        icon.setCategory(GWarps.this.category);
                                    }

                                    if(cursorIcon instanceof GlobalWarp) {
                                        GlobalWarp icon = (GlobalWarp) cursorIcon;
                                        icon.setCategory(GWarps.this.category);
                                    }

                                    if(cursorIcon instanceof DecoIcon) {
                                        DecoIcon icon = (DecoIcon) cursorIcon;
                                        icon.setCategory(GWarps.this.category);
                                    }

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

                            new GChooseIconType(getPlayer(), category, new Callback<IconType>() {
                                @Override
                                public void accept(IconType type) {
                                    if(type == null) {
                                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), GWarps.this::open, 2);
                                        return;
                                    }

                                    switch(type) {
                                        case DECORATION:
                                            new GEditIcon(p, category, item, null, slot, IconType.DECORATION).open();
                                            break;

                                        case GLOBAL_WARP:
                                        case WARP:
                                        case CATEGORY:
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

                                                        if(input.contains("@")) {
                                                            p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Correct_Name", new Example("ENG", "&cPlease don't use '@'-Symbols."), new Example("GER", "&cBitte benutze keine '@'-Zeichen.")));
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

                                                        if(type.equals(IconType.WARP)) {
                                                            if(manager.existsWarp(input, category)) {
                                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists", new Example("ENG", "&cThis name already exists."), new Example("GER", "&cDieser Name existiert bereits.")));
                                                                return;
                                                            }
                                                        } else if(type.equals(IconType.CATEGORY)) {
                                                            if(manager.existsCategory(input)) {
                                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists", new Example("ENG", "&cThis name already exists."), new Example("GER", "&cDieser Name existiert bereits.")));
                                                                return;
                                                            }
                                                        } else if(type.equals(IconType.GLOBAL_WARP)) {
                                                            if(manager.existsGlobalWarp(input)) {
                                                                p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists", new Example("ENG", "&cThis name already exists."), new Example("GER", "&cDieser Name existiert bereits.")));
                                                                return;
                                                            }
                                                        } else return;

                                                        input = input.replace("§", "&");

                                                        e.setClose(true);
                                                    }
                                                }

                                                @Override
                                                public void onClose(AnvilCloseEvent e) {
                                                    if(e.isSubmitted())
                                                        if(type.equals(IconType.GLOBAL_WARP)) {
                                                            e.setPost(() -> new GGlobalWarpList(p, new GGlobalWarpList.Listener() {
                                                                @Override
                                                                public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                                                                    getPlayer().closeInventory();
                                                                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new GEditIcon(p, category, item, input, slot, type, warp).open(), 2L);
                                                                }

                                                                @Override
                                                                public void onClose() {
                                                                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new GWarps(p, category, true).open(), 1);
                                                                }

                                                                @Override
                                                                public String getLeftclickDescription() {
                                                                    return ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + Lang.get("GlobalWarp_Leftclick_To_Choose", new Example("ENG", "&3Leftclick: &bChoose"), new Example("GER", ChatColor.GRAY + "&3Linksklick: &bWählen"));
                                                                }
                                                            }).open());
                                                        } else e.setPost(() -> new GEditIcon(p, category, item, input, slot, type).open());
                                                    else {
                                                        Sound.ITEM_BREAK.playSound(p);
                                                        e.setPost(() -> new GWarps(p, category, editing).open());
                                                    }
                                                }
                                            }, new ItemBuilder(Material.PAPER).setName(Lang.get("Name", new Example("ENG", "Name"), new Example("GER", "Name")) + "...").getItem());
                                            break;
                                    }
                                }
                            }).open();
                        }
                    }.setOption(option).setOnlyLeftClick(true));
                }
            } else {
                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) setItem(i, none);
            }
        }
    }

    private ItemBuilder prepareIcon(ActionIcon icon) {
        ItemBuilder builder = new ItemBuilder(icon.getItem());

        List<String> loreList = new ArrayList<>();
        if(icon.getName() != null) loreList.add("§f" + (icon instanceof Category ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', icon.getName()));
        if(builder.getLore() != null) loreList.addAll(new ArrayList<>(builder.getLore()));
        builder.setText(loreList);
        builder.setHideName(false);

        return builder;
    }

    private void addToGUI(Player p, ActionIcon icon) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        if((icon.getSlot() == 0 && showMenu) || icon.getSlot() >= getSize(getPlayer())) return;

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        if(editing || (!icon.hasPermission() || p.hasPermission(icon.getPermission()))) {
            ItemBuilder iconBuilder = prepareIcon(icon);

            if(editing) {
                String command = icon.getAction(Action.RUN_COMMAND) == null ? "-" : icon.getAction(Action.RUN_COMMAND).getValue();
                String permission = icon.getPermission() == null ? "-" : icon.getPermission();
                String costs = (icon.getAction(Action.PAY_MONEY) == null ? "0" : icon.getAction(Action.PAY_MONEY).getValue()) + " " + Lang.get("Coins", new Example("ENG", "Coin(s)"), new Example("GER", "Coin(s)"));

                if(icon.isDisabled()) {
                    iconBuilder.addText("§8------------");
                    iconBuilder.addText(Lang.get("Icon_Is_Disabled", new Example("ENG", "&4This icon is &l&ndisabled&4!"), new Example("GER", "&4Dieses Icon ist &l&ndeaktiviert&4!")));
                }

                iconBuilder.addText("§8------------");
                if(icon instanceof GlobalWarp) {
                    iconBuilder.addText("§7" + Lang.get("GlobalWarp", new Example("ENG", "GlobalWarp"), new Example("GER", "GlobalWarp")) + ": " + icon.getAction(Action.SWITCH_SERVER).getValue());
                    iconBuilder.addText("§7" + Lang.get("Target_Server", new Example("ENG", "Target-Server"), new Example("GER", "Ziel-Server")) + ": " + ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().get(icon.getAction(Action.SWITCH_SERVER).getValue()));

                    iconBuilder.addText("§8------------");
                }
                iconBuilder.addText("§7" + Lang.get("Command", new Example("ENG", "Command"), new Example("GER", "Befehl")) + ": " + command);
                iconBuilder.addText("§7" + Lang.get("Permission", new Example("ENG", "Permission"), new Example("GER", "Berechtigung")) + ": " + permission);
                if(AdapterType.canEnable()) iconBuilder.addText("§7" + Lang.get("Costs", new Example("ENG", "Costs"), new Example("GER", "Kosten")) + ": " + costs);
                iconBuilder.addText("§8------------");
                iconBuilder.addText(Lang.get("Leftclick_Edit", new Example("ENG", "&7Leftclick: Configure"), new Example("GER", "&7Linksklick: Bearbeiten")));
                iconBuilder.addText(Lang.get("Shift_Leftclick_Edit", new Example("ENG", "&7Shift-Leftclick: Move"), new Example("GER", "&7Shift-Linksklick: Bewegen")));
                iconBuilder.addText(Lang.get("Rightclick_Delete", new Example("ENG", "&7Rightclick: Delete"), new Example("GER", "&7Rechtsklick: Löschen")));
                if(icon instanceof Category) iconBuilder.addText(Lang.get("Shift_Rightclick_Edit", new Example("ENG", "&7Shift-Rightclick: Open"), new Example("GER", "&7Shift-Rechtsklick: Öffnen")));

                if(icon instanceof Warp || icon instanceof GlobalWarp || icon instanceof DecoIcon) {
                    iconBuilder.addText("§8------------");

                    List<String> list = TextAlignment.lineBreak(Lang.get("Move_Help", new Example("ENG", "&7Moving: Rightclick on categories to switch to it."), new Example("GER", "&7Bewegen: Rechtsklick auf Kategorien um dort hin zu wechseln.")), 80);
                    iconBuilder.addText(list);
                }
            } else if(icon.isDisabled()) return;

            addButton(new ItemButton(icon.getSlot(), iconBuilder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(editing) {
                        if(e.isLeftClick()) {
                            if(moving) {
                                if(cursorIcon instanceof Warp) {
                                    Warp icon = (Warp) cursorIcon;
                                    icon.setCategory(GWarps.this.category);
                                }

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
                        } else if(e.isRightClick()) {
                            if(moving) {
                                if(icon instanceof Category && (cursorIcon instanceof Warp || cursorIcon instanceof GlobalWarp || cursorIcon instanceof DecoIcon)) {
                                    GWarps.this.category = (Category) icon;
                                    reinitialize();
                                    setTitle(getTitle(GWarps.this.category, getPlayer()));
                                }
                            } else {
                                if(e.isShiftClick() && icon instanceof Category) {
                                    GWarps.this.category = (Category) icon;
                                    reinitialize();
                                    setTitle(getTitle(GWarps.this.category, getPlayer()));
                                    return;
                                }

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
                                            manager.remove(icon);
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Deleted", new Example("ENG", "&cThe icon was deleted successfully."), new Example("GER", "&cDas Symbol wurde erfolgreich gelöscht.")));
                                        } else {
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Not_Deleted", new Example("ENG", "&7The icon was &cnot &7deleted."), new Example("GER", "&7Das Symbol wurde &cnicht &7gelöscht.")));
                                        }

                                        new GWarps(p, category, editing).open();
                                    }
                                }, () -> new GWarps(p, category, editing).open()).open();
                            }
                        }
                    } else if(e.isLeftClick()) {
                        switch(icon.getType()) {
                            case CATEGORY:
                                GWarps.this.category = (Category) icon;
                                reinitialize();
                                setTitle(getTitle(GWarps.this.category, getPlayer()));
                                break;

                            case WARP:
                                if(listener != null) {
                                    Task task = listener.onClickOnWarp((Warp) icon, editing);

                                    if(task != null) {
                                        task.runTask(p, editing);
                                        return;
                                    }
                                }

                            case DECORATION:
                            case GLOBAL_WARP:
                                icon.perform(p, editing);
                                break;
                        }
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
            setTitle(getTitle(GWarps.this.category, getPlayer()));
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
