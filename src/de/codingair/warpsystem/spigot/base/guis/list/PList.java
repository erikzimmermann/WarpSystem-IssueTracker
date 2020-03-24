package de.codingair.warpsystem.spigot.base.guis.list;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Layout;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PList<E> extends Page {
    private static final int[] slots = {0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24};
    private List<ListItem<E>> items;
    private List<ListItem<E>> backup;

    private boolean searchable;
    private String title;
    private Player player;

    private int page = 0;
    private String searching = null;

    public PList(Player p, boolean searchable, String title) {
        super(p, title, new Layout(27) {
            @Override
            public void initialize() {
                this.addLine(7, 0, 7, 2, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem(), false);
                this.addLine(8, 0, 8, 2, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(), false);
            }
        }, false);

        this.player = p;
        this.searchable = searchable;
        this.title = title;
    }

    public void initList(List<ListItem<E>> items) {
        this.backup = new ArrayList<>(items);
        this.items = items;
        setTitle(TITLE(), true);
        initialize(player);
    }

    public void updateListItems() {
        this.items.clear();
        for(ListItem<E> i : this.backup) {
            if(getSearching() == null || i.isSearched(getSearching())) this.items.add(i);
        }

        for(int slot : slots) {
            ((SyncButton) getButton(slot)).update();
        }
    }

    @Override
    public void close() {
        super.close();
        this.items.clear();
        this.backup.clear();
    }

    private int MAX_PAGE() {
        if(items.isEmpty()) return 0;

        return (int) (Math.ceil((double) items.size() / slots.length) - 1);
    }

    private String TITLE() {
        return title.replace("%CURRENT%", page + 1 + "").replace("%MAX%", MAX_PAGE() + 1 + "");
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        for(int i = 0; i < slots.length; i++) {
            int finalI = i;
            addButton(new SyncButton(slots[finalI]) {
                @Override
                public ItemStack craftItem() {
                    ListItem<E> item = getListItem();
                    return item == null ? new ItemStack(Material.AIR) : item.buildItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    ListItem<E> item = getListItem();
                    if(item != null) item.onClick(item.getValue(), e.getClick());
                }

                private ListItem<E> getListItem() {
                    int id = getId();
                    return items.size() <= id ? null : items.get(getId());
                }

                private int getId() {
                    return finalI + slots.length * page;
                }
            }.setOption(option));
        }

        addButton(new Button(8, 0, new ItemBuilder(Skull.ArrowUp).setName(ChatColor.GRAY + Lang.get("Previous_Page")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(page == 0) return;
                page--;
                setTitle(TITLE(), true);
                updateListItems();
            }
        }.setOption(option));

        if(searchable) {
            addButton(new SyncButton(8, 1) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder search = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED.toString() + (searching == null ? "" : ChatColor.UNDERLINE) + Lang.get("Search"));

                    if(searching != null) {
                        search.addLore("", ChatColor.GRAY + "» " + Lang.get("Current") + ": '" + ChatColor.YELLOW + searching + ChatColor.GRAY + "'",
                                ChatColor.GRAY + "» " + Lang.get("Rightclick_To_Reset"));
                    }

                    return search.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isRightClick()) {
                        searching = null;
                        updateListItems();
                        update();
                    } else if(e.isLeftClick()) {
                        getLast().setClosingForGUI(true);

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
                                        updateListItems();
                                        update();
                                    }

                                    getLast().open();
                                });
                            }
                        }, new ItemBuilder(Material.PAPER).setName(Lang.get("Search")).getItem());
                    }
                }
            }.setOption(option).setOnlyLeftClick(false));
        }

        addButton(new Button(8, 2, new ItemBuilder(Skull.ArrowDown).setName(ChatColor.GRAY + Lang.get("Next_Page")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(page == MAX_PAGE()) return;
                page++;
                setTitle(TITLE(), true);
                updateListItems();
            }
        }.setOption(option));
    }

    public String getSearching() {
        return searching;
    }
}
