package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AllWarps implements Filter {
    @Override
    public Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra) {
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getPublicWarps();

        List<Button> buttons = new ArrayList<>();
        boolean hasNextPage = true;

        int max = (page + 1) * maxSize;
        int i;
        for(i = page * maxSize; i < max; i++) {
            if(warps.size() <= i) {
                hasNextPage = false;
                break;
            }

            PlayerWarp w = warps.get(i);
            SyncButton b = new SyncButton(0) {
                @Override
                public ItemStack craftItem() {
                    return w.getItem().getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    w.perform(player);
                }
            };

            buttons.add(b);
        }

        warps.clear();
        return new Node<>(buttons, hasNextPage);
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
}
