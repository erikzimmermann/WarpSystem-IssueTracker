package de.codingair.warpsystem.spigot.features.warps.guis.utils;

import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;

/**
 Doesn't support listening while player is editing
 */

public interface GUIListener {
    String getTitle();

    default Task onClickOnIcon(Icon icon, boolean editing) {
        return null;
    }

    void onClose();
}
