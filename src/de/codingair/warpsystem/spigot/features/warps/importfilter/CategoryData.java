package de.codingair.warpsystem.spigot.features.warps.importfilter;

import java.util.ArrayList;
import java.util.List;

public class CategoryData {
    private String name, permission;
    private List<WarpData> warps = new ArrayList<>();

    public CategoryData(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<WarpData> getWarps() {
        return warps;
    }
}
