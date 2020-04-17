package de.codingair.warpsystem.spigot.features.portals.utils;

import org.bukkit.entity.Player;

public interface PortalListener {
    void onEnter(Player player);

    void onLeave(Player player);
}
