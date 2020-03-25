package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.FilterType;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.Category;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Head;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassesFilter implements Filter {
    @Override
    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        List<Category> selected = extra != null && extra.length == 1 && extra[0] instanceof List<?> ? (List<Category>) extra[0] : null;
        List<Category> chosen = extra != null && extra.length == 2 && extra[1] instanceof List<?> ? (List<Category>) extra[1] : null;

        List<Button> buttons = new ArrayList<>();
        List<Category> classes = PlayerWarpManager.getManager().getWarpClasses();

        int max = (page + 1) * maxSize;
        int i, amount, noMatch = 0;
        if(chosen != null) {
            List<PlayerWarp> warps = PlayerWarpManager.getManager().filter(chosen, player);
            warps.sort(Comparator.comparing(o -> o.getName(false).toLowerCase()));

            amount = warps.size();

            for(i = page * maxSize; i < max; i++) {
                if(warps.size() <= i) break;
                PlayerWarp w = warps.get(i);

                if(search != null && !w.getName(false).toLowerCase().contains(search)) {
                    noMatch++;
                    continue;
                }

                SyncButton b = new SyncButton(0) {
                    @Override
                    public ItemStack craftItem() {
                        return w.getItem(search).addLore("§8§m                         ", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Teleport"),
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Back"))
                                .getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(e.isLeftClick()) {
                            ((PWList) getInterface()).getMain().setExtra(true, (Object[]) null);
                            w.perform(player);
                        } else ((PWList) getInterface()).getMain().setExtra(true, chosen);
                    }

                    @Override
                    public boolean canClick(ClickType click) {
                        return click == ClickType.LEFT || click == ClickType.RIGHT;
                    }
                };

                buttons.add(b);
            }
        } else {
            amount = classes.size();

            for(i = page * maxSize; i < max; i++) {
                if(classes.size() <= i) break;

                Category c = classes.get(i);
                SyncButton b = new SyncButton(0) {
                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = c.getBuilder().clone()
                                .addLore("");

                        if(selected != null && selected.contains(c)) {
                            builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            builder.setHideEnchantments(true);
                            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c" + Lang.get("Deselect"));
                        } else builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Select"));

                        return builder.getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(selected == null) {
                            ((PWList) getInterface()).getMain().setExtra(true, new ArrayList<Category>() {{
                                add(c);
                            }});
                            return;
                        } else if(selected.contains(c)) {
                            selected.remove(c);
                            if(selected.isEmpty()) {
                                ((PWList) getInterface()).getMain().setExtra(true, (Object[]) null);
                                return;
                            }
                        } else selected.add(c);

                        ((PWList) getInterface()).getMain().setExtra(true, selected);
                    }

                    @Override
                    public boolean canClick(ClickType click) {
                        return click == ClickType.LEFT;
                    }
                };

                buttons.add(b);
            }
        }

        return new Node<>(buttons, amount - noMatch);
    }

    @Override
    public boolean createButtonInList() {
        return false;
    }

    @Override
    public boolean deleteExtraBeforeChangeFilter() {
        return true;
    }

    @Override
    public Object[] getStandardExtra(PWList list) {
        return null;
    }

    @Override
    public PWPage.FilterButton getControllButton(PWPage page, int warps) {
        if(page.getExtra() == null) return null;

        return new ExtendedFilterButton(page, warps);
    }

    @Override
    public boolean searchable(PWPage page) {
        return page.getExtra() != null && page.getExtra().length == 2;
    }

    public static class ExtendedFilterButton extends PWPage.FilterButton {
        private int warps = -1;

        public ExtendedFilterButton(PWPage page, int warps) {
            super(page);
            this.warps = warps;
            update(false);
        }

        @Override
        public ItemStack craftItem() {
            if(page == null || warps == -1) return null;

            Object[] extra = page.getExtra();

            return (extra.length == 1 ? Head.CYAN_PLUS : Head.RED_PLUS).getItemBuilder()
                    .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Filter") + ":§7 " + FilterType.CLASSES.getFilterName())
                    .setLore(page.getSearch() == null || !page.getFilter().searchable(page) ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Search_Short") + ": §7'§f" + page.getSearch() + "§7'",
                            extra.length == 1 ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Warps") + ": §f" + warps,
                            "",
                            extra.length == 1 ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + "§a" + Lang.get("Accept") : null,
                            Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": " + "§c" + (extra.length == 1 ? Lang.get("Reset") : Lang.get("Back")),
                            page.getFilter().searchable(page) ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §7" + (page.getSearch() == null ? Lang.get("Search_Short") : Lang.get("Reset_Search")) : null)
                    .getItem();
        }

        @Override
        public void onOtherClick(InventoryClickEvent e) {
            if(e.isShiftClick() && e.isRightClick()) page.setSearch(null);
            else if(e.isLeftClick()){
                Object[] extra = page.getExtra();
                if(extra.length == 1) page.setExtra(true, null, extra[0]);
                else page.setExtra(true, extra[1]);
            } else if(e.isRightClick()) {
                page.setExtra(true, (Object[]) null);
            }

            page.resetPage();
        }

        @Override
        public boolean canClick(ClickType click) {
            Object[] extra = page.getExtra();
            return (extra.length == 1 && click == ClickType.LEFT) || click == ClickType.RIGHT || (page.getFilter().searchable(page) && click == ClickType.SHIFT_RIGHT);
        }
    }
}
