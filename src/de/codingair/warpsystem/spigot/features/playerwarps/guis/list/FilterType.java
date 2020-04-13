package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters.*;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import org.bukkit.entity.Player;

import java.util.List;

public enum FilterType {
    OWN_WARPS(OwnWarpFilter.class, Lang.get("Filter_Own_Warps"), 0),
    ALL_WARPS(AllWarps.class, Lang.get("Filter_All_Warps"), 1),
    ALL_PLAYERS(AllPlayers.class, Lang.get("Filter_All_Players"), 2),
    CLASSES(ClassesFilter.class, Lang.get("Filter_Classes"), 3);

    private Filter instance;
    private String filterName;
    private int id;

    FilterType(Class<? extends Filter> clazz, String filterName, int id) {
        try {
            this.instance = clazz.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.filterName = filterName;
        this.id = id;
    }

    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        return instance.getListItems(maxSize, page, player, search, extra);
    }

    public FilterType next() {
        int next = id + 1;
        if(next >= values().length - (PlayerWarpManager.getManager().isClasses() ? 0 : 1)) next = 0;
        return values()[next];
    }

    public FilterType previous() {
        int previous = id - 1;
        if(previous < 0) previous = values().length - 1 - (PlayerWarpManager.getManager().isClasses() ? 0 : 1);
        return values()[previous];
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean deleteExtraBeforeChangeFilter() {
        return instance.deleteExtraBeforeChangeFilter();
    }

    public PWPage.FilterButton getControllButton(PWPage page, int warps) {
        return instance.getControllButton(page, warps);
    }

    public Object[] getStandardExtra(PWList list) {
        return instance.getStandardExtra(list);
    }

    public boolean createButtonInList() {
        return instance.createButtonInList();
    }

    public boolean searchable(PWPage page) {
        return instance.searchable(page);
    }
}
