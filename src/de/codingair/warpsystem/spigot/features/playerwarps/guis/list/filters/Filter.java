package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;

import java.util.List;

public interface Filter {
    Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra);
    boolean createButtonInList();
    boolean deleteExtraBeforeChangeFilter();
    Object[] getStandardExtra(PWList list);
}
