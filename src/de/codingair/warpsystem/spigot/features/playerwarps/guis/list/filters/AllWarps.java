package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AllWarps implements Filter {
    @Override
    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getPublicWarps();
        warps.sort(Comparator.comparing(o -> o.getName(false).toLowerCase()));

        List<Button> buttons = new ArrayList<>();

        int max = (page + 1) * maxSize;
        int i, noMatch = 0;
        for(i = page * maxSize; i < max + noMatch; i++) {
            if(warps.size() <= i) break;
            PlayerWarp w = warps.get(i);

            if(search != null && !w.getName(false).toLowerCase().contains(search)) {
                noMatch++;
                continue;
            }

            SyncButton b = new SyncButton(0) {
                @Override
                public ItemStack craftItem() {
                    return w.getItem(search).addLore("§8§m                         ", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Teleport"))
                            .getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    w.perform(player);
                }
            };

            buttons.add(b);
        }

        int size = warps.size();
        warps.clear();
        return new Node<>(buttons, size - noMatch);
    }

    @Override
    public boolean createButtonInList() {
        return false;
    }

    @Override
    public boolean deleteExtraBeforeChangeFilter() {
        return false;
    }

    @Override
    public Object[] getStandardExtra(PWList list) {
        return null;
    }

    @Override
    public PWPage.FilterButton getControllButton(PWPage page, int warps) {
        return null;
    }

    @Override
    public boolean searchable(PWPage page) {
        return true;
    }
}
