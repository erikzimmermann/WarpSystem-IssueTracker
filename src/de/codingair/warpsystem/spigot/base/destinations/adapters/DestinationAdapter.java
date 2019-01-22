package de.codingair.warpsystem.spigot.base.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import org.bukkit.entity.Player;

public interface DestinationAdapter {
    boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<Boolean> callback);

    String simulate(Player player, String id);

    double getCosts(String id);
}
