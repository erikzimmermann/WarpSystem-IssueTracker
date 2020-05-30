package de.codingair.warpsystem.spigot.features.portals.old.nativeportals;

import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.blocks.StaticLavaBlock;
import de.codingair.warpsystem.spigot.api.blocks.StaticWaterBlock;
import de.codingair.warpsystem.spigot.api.blocks.utils.Block;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import org.bukkit.Material;

public enum PortalType {
    WATER(BlockType.WATER, null, StaticWaterBlock.class, XMaterial.WATER_BUCKET.parseMaterial()),
    LAVA(BlockType.LAVA, null, StaticLavaBlock.class, XMaterial.LAVA_BUCKET.parseMaterial()),
    NETHER(BlockType.NETHER, XMaterial.NETHER_PORTAL.parseMaterial(), null, XMaterial.FLINT_AND_STEEL.parseMaterial()),
    END(BlockType.END, XMaterial.END_PORTAL.parseMaterial(), XMaterial.END_GATEWAY.parseMaterial(true, false), null, XMaterial.END_PORTAL_FRAME.parseMaterial()),
    AIR(BlockType.AIR, XMaterial.AIR.parseMaterial(), null, null, XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial()),
    EDIT(null, XMaterial.END_STONE.parseMaterial(), null, null),
    ;

    private BlockType type;
    private Material blockMaterial;
    private Material verticalBlockMaterial;
    private Class<? extends Block> block;
    private Material item;

    PortalType(BlockType type, Material blockMaterial, Class<? extends Block> block, Material item) {
        this.type = type;
        this.blockMaterial = blockMaterial;
        this.verticalBlockMaterial = null;
        this.block = block;
        this.item = item;
    }

    PortalType(BlockType type, Material blockMaterial, Material verticalBlockMaterial, Class<? extends Block> block, Material item) {
        this.type = type;
        this.blockMaterial = blockMaterial;
        this.verticalBlockMaterial = verticalBlockMaterial;
        this.block = block;
        this.item = item;
    }

    public Material getVerticalBlockMaterial() {
        return verticalBlockMaterial;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    public Class<? extends Block> getBlock() {
        return block;
    }

    public Material getItem() {
        return item;
    }

    public BlockType getType() {
        return type;
    }
}
