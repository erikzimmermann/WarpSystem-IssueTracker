package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.warpsystem.spigot.base.utils.placeholderapi.WarpSystemPlaceholderExpansion;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerWarpPlaceholderExpansion extends WarpSystemPlaceholderExpansion {
    public PlayerWarpPlaceholderExpansion() {
        super(FeatureType.PLAYER_WARS);
    }

    @Override
    public String onRequest(Player player, String id) {
        if(player == null) return null;
        id = id.toLowerCase();
        if(!id.startsWith("playerwarps_")) return null;
        id = id.replace("playerwarps_", "");

        List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps(player);

        switch(id) {
            case "count":
                return warps.size() + "";
            case "max":
                return PlayerWarpManager.getMaxAmount(player) + "";
        }

        return null;
    }
}
