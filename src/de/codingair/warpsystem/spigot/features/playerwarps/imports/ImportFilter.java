package de.codingair.warpsystem.spigot.features.playerwarps.imports;

import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;

import java.util.List;

public interface ImportFilter {
    List<PlayerWarp> importAll();
}
