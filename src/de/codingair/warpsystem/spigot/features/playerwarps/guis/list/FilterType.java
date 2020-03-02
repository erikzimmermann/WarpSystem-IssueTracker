package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters.*;

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

    public Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra) {
        return instance.getListItems(maxSize, page, extra);
    }

    public FilterType next() {
        int next = id + 1;
        if(next >= values().length) next = 0;
        return values()[next];
    }

    public FilterType previous() {
        int previous = id - 1;
        if(previous < 0) previous = values().length - 1;
        return values()[previous];
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean deleteExtraBeforeChangeFilter() {
        return instance.deleteExtraBeforeChangeFilter();
    }

    public Object[] getStandardExtra(PWList list) {
        return instance.getStandardExtra(list);
    }
}
