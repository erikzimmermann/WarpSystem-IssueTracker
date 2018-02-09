package de.codingair.warpsystem.importfilter;

import de.codingair.warpsystem.importfilter.filters.CategoryWarpsFilter;
import de.codingair.warpsystem.importfilter.filters.EssentialsFilter;

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
}
