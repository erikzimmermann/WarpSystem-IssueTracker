package de.codingair.warpsystem.spigot.features.nativeportals.utils;

import de.codingair.codingapi.server.blocks.ModernBlock;
import de.codingair.codingapi.server.blocks.data.Orientable;
import de.codingair.codingapi.tools.Area;
import de.codingair.warpsystem.spigot.features.nativeportals.Portal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;

public class PortalBlock {
    private de.codingair.codingapi.tools.Location loc;

    public PortalBlock(Location loc) {
        loc = loc.getBlock().getLocation();
        this.loc = new de.codingair.codingapi.tools.Location(loc);
    }

    public de.codingair.codingapi.tools.Location getLocation() {
        return loc;
    }

    public void updateBlock(Portal portal) {
        if(portal.isVisible()) {
            if(portal.getType().getBlockMaterial() == null) {
                try {
                    portal.getType().getBlock().getConstructor(Location.class).newInstance(this.loc).create();
                } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                if(portal.getType() == PortalType.NETHER) {
                    new ModernBlock(this.loc.getBlock()).setTypeAndData(portal.getType().getBlockMaterial(), new Orientable(portal.getCachedAxis()));
                } else if(portal.getType() == PortalType.END) {
                    if(portal.isVertically() && portal.getType().getVerticalBlockMaterial() != null) {
                        this.loc.getBlock().setType(portal.getType().getVerticalBlockMaterial(), true);
                    } else this.loc.getBlock().setType(portal.getType().getBlockMaterial(), false);
                } else this.loc.getBlock().setType(portal.getType().getBlockMaterial(), false);
            }
        } else this.loc.getBlock().setType(Material.AIR, true);
    }

    public boolean touches(LivingEntity e) {
        return touches(e, e.getLocation());
    }

    public boolean touches(LivingEntity e, Location target) {
        return Area.isInBlock(e, target, loc.getBlock());
    }
}
