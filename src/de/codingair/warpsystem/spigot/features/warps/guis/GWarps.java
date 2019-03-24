package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.InterfaceListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.GEditor;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.BoundAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
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
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GWarps extends GUI {
    private Icon category;
    private boolean editing;

    private boolean moving = false;
    private ItemStack cursor = null;
    private int oldSlot = -999;
    private Icon cursorIcon = null;
    private boolean showMenu = true;

    private GUIListener listener;
    private boolean canEdit;
    private String world;
    private List<Class<? extends Icon>> hide;

    private static String getTitle(Icon category, GUIListener listener, Player player) {
        FileConfiguration config = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig();
        String key = player.hasPermission(WarpSystem.PERMISSION_ADMIN) ? "Admin" : "User";

        return listener == null || listener.getTitle() == null ?
                ChatColor.translateAlternateColorCodes('&', (category == null || category.getName() == null ?
                        config.getString("WarpSystem.GUI." + key + ".Title.Standard", "&c&l&nWarps&r") :
                        config.getString("WarpSystem.GUI." + key + ".Title.In_Category", "&c&l&nWarps&r &c@%CATEGORY%").replace("%CATEGORY%", category.getNameWithoutColor())))
                : listener.getTitle();
    }

    private static int getSize(Player player) {
        return player.hasPermission(WarpSystem.PERMISSION_ADMIN) ? IconManager.getInstance().getAdminSize() : IconManager.getInstance().getUserSize();
    }

    public GWarps(Player p, Icon category, boolean editing) {
        this(p, category, editing, null);
    }

    public GWarps(Player p, Icon category, boolean editing, GUIListener guiListener, Class<? extends Icon>... without) {
        this(p, category, editing, guiListener, true, without);
    }

    public GWarps(Player p, Icon category, boolean editing, GUIListener guiListener, boolean canEdit, Class<? extends Icon>... without) {
        this(p, category, editing, guiListener, canEdit, p.getLocation().getWorld().getName(), without);
    }

    public GWarps(Player p, Icon category, boolean editing, GUIListener guiListener, boolean canEdit, String world, Class<? extends Icon>... without) {
        super(p, getTitle(category, guiListener, p), getSize(p), WarpSystem.getInstance(), false);
        this.listener = guiListener;
        this.category = category;
        this.editing = editing;
        this.canEdit = canEdit;
        this.world = IconManager.getInstance().boundToWorld() ? world : null;
        this.hide = without == null ? new ArrayList<>() : Arrays.asList(without);

        setBuffering(true);

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
                if(isClosingByButton()) return;
                if(GWarps.this.listener != null) GWarps.this.listener.onClose();
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
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        ItemBuilder noneBuilder;

        if(editing) {
            noneBuilder = new ItemBuilder(Material.BARRIER).setHideStandardLore(true)
                    .setName("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Set_Icon"));
        } else {
            noneBuilder = new ItemBuilder(IconManager.getInstance().getBackground()).setHideName(true).setHideStandardLore(true).setHideEnchantments(true);
        }

        ItemStack none = noneBuilder.getItem();

        if(p.hasPermission(WarpSystem.PERMISSION_MODIFY_ICONS) && showMenu && canEdit) {
            ItemBuilder builder = new ItemBuilder(Material.NETHER_STAR).setName(Lang.get("Menu_Help"));

            if(editing) {
                builder.setLore("§0", "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Quit_Edit_Mode"));
            } else {
                builder.setLore("§0", "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Edit_Mode"));
            }
            builder.addLore("§3" + Lang.get("Shift_Leftclick") + ": §b" + Lang.get("Set_Background"));
            builder.addLore("");
            builder.addLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Options"));
            builder.addLore("§3" + Lang.get("Shift_Rightclick") + ": §b" + Lang.get("Show_Icon"));

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
                            setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                        } else {
                            editing = !editing;
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                        }
                    } else {
                        if(e.isShiftClick()) {
                            showMenu = !showMenu;
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                        } else {
                            new GConfig(p, category, editing).open();
                        }
                    }
                }
            }.setOption(option).setOnlyLeftClick(false));
        }

        int size = getSize(getPlayer());
        if(category != null) {
            addButton(new ItemButton(size - 9, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back")).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    GWarps.this.category = null;
                    reinitialize();
                    setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                }
            }.setOption(option));
        }

        List<Icon> icons = manager.getIcons(category);
        for(Icon icon : icons) {
            if(icon.isCategory() || (!icon.hasPermission() && (hideAll(p) || hideAll(p, "Warp")) && !editing)) continue;
            BoundAction bound = icon.getAction(Action.BOUND_TO_WORLD);

            if(((bound == null && world == null) || (bound != null && world != null && world.equals(bound.getValue())))
                    && (editing || (!icon.hasPermission() || p.hasPermission(icon.getPermission())))
                    && this.cursorIcon != icon) addToGUI(p, icon);
        }

        if(category == null) {
            List<Icon> cIcons = manager.getCategories();
            for(Icon icon : cIcons) {
                if(!icon.hasPermission() && (hideAll(p) || hideAll(p, "Category")) && !editing) continue;
                BoundAction bound = icon.getAction(Action.BOUND_TO_WORLD);

                if(((bound == null && world == null) || (bound != null && world != null && world.equals(bound.getValue())))
                        && (editing || (!icon.hasPermission() || p.hasPermission(icon.getPermission())))
                        && this.cursorIcon != icon) addToGUI(p, icon);
            }
        }

        for(int i = 0; i < size; i++) {
            if(editing) {
                final int slot = i;
                if(slot == oldSlot && !cursorIcon.isCategory() && cursorIcon.getCategory() == this.category) continue;

                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) {
                    addButton(new ItemButton(i, none.clone()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            if(moving) {
                                if(clickEvent.isLeftClick()) {
                                    cursorIcon.setCategory(GWarps.this.category);
                                    cursorIcon.setSlot(clickEvent.getSlot());
                                    clickEvent.setCursor(new ItemStack(Material.AIR));
                                    setMoving(false, clickEvent.getSlot());
                                }

                                return;
                            }

                            ItemStack item = p.getItemInHand();

                            if(item == null || item.getType().equals(Material.AIR)) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand"));
                                return;
                            }

                            Callback<Boolean> callback = new Callback<Boolean>() {
                                @Override
                                public void accept(Boolean category) {
                                    if(category == null) {
                                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), GWarps.this::open);
                                        return;
                                    }

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
                                                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                                    return;
                                                }

                                                if(input.contains("@")) {
                                                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Correct_Name"));
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

                                                if(category) {
                                                    if(manager.existsCategory(input)) {
                                                        p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                                        return;
                                                    }
                                                } else {
                                                    if(manager.existsIcon(input)) {
                                                        p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
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
                                                e.setPost(() -> new GEditor(p, input, slot, GWarps.this.category, item, category).open());
                                            else {
                                                Sound.ITEM_BREAK.playSound(p);
                                                e.setPost(() -> new GWarps(p, GWarps.this.category, editing).open());
                                            }
                                        }
                                    }, new ItemBuilder(Material.PAPER).setName(Lang.get("Name") + "...").getItem());
                                }
                            };

                            boolean category = GWarps.this.category == null;
                            if(category) {
                                //Choose
                                new GChooseIconType(p, callback).open();
                            } else callback.accept(category);
                        }
                    }.setOption(option).setOnlyLeftClick(true));
                }
            } else {
                if(getItem(i) == null || getItem(i).getType().equals(Material.AIR)) setItem(i, none);
            }
        }
    }

    private ItemBuilder prepareIcon(Icon icon) {
        ItemBuilder builder = new ItemBuilder(icon.getItem());

        List<String> loreList = new ArrayList<>();
        if(icon.getName() != null) loreList.add("§f" + (icon.isCategory() ? "§n" : "") + ChatColor.translateAlternateColorCodes('&', icon.getName()));
        if(builder.getLore() != null) loreList.addAll(new ArrayList<>(builder.getLore()));
        builder.setText(loreList);
        builder.setHideName(false);

        return builder;
    }

    private void addToGUI(Player p, Icon icon) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        if((icon.getSlot() == 0 && showMenu) || icon.getSlot() >= getSize(getPlayer())) return;

        for(Class<? extends Icon> forbidden : this.hide) {
            if(forbidden.isInstance(icon)) return;
        }

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        if(editing || (!icon.hasPermission() || p.hasPermission(icon.getPermission()))) {
            ItemBuilder iconBuilder = prepareIcon(icon);

            if(editing) {
                String command = icon.getAction(Action.COMMAND) == null ? "0" : icon.getAction(CommandAction.class).getValue().size() + "";
                String permission = icon.getPermission() == null ? "-" : icon.getPermission();
                String costs = (icon.getAction(Action.COSTS) == null ? "0" : icon.getAction(CostsAction.class).getValue()) + " " + Lang.get("Coins");

                if(icon.isDisabled()) {
                    iconBuilder.addText("§8------------");
                    iconBuilder.addText(Lang.get("Icon_Is_Disabled"));
                }

                iconBuilder.addText("§8------------");

                iconBuilder.addText("§7" + Lang.get("Commands") + ": " + command);
                iconBuilder.addText("§7" + Lang.get("Permission") + ": " + permission);
                if(AdapterType.canEnable()) iconBuilder.addText("§7" + Lang.get("Costs") + ": " + costs);
                iconBuilder.addText("§8------------");
                iconBuilder.addText("§3" + Lang.get("Leftclick") + ": §7" + Lang.get("Edit"));
                iconBuilder.addText("§3" + Lang.get("Shift_Leftclick") + ": §7" + Lang.get("Move"));
                iconBuilder.addText("§3" + Lang.get("Rightclick") + ": §7" + ChatColor.stripColor(Lang.get("Delete")));
                if(icon.isCategory()) iconBuilder.addText("§3" + Lang.get("Shift_Rightclick") + ": §7" + Lang.get("Open"));

                if(!icon.isCategory()) {
                    iconBuilder.addText("§8------------");

                    List<String> list = TextAlignment.lineBreak(Lang.get("Move_Help"), 80);
                    iconBuilder.addText(list);
                }
            } else if(icon.isDisabled()) return;

            addButton(new ItemButton(icon.getSlot(), iconBuilder.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if(listener != null) {
                        Task task = listener.onClickOnIcon(icon, editing);

                        if(task != null) {
                            task.runTask(p, editing);
                            return;
                        }
                    }

                    if(editing) {
                        if(e.isLeftClick()) {
                            if(moving) {
                                if(!cursorIcon.isCategory()) {
                                    cursorIcon.setCategory(GWarps.this.category);
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
                                    new GEditor(p, icon).open();
                                }
                            }
                        } else if(e.isRightClick()) {
                            if(moving) {
                                if(icon.isCategory() && !cursorIcon.isCategory()) {
                                    GWarps.this.category = icon;
                                    reinitialize();
                                    setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                                }
                            } else {
                                if(e.isShiftClick() && icon.isCategory()) {
                                    GWarps.this.category = icon;
                                    reinitialize();
                                    setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                                    return;
                                }

                                new IconDeleteGUI(p, new Callback<Boolean>() {
                                    @Override
                                    public void accept(Boolean delete) {
                                        if(delete) {
                                            manager.remove(icon);
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Deleted"));
                                        } else {
                                            p.sendMessage(Lang.getPrefix() + Lang.get("Icon_Not_Deleted"));
                                        }

                                        new GWarps(p, category, editing).open();
                                    }
                                }, () -> new GWarps(p, category, editing).open()).open();
                            }
                        }
                    } else if(e.isLeftClick()) {
                        if(icon.isCategory()) {
                            GWarps.this.category = icon;
                            reinitialize();
                            setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
                        } else {
                            if(listener != null) {
                                Task task = listener.onClickOnIcon(icon, editing);

                                if(task != null) {
                                    task.runTask(p, editing);
                                    return;
                                }
                            }
                        }

                        icon.perform(p);
                    }
                }
            }.setOption(option).setOnlyLeftClick(!editing));
        }
    }

    private void setMoving(boolean moving, int slot) {
        if(!moving) {
            if(oldSlot != slot) {
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Success_Icon_Moved"));
            }

            oldSlot = -999;
            cursor = null;
            cursorIcon = null;
            reinitialize();
            setTitle(getTitle(GWarps.this.category, listener, getPlayer()));
        }

        this.moving = moving;
        this.oldSlot = slot;

        if(moving) {
            for(int i = 0; i < getSize(); i++) {
                if(i == slot || getItem(i) == null || getItem(i).getType().equals(Material.AIR)) continue;

                setItem(i, new ItemBuilder(getItem(i)).setLore("", "§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Move_Icon")).getItem());
            }
        }
    }

    private boolean hideAll(Player player) {
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();
            if(perm.equalsIgnoreCase(WarpSystem.PERMISSION_HIDE_ALL_ICONS)) return true;
        }
        return false;
    }

    private boolean hideAll(Player player, String type) {
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();
            if(perm.equalsIgnoreCase(WarpSystem.PERMISSION_HIDE_ALL_ICONS + "." + type)) return true;
        }
        return false;
    }
}
