package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import org.bukkit.entity.Player;

import java.util.List;

public interface Filter {
    Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra);

    boolean createButtonInList();

    boolean deleteExtraBeforeChangeFilter();

    Object[] getStandardExtra(PWList list);

    PWPage.FilterButton getControllButton(PWPage page, int warps);

    boolean searchable(PWPage page);
}
