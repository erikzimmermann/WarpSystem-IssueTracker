package de.codingair.warpsystem.spigot.base.utils.placeholderapi;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public abstract class WarpSystemPlaceholderExpansion extends PlaceholderExpansion {
    private FeatureType type;

    public WarpSystemPlaceholderExpansion(FeatureType type) {
        this.type = type;
    }

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public boolean canRegister() {
        return type.isActive();
    }

    @Override
    public String getIdentifier() {
        return "warpsystem";
    }

    @Override
    public String getAuthor() {
        return WarpSystem.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return WarpSystem.getInstance().getDescription().getVersion();
    }

    public abstract String onRequest(Player player, String id);

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        return onRequest(p, params);
    }
}
