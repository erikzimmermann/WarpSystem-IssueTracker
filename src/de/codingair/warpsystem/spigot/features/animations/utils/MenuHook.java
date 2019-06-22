package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import org.bukkit.entity.Player;

public interface MenuHook {
    void onInitialize(HotbarGUI gui, Player player);
}
