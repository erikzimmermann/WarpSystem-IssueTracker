package de.codingair.warpsystem.spigot.features.tempwarps.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class EmptyTempWarp extends TempWarp {
    public EmptyTempWarp(Player player) {
        super(player.getName(), WarpSystem.getInstance().getUUIDManager().get(player), Location.getByLocation(player.getLocation()), null, null, null, null, null, null, -999, false, 0, 0, 0);
        TempWarpManager.getManager().getReserved().add(this);
    }

    public boolean canConvert() {
        return getName() != null && getDuration() > 0 && Bukkit.getPlayer(getLastKnownName()) != null;
    }

    public TempWarp convert() {
        return new TempWarp(Bukkit.getPlayer(getLastKnownName()), getLocation(), getName(), getDuration(), isPublic(), getMessage(), getTeleportCosts());
    }

    @Override
    public Date calculateEndDate() {
        throw new IllegalStateException("Not supported in EmptyTempWarps");
    }

    @Override
    public boolean isValid() {
        throw new IllegalStateException("Not supported in EmptyTempWarps");
    }

    @Override
    public boolean isSave() {
        throw new IllegalStateException("Not supported in EmptyTempWarps");
    }

    @Override
    public boolean isExpired() {
        throw new IllegalStateException("Not supported in EmptyTempWarps");
    }

    @Override
    public String toJSONString() {
        throw new IllegalStateException("Not supported in EmptyTempWarps");
    }
}
