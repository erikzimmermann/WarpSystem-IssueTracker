package de.codingair.warpsystem.spigot.features.warps.guis.affiliations;

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
