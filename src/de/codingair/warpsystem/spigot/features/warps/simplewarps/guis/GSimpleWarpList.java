package de.codingair.warpsystem.spigot.features.warps.simplewarps.guis;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GSimpleWarpList extends GUI {
    public interface Listener {
        void onClickOnWarp(String warp, InventoryClickEvent e);

        void onClose();

        String getLeftclickDescription();
    }

    private static int MAX_PAGE() {
        SimpleWarpManager manager = SimpleWarpManager.getInstance();
        if(manager.getWarps().isEmpty()) return 0;

        return (int) (Math.ceil((double) manager.getWarps().size() / 44.0) - 1);
    }

    private static String TITLE(int page) {
        return ChatColor.RED + Lang.get("SimpleWarps") + " " + ChatColor.GRAY + "- " + ChatColor.RED + Lang.get("List") + " " + ChatColor.GRAY + "(" + page + "/" + (MAX_PAGE() + 1) + ")";
    }

    private static void addWarp(GSimpleWarpList gui, String warp, String underline) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());

        int slot = gui.firstEmpty();
        if(slot < 0 || slot > 53) return;

        ItemStack icon;
        ItemBuilder builder = new ItemBuilder(getIcon(warp, underline));

        if(gui.getClickListener() != null && gui.getClickListener().getLeftclickDescription() != null) builder.setLore("", gui.getClickListener().getLeftclickDescription());

        icon = builder.getItem();

        gui.addButton(new ItemButton(slot, icon) {
            @Override
            public void onClick(InventoryClickEvent e) {
                gui.button = true;
                if(gui.getClickListener() != null) gui.getClickListener().onClickOnWarp(warp, e);
                gui.button = false;
            }
        }.setOption(option));
    }

    private Listener listener;
    private String searching;
    private int page = 0;
    private boolean button = false;

    public GSimpleWarpList(Player p) {
        super(p, TITLE(1), 54, WarpSystem.getInstance());
    }

    public GSimpleWarpList(Player p, String search) {
        super(p, TITLE(1), 54, WarpSystem.getInstance(), false);

        this.searching = search;
        initialize(p);
    }

    public GSimpleWarpList(Player p, Listener listener) {
        super(p, TITLE(1), 54, WarpSystem.getInstance(), false);
        this.listener = listener;
        initialize(p);
    }

    public Listener getClickListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(Player p) {
        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!isClosingByButton()) {
                    if(getClickListener() != null) getClickListener().onClose();
                }
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

        SimpleWarpManager manager = SimpleWarpManager.getInstance();

        ItemStack ph = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemBuilder search = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED.toString() + (searching == null ? "" : ChatColor.UNDERLINE) + Lang.get("Search"));
        if(searching != null) {
            search.addLore("", ChatColor.GRAY + "» " + Lang.get("Current") + ": '" + ChatColor.YELLOW + searching + ChatColor.GRAY + "'",
                    ChatColor.GRAY + "» " + Lang.get("Rightclick_To_Reset"));
        }

        setItem(2, ph);
        setItem(6, ph);
        addLine(2, 1, 6, 1, ph, true);

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setCloseOnClick(true);

        addButton(new ItemButton(3, new ItemBuilder(Skull.ArrowLeft).setName(ChatColor.GRAY + Lang.get("Previous_Page")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(page == 0) return;
                page--;
                reinitialize(TITLE(page + 1));
            }
        });

        addButton(new ItemButton(5, new ItemBuilder(Skull.ArrowRight).setName(ChatColor.GRAY + Lang.get("Next_Page")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(page == MAX_PAGE()) return;
                page++;
                reinitialize(TITLE(page + 1));
            }
        });

        addButton(new ItemButton(4, search.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(e.isRightClick()) {
                    searching = null;
                    reinitialize();
                } else if(e.isLeftClick()) {
                    setClosingByButton(true);
                    p.closeInventory();

                    AnvilGUI.openAnvil(WarpSystem.getInstance(), p, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            e.setCancelled(true);
                            e.setClose(true);
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            e.setPost(() -> {
                                if(e.isSubmitted()) {
                                    searching = e.getSubmittedText();
                                    page = 0;
                                    reinitialize();
                                }

                                open();
                            });
                        }
                    }, new ItemBuilder(Material.PAPER).setName(Lang.get("Search")).getItem());
                }
            }
        }.setOption(option).setCloseOnClick(false));

        List<String> warps = new ArrayList<>();
        if(searching == null) {
            for(SimpleWarp value : manager.getWarps().values()) {
                warps.add(value.getName());
            }
        } else {
            warps = new ArrayList<>();

            for(SimpleWarp value : manager.getWarps().values()) {
                if(ChatColor.stripColor(value.getName()).toLowerCase().contains(searching.toLowerCase())) {
                    warps.add(value.getName());
                }
            }
        }

        for(int i = page * 44; i < 44 + page * 44; i++) {
            if(warps.size() <= i) break;

            addWarp(this, warps.get(i), searching);
        }
    }

    public static ItemStack getIcon(String name, String underline) {
        SimpleWarpManager manager = SimpleWarpManager.getInstance();
        SimpleWarp warp = manager.getWarp(name);

        String world = warp.getLocation().getWorldName();
        double x = round(warp.getLocation().getX());
        double y = round(warp.getLocation().getY());
        double z = round(warp.getLocation().getZ());

        return new ItemBuilder(XMaterial.ENDER_PEARL).setName(
                de.codingair.codingapi.utils.ChatColor.GRAY + "\"" + de.codingair.codingapi.utils.ChatColor.RESET
                        + de.codingair.codingapi.utils.ChatColor.highlight(name, underline, "§n")
                        + de.codingair.codingapi.utils.ChatColor.GRAY + "\" "
                        + "(\"" + world + "\", x=" + x + ", y=" + y + ", z=" + z + ")").getItem();
    }

    private static double round(double d) {
        return ((double) (int) (d * 100)) / 100;
    }

    private static float round(float d) {
        return ((float) (int) (d * 100)) / 100;
    }
}
