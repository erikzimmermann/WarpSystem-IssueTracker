package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters.*;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import org.bukkit.entity.Player;

import java.util.List;

public enum FilterType {
    OWN_WARPS(OwnWarpFilter.class, Lang.get("Filter_Own_Warps"), 0, true),
    ALL_WARPS(AllWarps.class, Lang.get("Filter_All_Warps"), 1, PlayerWarpManager.getManager().isAllowPublicWarps() || PlayerWarpManager.getManager().isAllowTrustedMembers()),
    ALL_PLAYERS(AllPlayers.class, Lang.get("Filter_All_Players"), 2, PlayerWarpManager.getManager().isAllowPublicWarps() || PlayerWarpManager.getManager().isAllowTrustedMembers()),
    CLASSES(ClassesFilter.class, Lang.get("Filter_Classes"), 3, true);

    private Filter instance;
    private final String filterName;
    private final int id;
    private final boolean enabled;

    FilterType(Class<? extends Filter> clazz, String filterName, int id, boolean enabled) {
        try {
            this.instance = clazz.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.filterName = filterName;
        this.id = id;
        this.enabled = enabled;
    }

    public static int active(Player player) {
        int i = 0;
        for(FilterType value : values()) {
            if(value == CLASSES && player != null && player.hasPermission(WarpSystem.PERMISSION_MODIFY_PLAYER_WARPS)) i++;
            else if(value != CLASSES && value.isEnabled()) i++;
        }
        return i;
    }

    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        return instance.getListItems(maxSize, page, player, search, extra);
    }

    public FilterType next(Player player) {
        int next = id + 1;
        if(next >= values().length) next = 0;

        while(!values()[next].isEnabled() || (values()[next] == CLASSES && !player.hasPermission(WarpSystem.PERMISSION_MODIFY_PLAYER_WARPS))) {
            next++;
            if(next >= values().length) next = 0;
        }

        return values()[next];
    }

    public FilterType previous(Player player) {
        int previous = id - 1;
        if(previous < 0) previous = values().length - 1;

        while(!values()[previous].isEnabled() || (values()[previous] == CLASSES && !player.hasPermission(WarpSystem.PERMISSION_MODIFY_PLAYER_WARPS))) {
            previous--;
            if(previous < 0) previous = values().length - 1;
        }

        return values()[previous];
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean deleteExtraBeforeChangeFilter() {
        return instance.deleteExtraBeforeChangeFilter();
    }

    public PWPage.FilterButton getControllButton(PWPage page, int warps, Player player) {
        return instance.getControllButton(page, warps, player);
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

    public boolean isEnabled() {
        return enabled;
    }
}
