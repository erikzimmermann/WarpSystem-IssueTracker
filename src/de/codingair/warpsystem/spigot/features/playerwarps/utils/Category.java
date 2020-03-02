package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.tools.io.DataWriter;
import de.codingair.codingapi.tools.io.Serializable;
import de.codingair.codingapi.tools.items.ItemBuilder;
import org.bukkit.Material;

import java.util.List;

public class Category implements Serializable {
    private ItemBuilder builder;

    public Category() {
    }

    public Category(ItemBuilder builder, String name, List<String> description) {
        assert builder != null && builder.getType() != Material.AIR && name != null;

        this.builder = builder;
        this.builder.setName(name);
        this.builder.setLore(description);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.builder = new ItemBuilder();
        return this.builder.read(d);
    }

    @Override
    public void write(DataWriter d) {
        this.builder.write(d);
    }

    @Override
    public void destroy() {
        this.builder.destroy();
    }

    public ItemBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(ItemBuilder builder) {
        this.builder = builder;
    }

    public String getName() {
        return this.builder.getName();
    }

    public void setName(String name) {
        this.builder.setName(name);
    }

    public List<String> getDescription() {
        return this.builder.getLore();
    }

    public void setDescription(List<String> description) {
        this.builder.setLore(description);
    }
}
