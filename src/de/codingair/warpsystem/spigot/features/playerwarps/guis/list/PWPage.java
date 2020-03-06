package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Head;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PWPage extends Page {
    private int size, maxPage = 0, page = 0;
    protected FilterType filter;
    protected Object[] extra;
    protected String search;

    public String getTitle() {
        return "§c" + Lang.get("Player_Warps") + " §8(" + filter.getFilterName() + " " + (page + 1) + "/" + (maxPage + 1) + ")";
    }

    public PWPage(Player p, int size) {
        super(p, "§c" + Lang.get("Player_Warps"), false);

        this.size = size;

        filter = FilterType.OWN_WARPS;
        extra = new Object[] {p};
        updateTitle();

        initialize(p);
    }

    public void updateTitle() {
        setTitle(getTitle(), true);
    }

    public void resetPage() {
        page = 0;
        initialize(getLast().getPlayer());
        getLast().changePage(PWPage.this, true);
    }

    @Override
    public void initialize(Player p) {
        getButtons().clear();
        Node<List<Button>, Integer> data = filter.getListItems((size / 9) * 7, page, p, search, extra);
        maxPage = (int) Math.floor(data.getValue() / ((size / 9) * 7));
        updateTitle();

        int slot = 0;
        for(Button button : data.getKey()) {
            button.setSlot(slot++);
            addButton(button);
            button.setOption(new StandardButtonOption());

            if(slot == 7 || slot == 16 || slot == 25 || slot == 34 || slot == 43 || slot == 52 || slot == 61) slot += 2;
        }

        if(filter.createButtonInList()) addButton(new SyncButton(slot) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NETHER_STAR).setName((PlayerWarpManager.hasPermission(p) ? "§b" + Lang.get("Create") : "§7" + Lang.get("Create") + " (§c" + Lang.get("Maximum_reached") + "§7)")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                CPlayerWarps.createPlayerWarp(player, getInterface());
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && PlayerWarpManager.hasPermission(p);
            }
        }.setOption(new StandardButtonOption()));

        //arrow up
        addButton(new SyncButton(8, 0) {
            @Override
            public ItemStack craftItem() {
                return (page == 0 ? Head.GRAY_ARROW_UP : Head.CYAN_ARROW_UP).getItemBuilder().setName((page == 0 ? "§7" : "§b") + Lang.get("Previous_Page") + " §7(" + (page + 1) + "/" + (maxPage + 1) + ")").getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                page--;
                initialize(p);
                getLast().changePage(PWPage.this, true);
            }

            @Override
            public boolean canClick(ClickType click) {
                return page > 0;
            }
        }.setOption(new StandardButtonOption()));

        //filter
        FilterButton b = filter.getControllButton(this, data.getValue());
        if(b != null) {
            b.setSlot(17);
            b.setOption(new StandardButtonOption());
            b.update();
            addButton(b);
        } else addButton(new FilterButton(this));

        //arrow down
        addButton(new SyncButton(8, 2) {
            @Override
            public ItemStack craftItem() {
                return (maxPage <= page ? Head.GRAY_ARROW_DOWN : Head.CYAN_ARROW_DOWN).getItemBuilder().setName((maxPage <= page ? "§7" : "§b") + Lang.get("Next_Page") + " §7(" + (page + 1) + "/" + (maxPage + 1) + ")").getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                page++;
                resetPage();
            }

            @Override
            public boolean canClick(ClickType click) {
                return maxPage > page;
            }
        }.setOption(new StandardButtonOption()));
    }

    public Object[] getExtra() {
        return extra;
    }

    public void setExtra(boolean update, Object... extra) {
        this.extra = extra;

        if(update) resetPage();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public FilterType getFilter() {
        return filter;
    }

    public static class FilterButton extends SyncAnvilGUIButton {
        protected PWPage page;

        public FilterButton(PWPage page) {
            super(8, 1, ClickType.SHIFT_RIGHT);
            this.page = page;
            update(false);

            setOption(new StandardButtonOption());
        }

        @Override
        public void onClick(AnvilClickEvent e) {
            e.setClose(true);

            page.setSearch(e.getInput().toLowerCase());
            page.resetPage();
        }

        @Override
        public void onClose(AnvilCloseEvent e) {
        }

        @Override
        public ItemStack craftItem() {
            if(page == null) return null;

            return new ItemBuilder(XMaterial.COMPASS).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Filter") + ":§7 " + page.filter.getFilterName())
                    .setLore(page.getSearch() == null || !page.filter.searchable(page) ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Search_Short") + ": §7'§f" + page.getSearch() + "§7'",
                            "", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Previous_Filter"),
                            Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Next_Filter"),
                            page.filter.searchable(page) ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §7" + (page.getSearch() == null ? Lang.get("Search_Short") : Lang.get("Reset_Search")) : null)
                    .getItem();
        }

        @Override
        public ItemStack craftAnvilItem(ClickType trigger) {
            return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Search")).getItem();
        }

        @Override
        public void onOtherClick(InventoryClickEvent e) {
            if(e.isShiftClick() && e.isRightClick()) page.setSearch(null);
            else if(page.filter.deleteExtraBeforeChangeFilter() && page.extra != null) page.extra = null;
            else if(e.isLeftClick()) {
                page.filter = page.filter.previous();
                page.extra = page.filter.getStandardExtra((PWList) page.getLast());
            } else if(e.isRightClick()) {
                page.filter = page.filter.next();
                page.extra = page.filter.getStandardExtra((PWList) page.getLast());
            }

            page.resetPage();
        }

        @Override
        public void onTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
            if(page.search == null) super.onTrigger(e, trigger, player);
            else onOtherClick(e);
        }

        @Override
        public boolean canClick(ClickType click) {
            return click == ClickType.LEFT || click == ClickType.RIGHT || (page.filter.searchable(page) && click == ClickType.SHIFT_RIGHT);
        }
    }
}
