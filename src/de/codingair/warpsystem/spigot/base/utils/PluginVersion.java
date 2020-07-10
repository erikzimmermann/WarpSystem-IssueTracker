package de.codingair.warpsystem.spigot.base.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;

public enum PluginVersion {
    UNKNOWN,
    v4_2_6,
    v4_2_7,
    v4_2_8,
    v4_2_9,
    ;

    public static final PluginVersion[] values = values();
    public static PluginVersion current = null;
    public static PluginVersion old = null;
    public static PluginVersion upcoming = null;

    public static PluginVersion getVersion(String version) {
        version = version.split("-", -1)[0];
        if(!version.startsWith("v")) version = "v" + version;
        version = version.replace(".", "_");
        try {
            return valueOf(version);
        } catch(Exception ex) {
            return UNKNOWN;
        }
    }

    public static PluginVersion getOld() {
        if(old == null) old = getVersion(WarpSystem.getInstance().getOldVersion());
        return old;
    }

    public static PluginVersion getCurrent() {
        if(current == null) current = getVersion(WarpSystem.getInstance().getDescription().getVersion());
        return current;
    }

    public static PluginVersion getUpcoming() {
        if(upcoming == null) upcoming = values[getCurrent().ordinal() + 1];
        return upcoming;
    }

    public PluginVersion previous() {
        int i = ordinal() - 1;
        if(i <= 0) i = values().length - 1;
        return values[i];
    }

    public PluginVersion next() {
        int i = ordinal() + 1;
        if(i >= values.length) i = 1;
        return values[i];
    }


    @Override
    public String toString() {
        return name().replace("_", ".");
    }
}
