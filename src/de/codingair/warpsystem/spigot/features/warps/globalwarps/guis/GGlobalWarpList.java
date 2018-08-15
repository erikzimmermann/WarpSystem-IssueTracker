package de.codingair.warpsystem.spigot.features.warps.globalwarps.guis;

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
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
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

public class GGlobalWarpList extends GUI {
    public interface Listener {
        void onClickOnGlobalWarp(String warp, InventoryClickEvent e);
        void onClose();
        String getLeftclickDescription();
    }

    private static int MAX_PAGE() {
        if(WarpSystem.getInstance().getTeleportManager().getPortals().isEmpty()) return 0;

        return (int) (Math.ceil((double) WarpSystem.getInstance().getTeleportManager().getPortals().size() / 44.0) - 1);
    }

    private static String TITLE(int page) {
        return ChatColor.RED + "GlobalWarps " + ChatColor.GRAY + "- " + ChatColor.RED + "List " + ChatColor.GRAY + "(" + page + "/" + (MAX_PAGE() + 1) + ")";
    }

    private static void addGlobalWarps(GGlobalWarpList gui, String globalWarp, String underline) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());

        int slot = gui.firstEmpty();
        if(slot < 0 || slot > 53) return;

        ItemStack icon;
        ItemBuilder builder = new ItemBuilder(getIcon(globalWarp));

        if(underline != null) {
            builder.setName(de.codingair.codingapi.utils.ChatColor.GRAY + "\"" + de.codingair.codingapi.utils.ChatColor.RESET + de.codingair.codingapi.utils.ChatColor.highlight(globalWarp, underline, "§n") + de.codingair.codingapi.utils.ChatColor.GRAY + "\" ("+Lang.get("Target_Server", new Example("ENG", "Target-Server"), new Example("GER", "Ziel-Server"))+": \""+de.codingair.codingapi.utils.ChatColor.highlight(WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().get(globalWarp), underline, "§n", "§7")+"\")");
        }

        if(gui.getClickListener() != null) builder.setLore("", gui.getClickListener().getLeftclickDescription());

        icon = builder.getItem();

        gui.addButton(new ItemButton(slot, icon) {
            @Override
            public void onClick(InventoryClickEvent e) {
                gui.button = true;
                if(gui.getClickListener() != null) gui.getClickListener().onClickOnGlobalWarp(globalWarp, e);
                gui.button = false;
            }
        }.setOption(option));
    }

    private Listener listener;
    private String searching;
    private int page = 0;
    private boolean button = false;

    public GGlobalWarpList(Player p) {
        super(p, TITLE(1), 54, WarpSystem.getInstance());
    }

    public GGlobalWarpList(Player p, String search) {
        super(p, TITLE(1), 54, WarpSystem.getInstance(), false);

        this.searching = search;
        initialize(p);
    }

    public GGlobalWarpList(Player p, Listener listener) {
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
                if(!button) {
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

        ItemStack ph = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemBuilder search = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED.toString() + (searching == null ? "" : ChatColor.UNDERLINE) + Lang.get("Search", new Example("ENG", "Search..."), new Example("GER", "Suchen...")));
        if(searching != null) {
            search.addLore("", ChatColor.GRAY + "» " + Lang.get("Current") + ": '" + ChatColor.YELLOW + searching + ChatColor.GRAY + "'",
                    ChatColor.GRAY + "» " + Lang.get("Rightclick_To_Reset", new Example("ENG", "Rightclick to reset"), new Example("GER", "Rechtsklick zum resetten")));
        }

        setItem(2, ph);
        setItem(6, ph);
        addLine(2, 1, 6, 1, ph, true);

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setCloseOnClick(true);

        addButton(new ItemButton(3, new ItemBuilder(Skull.ArrowLeft).setName(ChatColor.GRAY + Lang.get("Previous_Page", new Example("ENG", "Previous Page"), new Example("GER", "Vorherige Seite"))).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(page == 0) return;
                page--;
                reinitialize(TITLE(page + 1));
            }
        });

        addButton(new ItemButton(5, new ItemBuilder(Skull.ArrowRight).setName(ChatColor.GRAY + Lang.get("Next_Page", new Example("ENG", "Next Page"), new Example("GER", "Nächste Seite"))).getItem()) {
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

        List<String> globalWarps;
        if(searching == null) {
            globalWarps = new ArrayList<>(WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().keySet());
        } else {
            globalWarps = new ArrayList<>();

            WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().forEach((warp, server) -> {
                if(ChatColor.stripColor(warp).toLowerCase().contains(searching.toLowerCase()) || server.toLowerCase().contains(searching.toLowerCase())){
                    globalWarps.add(warp);
                }
            });
        }

        for(int i = page * 44; i < 44 + page * 44; i++) {
            if(globalWarps.size() <= i) break;

            addGlobalWarps(this, globalWarps.get(i), searching);
        }
    }

    public static ItemStack getIcon(String name) {
        return new ItemBuilder(Material.ENDER_CHEST).setName(de.codingair.codingapi.utils.ChatColor.GRAY + "\"" + de.codingair.codingapi.utils.ChatColor.RESET + name + de.codingair.codingapi.utils.ChatColor.GRAY + "\" ("+Lang.get("Target_Server", new Example("ENG", "Target-Server"), new Example("GER", "Ziel-Server"))+": \""+WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().get(name)+"\")").getItem();
    }
}
