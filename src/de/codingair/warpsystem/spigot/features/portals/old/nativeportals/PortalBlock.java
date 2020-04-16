package de.codingair.warpsystem.spigot.features.portals.old.nativeportals;

import de.codingair.codingapi.tools.Location;

public class PortalBlock {
    private Location loc;

    public PortalBlock(Location loc) {
        this.loc = loc.clone();
        this.loc.trim(0);

        try {
            this.loc.getBlock();
        } catch(Throwable t) {
        }
    }

    public Location getLocation() {
        return loc;
    }
}
