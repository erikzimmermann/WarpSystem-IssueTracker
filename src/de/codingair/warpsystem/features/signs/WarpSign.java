package de.codingair.warpsystem.features.signs;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.gui.affiliations.Warp;

public class WarpSign {
    private Location location;
    private Warp warp;

    public WarpSign(Location location, Warp warp) {
        this.location = location;
        this.warp = warp;
    }

    public Location getLocation() {
        return location;
    }

    public Warp getWarp() {
        return warp;
    }
}
