package de.codingair.warpsystem.spigot.gui.guis.utils;

import de.codingair.warpsystem.gui.affiliations.Warp;

/**
 Doesn't support listening while player is editing
 */

public interface GUIListener {
    String getTitle();
    Task onClickOnWarp(Warp warp, boolean editing);
}
