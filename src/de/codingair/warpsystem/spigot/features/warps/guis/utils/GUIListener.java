package de.codingair.warpsystem.spigot.features.warps.guis.utils;

import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Icon;

/**
 Doesn't support listening while player is editing
 */

public interface GUIListener {
    String getTitle();

    @Deprecated
    default Task onClickOnWarp(Warp warp, boolean editing) {
        return null;
    }

    default Task onClickOnIcon(Icon icon, boolean editing) {
        return null;
    }

    void onClose();
}
