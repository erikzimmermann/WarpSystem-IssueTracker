package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Head;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PWPage extends Page {
    private int size, page = 0;
    private FilterType filter;
    private Object[] extra;

    public PWPage(Player p, int size) {
        super(p, "§c" + Lang.get("Player_Warps"), false);

        this.size = size;

        filter = FilterType.OWN_WARPS;
        extra = new Object[] {p};

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        getButtons().clear();
        Node<List<Button>, Boolean> data = filter.getListItems((size / 9) * 7, page, extra);

        int slot = 0;
        for(Button button : data.getKey()) {
            button.setSlot(slot++);
            addButton(button);

            if(slot % 7 == 0) slot += 2;
        }

        //arrow up
        addButton(new SyncButton(8, 0) {
            @Override
            public ItemStack craftItem() {
                return (page == 0 ? Head.GRAY_ARROW_UP : Head.CYAN_ARROW_UP).getItemBuilder().setName((page == 0 ? "§7" : "§b") + Lang.get("Previous_Page")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(page > 0) page--;
            }
        });

        //filter
        addButton(new SyncButton(8, 1) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.CHEST).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Filter") + ":§7 " + filter.getFilterName())
                        .setLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Previous_Filter"),
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Next_Filter"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(filter.deleteExtraBeforeChangeFilter() && extra != null) extra = null;
                else if(e.isLeftClick()) {
                    filter = filter.previous();
                    extra = filter.getStandardExtra((PWList) getLast());
                } else if(e.isRightClick()) {
                    filter = filter.next();
                    extra = filter.getStandardExtra((PWList) getLast());
                }

                initialize(p);
                getLast().changePage(PWPage.this, true);
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        });

        //arrow down
        addButton(new SyncButton(8, 2) {
            @Override
            public ItemStack craftItem() {
                return (!data.getValue() ? Head.GRAY_ARROW_DOWN : Head.CYAN_ARROW_DOWN).getItemBuilder().setName((!data.getValue() ? "§7" : "§b") + Lang.get("Next_Page")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(data.getValue()) page++;
            }
        });
    }

    public Object[] getExtra() {
        return extra;
    }

    public void setExtra(boolean update, Object... extra) {
        this.extra = extra;

        if(update) {
            initialize(getLast().getPlayer());
            getLast().changePage(PWPage.this, true);
        }
    }

    public static Button getCreateButton() {
        return new SyncButton(0) {
            @Override
            public ItemStack craftItem() {
                return Head.GRAY_PLUS.getItemBuilder().setName("§7" + Lang.get("Create")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                player.performCommand("playerwarps create");
            }
        };
    }
}
