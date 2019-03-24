package de.codingair.warpsystem.spigot.features.nativeportals.guis;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.nativeportals.Portal;
import de.codingair.warpsystem.spigot.features.nativeportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Head;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.guis.GSimpleWarpList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.List;

public class GEditor extends GUI {
    private Portal backup;
    private Portal portal;
    private Menu menu;

    private String name;
    private PortalType type;

    private Destination destination;
    private boolean changed = false;

    public GEditor(Player p) {
        this(p, null);
    }

    public GEditor(Player p, Portal backup) {
        this(p, backup, Menu.MAIN);
    }

    public GEditor(Player p, Portal backup, Menu page) {
        super(p, Lang.get("Native_Portals"), 27, WarpSystem.getInstance(), false);

        this.backup = backup;
        if(this.backup != null) {
            this.name = this.backup.getDisplayName();
            this.type = this.backup.getType();
            this.destination = this.backup.getDestination();

            this.backup.setVisible(false);
            this.portal = backup.clone();
            this.portal.setType(PortalType.EDIT);
            this.portal.setVisible(true);
        }

        this.menu = page == null ? Menu.MAIN : page;

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(isClosingByButton() || isClosingByOperation()) return;

                if(menu == Menu.DELETE) {
                    if(backup != null) {
                        portal.clear();
                        backup.setVisible(true);
                    }

                    p.sendMessage(Lang.getPrefix() + Lang.get("NativePortal_Not_Deleted"));
                    return;
                }

                if(!changed && menu == Menu.MAIN) {
                    if(backup != null) {
                        portal.clear();
                        backup.setVisible(true);
                    }
                    return;
                }

                Sound.CLICK.playSound(getPlayer());
                if(menu != Menu.CLOSE) menu = menu == Menu.MAIN ? Menu.CLOSE : Menu.MAIN;

                reinitialize(menu == Menu.CLOSE ?
                        Lang.get("NativePortals_Confirm_Close")
                        : getTitle());
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> open(), 1);
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {

            }

            @Override
            public void onMoveToTopInventory(ItemStack item, int oldRawSlot, List<Integer> newRawSlots) {

            }

