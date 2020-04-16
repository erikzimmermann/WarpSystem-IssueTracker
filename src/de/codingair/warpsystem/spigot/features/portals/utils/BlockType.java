package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.blocks.StaticLavaBlock;
import de.codingair.warpsystem.spigot.api.blocks.StaticWaterBlock;
import de.codingair.warpsystem.spigot.api.blocks.utils.Block;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Material;

public enum BlockType {
    WATER(Lang.get("Water_Portal"), null, XMaterial.BLUE_TERRACOTTA.parseMaterial(), StaticWaterBlock.class),
    LAVA(Lang.get("Lava_Portal"), null, XMaterial.RED_TERRACOTTA.parseMaterial(), StaticLavaBlock.class),
    NETHER(Lang.get("Nether_Portal"), XMaterial.NETHER_PORTAL.parseMaterial(), XMaterial.PURPLE_TERRACOTTA.parseMaterial(), null),
    END(Lang.get("End_Portal"), XMaterial.END_PORTAL.parseMaterial(), XMaterial.END_GATEWAY.parseMaterialSafely(), XMaterial.BLACK_TERRACOTTA.parseMaterial(), null),
    AIR(Lang.get("Air_Portal"), XMaterial.AIR.parseMaterial(), XMaterial.WHITE_STAINED_GLASS.parseMaterial(), null),
    CUSTOM(Lang.get("Custom_Portal"), null, null, null);

    private String name;
    private Material blockMaterial;
    private Material verticalBlockMaterial;
    private Material editMaterial;
    private Class<? extends Block> block;

    BlockType(String name, Material blockMaterial, Material editMaterial, Class<? extends Block> block) {
        this.name = name;
        this.blockMaterial = blockMaterial;
        this.editMaterial = editMaterial;
        this.verticalBlockMaterial = null;
        this.block = block;
    }

    BlockType(String name, Material blockMaterial, Material verticalBlockMaterial, Material editMaterial, Class<? extends Block> block) {
        this.name = name;
        this.blockMaterial = blockMaterial;
        this.verticalBlockMaterial = verticalBlockMaterial;
        this.editMaterial = editMaterial;
        this.block = block;
    }

    public static BlockType getByEditMaterial(Material editMaterial) {
        for(BlockType value : values()) {
            if(value.getEditMaterial() == editMaterial) return value;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public Material getEditMaterial() {
        return editMaterial;
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
}
