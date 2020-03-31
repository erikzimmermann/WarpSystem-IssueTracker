package de.codingair.warpsystem.spigot.bstats;

import java.util.Map;

public interface Collectible {
    void collectOptionStatistics(Map<String, Integer> entry);

    default void addCustomCarts(Metrics metrics) {

    }
}
