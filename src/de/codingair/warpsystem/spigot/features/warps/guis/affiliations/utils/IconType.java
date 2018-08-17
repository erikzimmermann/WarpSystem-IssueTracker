package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;

public enum IconType {
    CATEGORY(Category.class),
    WARP(Warp.class),
    DECORATION(DecoIcon.class),
    GLOBAL_WARP(GlobalWarp.class);

    private Class<?> clazz;

    IconType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
