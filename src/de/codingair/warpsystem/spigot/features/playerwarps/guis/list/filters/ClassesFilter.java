package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.Category;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClassesFilter implements Filter {
    @Override
    public Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra) {
        List<Button> buttons = new ArrayList<>();
        boolean hasNextPage = true;
        List<Category> classes = PlayerWarpManager.getManager().getWarpClasses();

        int max = (page + 1) * maxSize;
        int i;
        for(i = page * maxSize; i < max; i++) {
            if(classes.size() <= i) {
                hasNextPage = false;
                break;
            }

            Category c = classes.get(i);
            SyncButton b = new SyncButton(0) {
                @Override
                public ItemStack craftItem() {
                    return c.getBuilder().getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {

                }
            };

            buttons.add(b);
        }

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
