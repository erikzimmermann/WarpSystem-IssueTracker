package de.codingair.warpsystem.spigot.features.warps.importfilter;

import java.util.ArrayList;
import java.util.List;

public class PageData {
    private final String name;
    private final String permission;
    private final List<WarpData> warps = new ArrayList<>();

    public PageData(String name, String permission) {
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
