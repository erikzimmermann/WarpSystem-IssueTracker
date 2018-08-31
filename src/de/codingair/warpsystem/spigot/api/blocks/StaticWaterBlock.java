package de.codingair.warpsystem.spigot.api.blocks;

import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.api.blocks.utils.StaticBlock;
import org.bukkit.Location;

public class StaticWaterBlock extends StaticBlock {
    public StaticWaterBlock(Location location) {
        super(location);
    }

    @Override
    public void create() {
        getLocation().getBlock().setType(XMaterial.WATER.parseMaterial());
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return StaticWaterBlock.class;
    }
}
