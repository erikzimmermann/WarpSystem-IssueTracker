package de.codingair.warpsystem.gui.guis;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.ItemBuilder;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.features.portals.Portal;
import de.codingair.warpsystem.features.portals.PortalEditor;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GPortalList extends GUI {
    private static int MAX_PAGE() {
        if(WarpSystem.getInstance().getTeleportManager().getPortals().isEmpty()) return 0;

        return (int) (Math.ceil((double) WarpSystem.getInstance().getTeleportManager().getPortals().size() / 44.0) - 1);
    }

    private static String TITLE(int page) {
        return ChatColor.RED + "Portals " + ChatColor.GRAY + "- " + ChatColor.RED + "List " + ChatColor.GRAY + "(" + page + "/" + (MAX_PAGE() + 1) + ")";
    }

    private static void addPortal(GPortalList gui, Portal portal, String underline) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setCloseOnClick(true);

        int slot = gui.firstEmpty();
        if(slot < 0 || slot > 53) return;

        ItemStack icon;
        ItemBuilder builder = new ItemBuilder(portal.getIcon());

        if(underline != null) {
            StringBuilder name = new StringBuilder(builder.getName().replace(ChatColor.UNDERLINE.toString(), ""));

            //Prepare Underline (Upper-Case or Lower-Case?)
            int start = name.toString().toLowerCase().indexOf(underline.toLowerCase());
            StringBuilder prepared = new StringBuilder();
            for(int i = start; i < start + underline.length(); i++) {
                prepared.append(name.toString().charAt(i));
            }

            underline = prepared.toString();

            //Underline String-Parts
            String[] a = name.toString().split(underline);

            name = new StringBuilder();
            String color = "";

            int i = 0;
            for(String s : a) {
                i++;

                String nextColor = ChatColor.getLastColors(s);
                color = nextColor.isEmpty() ? color : nextColor;

                name.append(s);
                if(a.length != i) name.append(ChatColor.UNDERLINE).append(underline).append(color);
            }

            builder.setName(name.toString());
        }

        builder.setLore("", ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + Lang.get("Portal_List_Leftclick_To_Edit", new Example("ENG", ChatColor.GRAY + "Leftclick: Edit"), new Example("GER", ChatColor.GRAY + "Linksklick: Editieren")));
        icon = builder.getItem();

        gui.addButton(new ItemButton(slot, icon) {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.getWhoClicked().teleport(portal.getStart());
                Sound.ENDERMAN_TELEPORT.playSound((Player) e.getWhoClicked());
                new PortalEditor((Player) e.getWhoClicked(), portal).start();
            }
        }.setOption(option));
    }

    private String searching;
    private int page = 0;

    public GPortalList(Player p) {
        this(p, null);
    }

    public GPortalList(Player p, String search) {
        super(p, TITLE(1), 54, WarpSystem.getInstance(), false);

        this.searching = search;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemStack ph = new ItemBuilder(Material.STAINED_GLASS_PANE).setHideName(true).setColor(DyeColor.BLACK).getItem();
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

        List<Portal> portalList;
        if(searching == null) {
            portalList = new ArrayList<>(WarpSystem.getInstance().getTeleportManager().getPortals());
        } else {
            portalList = new ArrayList<>();

            for(Portal portal : WarpSystem.getInstance().getTeleportManager().getPortals()) {
                if(ChatColor.stripColor(portal.getStartName()).toLowerCase().contains(searching.toLowerCase()) || ChatColor.stripColor(portal.getDestinationName()).toLowerCase().contains(searching.toLowerCase()))
                    portalList.add(portal);
            }
        }

        for(int i = page * 44; i < 44 + page * 44; i++) {
            if(portalList.size() <= i) break;

            addPortal(this, portalList.get(i), null);
        }
    }
}
