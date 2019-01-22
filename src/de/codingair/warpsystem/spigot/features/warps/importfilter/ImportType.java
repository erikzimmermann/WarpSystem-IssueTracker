package de.codingair.warpsystem.spigot.features.warps.importfilter;

import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.importfilter.filters.CategoryWarpsFilter;
import de.codingair.warpsystem.spigot.features.warps.importfilter.filters.EssentialsFilter;

import java.util.List;

public enum ImportType {
    ESSENTIALS(new EssentialsFilter()),
    CATEGORY_WARPS(new CategoryWarpsFilter());

    private Filter filter;

    ImportType(Filter filter) {
        this.filter = filter;
    }

    public Result importData() {
        return this.filter.importData();
    }

    public List<String> loadWarpNames() {
        return this.filter.loadWarpNames();
    }

    public SimpleWarp loadWarp(String link) {
        return this.filter.loadWarp(link);
    }
}
