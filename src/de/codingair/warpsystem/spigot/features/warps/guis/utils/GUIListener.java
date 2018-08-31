package de.codingair.warpsystem.spigot.features.warps.guis.utils;

import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;

/**
 Doesn't support listening while player is editing
 */

public interface GUIListener {
    String getTitle();
    Task onClickOnWarp(Warp warp, boolean editing);
    void onClose();
}
