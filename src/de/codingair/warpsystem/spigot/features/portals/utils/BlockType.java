package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.blocks.StaticLavaBlock;
import de.codingair.warpsystem.spigot.api.blocks.StaticWaterBlock;
import de.codingair.warpsystem.spigot.api.blocks.utils.Block;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BlockType {
    WATER(Lang.get("Water_Portal"), null, new ItemBuilder(XMaterial.BLUE_TERRACOTTA), StaticWaterBlock.class),
    LAVA(Lang.get("Lava_Portal"), null, new ItemBuilder(XMaterial.RED_TERRACOTTA), StaticLavaBlock.class),
    NETHER(Lang.get("Nether_Portal"), new ItemBuilder(XMaterial.NETHER_PORTAL), new ItemBuilder(XMaterial.PURPLE_TERRACOTTA), null),
    END(Lang.get("End_Portal"), new ItemBuilder(XMaterial.END_PORTAL), new ItemBuilder(XMaterial.END_GATEWAY), new ItemBuilder(XMaterial.BLACK_TERRACOTTA), null),
    AIR(Lang.get("Air_Portal"), new ItemBuilder(XMaterial.AIR), new ItemBuilder(XMaterial.WHITE_STAINED_GLASS), null),
    CUSTOM(Lang.get("Custom_Portal"), null, null, null);

    private String name;
    private ItemBuilder blockMaterial;    private ItemBuilder verticalBlockMaterial;
    private ItemBuilder editMaterial;
    private Class<? extends Block> block;

    BlockType(String name, ItemBuilder blockMaterial, ItemBuilder editMaterial, Class<? extends Block> block) {
        this.name = name;
        this.blockMaterial = blockMaterial;
        this.editMaterial = editMaterial;
        this.verticalBlockMaterial = null;
        this.block = block;
    }

    BlockType(String name, ItemBuilder blockMaterial, ItemBuilder verticalBlockMaterial, ItemBuilder editMaterial, Class<? extends Block> block) {
        this.name = name;
        this.blockMaterial = blockMaterial;
        this.verticalBlockMaterial = verticalBlockMaterial;
        this.editMaterial = editMaterial;
        this.block = block;
    }

    public static BlockType getByEditMaterial(ItemStack item) {
        if(item == null) return null;

        ItemBuilder builder = new ItemBuilder(item);

        for(BlockType value : values()) {
            if(Version.getVersion().isBiggerThan(Version.v1_12)) {
                if(!value.hasEditMaterial()) continue;
                if(builder.getType() == value.getExactEditMaterial()) return value;
            } else if(builder.equalsSimply(value.editMaterial)) return value;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public boolean hasEditMaterial() {
        return editMaterial != null && editMaterial.getType() != null;
    }

    public ItemBuilder getEditMaterial() {
        return editMaterial == null ? null : editMaterial.getType() == null ? null : editMaterial.clone();
    }

    public Material getExactEditMaterial() {
        return editMaterial == null ? null : editMaterial.getType();
    }

    public boolean hasVerticalBlockMaterial() {
        return verticalBlockMaterial != null && verticalBlockMaterial.getType() != null;
    }

    public ItemBuilder getVerticalBlockMaterial() {
        return verticalBlockMaterial == null ? null : verticalBlockMaterial.getType() == null ? null : verticalBlockMaterial.clone();
    }

    public Material getExactVerticalBlockMaterial() {
        return verticalBlockMaterial == null ? null : verticalBlockMaterial.getType();
    }

    public boolean hasBlockMaterial() {
        return blockMaterial != null && blockMaterial.getType() != null;
    }

    public ItemBuilder getBlockMaterial() {
        return blockMaterial == null ? null : blockMaterial.getType() == null ? null : blockMaterial.clone();
    }

    public Material getExactBlockMaterial() {
        return blockMaterial == null ? null : blockMaterial.getType();
    }

    public Class<? extends Block> getBlock() {
        return block;
    }
}
