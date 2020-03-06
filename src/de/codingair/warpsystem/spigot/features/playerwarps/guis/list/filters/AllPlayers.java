package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.FilterType;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Head;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AllPlayers implements Filter {
    @Override
    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        UUID special = null;
        if(extra != null && extra.length == 1 && extra[0] instanceof UUID) special = (UUID) extra[0];

        List<UUID> uuids = special == null ? new ArrayList<>(PlayerWarpManager.getManager().getUUIDs()) : null;
        List<PlayerWarp> publicWarps = special != null ? PlayerWarpManager.getManager().getUsableWarpsOf(special, player) : null;

        if(uuids != null) {
            uuids.sort(Comparator.comparing(o -> PlayerWarpManager.getManager().getWarps(o).get(0).getOwner().getName().toLowerCase()));
        } else if(publicWarps != null) {
            publicWarps.sort(Comparator.comparing(o -> o.getName(false).toLowerCase()));
        }

        List<Button> buttons = new ArrayList<>();

        int max = (page + 1) * maxSize;
        int i;
        int noMatch = 0, amount = 0;

        if(uuids != null) {
            amount = uuids.size();

            for(i = page * maxSize; i < max + noMatch; i++) {
                if(uuids.size() <= i) break;

                UUID id = uuids.get(i);
                int warps = PlayerWarpManager.getManager().getTrustedWarpAmountOf(id, player);

                if(warps == 0) {
                    noMatch++;
                    continue;
                }

                PlayerWarp.User user = PlayerWarpManager.getManager().getWarps(id).get(0).getOwner();

                if(search != null && !user.getName().toLowerCase().contains(search)) {
                    noMatch++;
                    continue;
                }

                SyncButton b = new SyncButton(0) {
                    @Override
                    public ItemStack craftItem() {
                        return new ItemBuilder(WarpSystem.getInstance().getHeadManager().getHead(id).buildProfile())
                                .setName(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Player") + ": §f" + ChatColor.highlight(user.getName(), search, "§e§n", "§f", true))
                                .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Warps") + ": §f" + warps,
                                        "", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §b" + Lang.get("Show"))
                                .getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        //list PlayerWarps of selected player
                        ((PWList) getInterface()).getMain().setExtra(true, id);
                    }

                    @Override
                    public boolean canClick(ClickType click) {
                        return click == ClickType.LEFT;
                    }
                };

                buttons.add(b);
            }
        } else if(publicWarps != null) {
            amount = publicWarps.size();

            for(i = page * maxSize; i < max + noMatch; i++) {
                if(publicWarps.size() <= i) break;
                PlayerWarp w = publicWarps.get(i);

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
                        ((PWList) getInterface()).getMain().setExtra(true, (Object[]) null);

                        if(e.isLeftClick()) w.perform(player);
                    }

                    @Override
                    public boolean canClick(ClickType click) {
                        return click == ClickType.LEFT || click == ClickType.RIGHT;
                    }
                };

                buttons.add(b);
            }
        }

        if(uuids != null) uuids.clear();
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
        return new ExtendedFilterButton(page);
    }

    @Override
    public boolean searchable(PWPage page) {
        return true;
    }

    public static class ExtendedFilterButton extends PWPage.FilterButton {
        public ExtendedFilterButton(PWPage page) {
            super(page);
        }

        @Override
        public ItemStack craftItem() {
            if(page == null) return null;

            return Head.RED_PLUS.getItemBuilder()
                    .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Filter") + ":§7 " + FilterType.ALL_PLAYERS.getFilterName())
                    .setLore(page.getSearch() == null || !page.getFilter().searchable(page) ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Search_Short") + ": §7'§f" + page.getSearch() + "§7'",
                            "", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c" + Lang.get("Back"),
                            page.getFilter().searchable(page) ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §7" + (page.getSearch() == null ? Lang.get("Search_Short") : Lang.get("Reset_Search")) : null)
                    .getItem();
        }

        @Override
        public void onOtherClick(InventoryClickEvent e) {
            if(e.isShiftClick() && e.isRightClick()) page.setSearch(null);
            else page.setExtra(true, (Object[]) null);
            page.resetPage();
        }

        @Override
        public boolean canClick(ClickType click) {
            return click == ClickType.LEFT || (page.getFilter().searchable(page) && click == ClickType.SHIFT_RIGHT);
        }
    }
}
