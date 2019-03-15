package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils;

public interface Serializable {
    void read(String s) throws Exception;
    String write();

    void destroy();
}