            @Override
            public void onCollectToCursor(ItemStack item, List<Integer> oldRawSlots, int newRawSlot) {

            }
        });

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setOnlyLeftClick(true);
        option.setClickSound(Sound.CLICK.bukkitSound());

        ItemStack black = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemStack gray = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemStack leaves = new ItemBuilder(XMaterial.OAK_LEAVES).setHideName(true).getItem();

        setItem(0, 0, leaves);
        setItem(8, 0, leaves);

        setItem(1, 0, black);
        setItem(0, 1, black);
        setItem(0, 2, black);
        setItem(7, 0, black);
        setItem(8, 1, black);
        setItem(8, 2, black);

        setItem(2, 0, gray);
        setItem(1, 1, gray);
        setItem(1, 2, gray);
        setItem(6, 0, gray);
        setItem(7, 1, gray);
        setItem(7, 2, gray);

        String waterPortal = Lang.get("Water_Portal");
        String lavaPortal = Lang.get("Lava_Portal");
        String airPortal = Lang.get("Air_Portal");
        String netherPortal = Lang.get("Nether_Portal");
        String endPortal = Lang.get("End_Portal");

        if(menu != Menu.CLOSE) {
            boolean ready = name != null && type != null && destination != null && destination.getId() != null && portal != null && !portal.getBlocks().isEmpty();
            ItemBuilder builder = new ItemBuilder(ready ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA)
                    .setText((ready ? "§a" : "§c") + "§n" + Lang.get("Status"));

            builder.addText("§7" + Lang.get("Name") + ": " + (name == null ? "§c§m-" : "\"" + ChatColor.translateAlternateColorCodes('&', name) + "\""));
            builder.addText("§7" + Lang.get("NativePortal_Material") + ": " + (type == null ? "§c§m-" : type.name()));

            String teleportType = null;
            if(destination != null && destination.getId() != null) {
                switch(destination.getType()) {
                    case SimpleWarp: {
                        teleportType = Lang.get("SimpleWarp");
                        break;
                    }

                    case GlobalWarp: {
                        teleportType = Lang.get("GlobalWarp");
                        break;
                    }

                    case Server: {
                        teleportType = Lang.get("Server");
                        break;
                    }
                }
            }
            builder.addText("§7" + Lang.get("Teleport_Link") + ": " + (destination == null || destination.getId() == null ? "§c§m-" : ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', destination.getId())) + " §8(§7" + teleportType + "§8)"));
            builder.addText("§7" + Lang.get("Portal_Blocks") + ": " + (portal == null ? "§c0" : portal.getBlocks().size()));

            builder.addText("");

            if(ready) {
                builder.addText("§8» §a" + Lang.get("Save"));

                addButton(new ItemButton(4, builder.getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        if(backup == null) {
                            portal.setType(type);
                            portal.setDisplayName(name);
                            portal.setDestination(destination);
                            NativePortalManager.getInstance().addPortal(portal);
                        } else {
                            backup.apply(portal);
                            backup.setType(type);
                            backup.setDisplayName(name);
                            backup.setDestination(destination);

                            portal.clear();
                            backup.setVisible(true);
                        }
                    }
                }.setOption(option).setCloseOnClick(true));
            } else {
                builder.addText("§8» " + Lang.get("Not_Ready_For_Saving"));
                setItem(4, builder.getItem());
            }
        }

        switch(menu) {
            case MAIN:
                addButton(new ItemButton(2, 2, new ItemBuilder(XMaterial.NAME_TAG).setName("§8» §b" + Lang.get("Set_Portal_Name")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        AnvilGUI.openAnvil(WarpSystem.getInstance(), getPlayer(), new AnvilListener() {
                            @Override
                            public void onClick(AnvilClickEvent e) {
                                playSound(getPlayer());
                                e.setCancelled(true);
                                e.setClose(false);

                                if(e.getInput() == null) {
                                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                    return;
                                }

                                e.setClose(true);
                            }

                            @Override
                            public void onClose(AnvilCloseEvent e) {
                                if(e.getSubmittedText() != null) {
                                    GEditor.this.name = e.getSubmittedText();
                                    changed = true;
                                }

                                initialize(p);
                                e.setPost(GEditor.this::open);
                            }
                        }, new ItemBuilder(XMaterial.PAPER).setName(name == null ? Lang.get("Portal_Name") + "..." : name).getItem());
                    }
                }.setOption(option).setCloseOnClick(true));

                addButton(new ItemButton(3, 2, new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName("§8» §b" + Lang.get("NativePortal_Material")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        menu = Menu.MATERIAL;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(5, 2, new ItemBuilder(XMaterial.ENDER_PEARL).setName("§8» §b" + Lang.get("Teleport_Link")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        menu = Menu.TYPE;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(6, 2, new ItemBuilder(XMaterial.IRON_PICKAXE).setHideStandardLore(true).setName("§8» §b" + Lang.get("NativePortals_Set_Blocks")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalEditor editor = portal == null ? new PortalEditor(getPlayer(), PortalType.EDIT) : new PortalEditor(getPlayer(), portal);
                        int size = editor.getPortal().getBlocks().size();
                        editor.init();

                        MessageAPI.sendActionBar(getPlayer(), Lang.get("Drop_To_Leave"), WarpSystem.getInstance(), Integer.MAX_VALUE);

                        Bukkit.getPluginManager().registerEvents(new Listener() {
                            @EventHandler
                            public void onDrop(PlayerDropItemEvent e) {
                                if(e.getItemDrop().getItemStack().equals(PortalEditor.PORTAL_ITEM.getItem())) {
                                    e.setCancelled(true);

                                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                                        MessageAPI.stopSendingActionBar(getPlayer());
                                        HandlerList.unregisterAll(this);

                                        portal = editor.end();
                                        if(size != portal.getBlocks().size()) changed = true;

                                        reinitialize();
                                        open();
                                    });
                                }
                            }
                        }, WarpSystem.getInstance());
                    }
                }.setOption(option).setCloseOnClick(true));
                break;

            case MATERIAL:
                addButton(new ItemButton(2, 2, new ItemBuilder(XMaterial.WATER_BUCKET).setName("§8» §b" + waterPortal).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalType next = PortalType.WATER;
                        if(type != next) {
                            changed = true;
                            type = next;
                        }

                        menu = Menu.MAIN;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(3, 2, new ItemBuilder(XMaterial.LAVA_BUCKET).setName("§8» §b" + lavaPortal).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalType next = PortalType.LAVA;
                        if(type != next) {
                            changed = true;
                            type = next;
                        }

                        menu = Menu.MAIN;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(4, 2, new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE).setName("§8» §b" + airPortal).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalType next = PortalType.AIR;
                        if(type != next) {
                            changed = true;
                            type = next;
                        }

                        menu = Menu.MAIN;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(5, 2, new ItemBuilder(XMaterial.FLINT_AND_STEEL).setName("§8» §b" + netherPortal).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalType next = PortalType.NETHER;
                        if(type != next) {
                            changed = true;
                            type = next;
                        }

                        menu = Menu.MAIN;
                        reinitialize();
                    }
                }.setOption(option));

                addButton(new ItemButton(6, 2, new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName("§8» §b" + endPortal).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        PortalType next = PortalType.END;
                        if(type != next) {
                            changed = true;
                            type = next;
                        }

                        menu = Menu.MAIN;
                        reinitialize();
                    }
                }.setOption(option));
                break;

            case TYPE: {
                addButton(new ItemButton(WarpSystem.getInstance().isOnBungeeCord() ? 2 : 4, 2, new ItemBuilder(XMaterial.ENDER_PEARL).setName("§8» §b" + Lang.get("Choose_A_SimpleWarp")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        new GSimpleWarpList(p, new GSimpleWarpList.Listener() {
                            @Override
                            public void onClickOnWarp(String warp, InventoryClickEvent e) {
                                Destination next = new Destination(warp, DestinationType.SimpleWarp);
                                if(destination == null || !destination.equals(next)) {
                                    destination = next;
                                    changed = true;
                                }

                                p.closeInventory();
                            }

                            @Override
                            public void onClose() {
                                menu = Menu.MAIN;
                                reinitialize();
                                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), GEditor.this::open, 1);
                            }

                            @Override
                            public String getLeftclickDescription() {
                                return ChatColor.DARK_GRAY + "» §3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose");
                            }
                        }).open();
                    }
                }.setOption(option).setCloseOnClick(true));

                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    addButton(new ItemButton(4, 2, new ItemBuilder(XMaterial.ENDER_EYE).setName("§8» §b" + Lang.get("Choose_A_GlobalWarp")).getItem()) {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            new GGlobalWarpList(p, new GGlobalWarpList.Listener() {
                                @Override
                                public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                                    Destination next = new Destination(warp, DestinationType.GlobalWarp);
                                    if(destination == null || !destination.equals(next)) {
                                        destination = next;
                                        changed = true;
                                    }

                                    p.closeInventory();
                                }

                                @Override
                                public void onClose() {
                                    menu = Menu.MAIN;
                                    reinitialize();
                                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), GEditor.this::open, 1);
                                }

                                @Override
                                public String getLeftclickDescription() {
                                    return ChatColor.DARK_GRAY + "» §3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose");
                                }
                            }).open();
                        }
                    }.setOption(option).setCloseOnClick(true));

                    addButton(new ItemButton(6, 2, new ItemBuilder(XMaterial.ENDER_CHEST).setName("§8» §b" + Lang.get("Choose_A_Server")).getItem()) {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                                @Override
                                public void onClick(AnvilClickEvent e) {
                                    if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                                    String input = e.getInput();

                                    if(input == null) {
                                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                        return;
                                    }

                                    e.setClose(true);
                                    Destination next = new Destination(input, DestinationType.Server);
                                    if(destination == null || !destination.equals(next)) {
                                        destination = next;
                                        changed = true;
                                    }
                                }

                                @Override
                                public void onClose(AnvilCloseEvent e) {
                                    if(e.getSubmittedText() != null) {
                                        menu = Menu.MAIN;
                                        reinitialize();
                                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), GEditor.this::open, 1);
                                    }
                                }
                            }, new ItemBuilder(XMaterial.PAPER).setName(destination != null && destination.getType() == DestinationType.Server ? destination.getId() : Lang.get("Server") + "...").getItem());
                        }
                    }.setOption(option).setCloseOnClick(true));
                }
                break;
            }

            case CLOSE:
                setItem(4, 0, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial()).setText(TextAlignment.lineBreak(Lang.get("Sure_That_You_Want_To_Loose_Your_Data"), 100)).getItem());

                addButton(new ItemButton(2, 2, new ItemBuilder(Head.CYAN_ARROW_LEFT.getItem()).setHideName(false).setName("§8» §b" + Lang.get("Back")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        menu = Menu.MAIN;
                        reinitialize(Lang.get("Native_Portals"));
                        getPlayer().updateInventory();
                    }
                }.setOption(option));

                addButton(new ItemButton(6, 2, new ItemBuilder(XMaterial.BARRIER).setName("§8» §c" + Lang.get("Close")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        if(portal != null) portal.clear();
                        if(backup != null) backup.setVisible(true);
                    }
                }.setOption(option).setCloseOnClick(true));
                break;

            case DELETE:
                setItem(4, 0, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial()).setText(TextAlignment.lineBreak(Lang.get("NativePortal_Confirm_Delete"), 100)).getItem());

                addButton(new ItemButton(2, 2, new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§8» §b" + Lang.get("No_Keep")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        if(backup != null) {
                            p.sendMessage(Lang.getPrefix() + Lang.get("NativePortal_Not_Deleted"));
                            portal.clear();
                            backup.setVisible(true);
                        }
                    }
                }.setOption(option).setCloseOnClick(true));

                addButton(new ItemButton(6, 2, new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§8» §c" + Lang.get("Yes_Delete")).getItem()) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        if(backup != null) {
                            portal.clear();
                            backup.clear();
                            NativePortalManager.getInstance().getPortals().remove(backup);
                            p.sendMessage(Lang.getPrefix() + Lang.get("NativePortal_Deleted"));
                        }
                    }
                }.setOption(option).setCloseOnClick(true));
                break;
        }
    }

    public Portal getPortal() {
        return portal;
    }

    public enum Menu {
        MAIN, MATERIAL, TYPE, CLOSE, DELETE
    }
}
