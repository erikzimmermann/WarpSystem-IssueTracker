package de.codingair.warpsystem.spigot.api.blocks;

import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.api.blocks.utils.StaticBlock;
import org.bukkit.Location;

public class StaticLavaBlock extends StaticBlock {
    private boolean spreadFire;
    private boolean hitEntity;

    public StaticLavaBlock(Location location) {
        super(location);
        spreadFire = false;
        hitEntity = false;
    }

    @Override
    public void create() {
        getLocation().getBlock().setType(XMaterial.LAVA.parseMaterial());
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return StaticLavaBlock.class;
    }

    public boolean isSpreadFire() {
        return spreadFire;
    }

    public StaticLavaBlock setSpreadFire(boolean spreadFire) {
        this.spreadFire = spreadFire;
        return this;
    }

    public StaticLavaBlock setHitEntity(boolean hitEntity) {
        this.hitEntity = hitEntity;
        return this;
    }

    public boolean canHitEntity() {
        return hitEntity;
    }
}
