package de.codingair.warpsystem.spigot.features.nativeportals.utils;

import de.codingair.codingapi.server.blocks.ModernBlock;
import de.codingair.codingapi.server.blocks.data.Orientable;
import de.codingair.codingapi.tools.Area;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.codingapi.tools.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;

public class PortalBlock {
    private Location loc;
    private boolean valid = true;

    public PortalBlock(Location loc) {
        this.loc = loc.clone();
        this.loc.trim(0);

        try {
            this.loc.getBlock();
        } catch(Throwable t) {
            valid = false;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public de.codingair.codingapi.tools.Location getLocation() {
        return loc;
    }

    public void updateBlock(NativePortal nativePortal) {
        if(!isValid()) return;
        PortalType type = nativePortal.isEditMode() ? PortalType.EDIT : nativePortal.getType();

        if(nativePortal.isVisible()) {
            if(type.getBlockMaterial() == null) {
                try {
                    type.getBlock().getConstructor(org.bukkit.Location.class).newInstance(this.loc).create();
                } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                if(type == PortalType.NETHER) {
                    new ModernBlock(this.loc.getBlock()).setTypeAndData(type.getBlockMaterial(), new Orientable(nativePortal.getCachedAxis()));
                } else if(type == PortalType.END) {
                    if(nativePortal.isVertically() && type.getVerticalBlockMaterial() != null) {
                        this.loc.getBlock().setType(type.getVerticalBlockMaterial(), true);
                    } else this.loc.getBlock().setType(type.getBlockMaterial(), false);
                } else this.loc.getBlock().setType(type.getBlockMaterial(), false);
            }
        } else this.loc.getBlock().setType(Material.AIR, true);
    }

    public boolean touches(LivingEntity e) {
        return touches(e, e.getLocation());
    }

    public boolean touches(LivingEntity e, org.bukkit.Location target) {
        return isValid() && Area.isInBlock(e, target, loc.getBlock());
    }
}
