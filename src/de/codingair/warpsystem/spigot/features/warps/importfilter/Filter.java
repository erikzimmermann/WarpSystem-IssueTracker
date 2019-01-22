package de.codingair.warpsystem.spigot.features.warps.importfilter;

import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;

import java.util.List;

public interface Filter {
    Result importData();
    List<String> loadWarpNames();
    SimpleWarp loadWarp(String link);
}
