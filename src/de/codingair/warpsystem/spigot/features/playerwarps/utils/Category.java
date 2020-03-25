package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.codingapi.tools.items.ItemBuilder;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

public class Category implements Serializable {
    private ItemBuilder builder;
    private int id;

    public Category() {
    }

    public Category(ItemBuilder builder, String name, int id, List<String> description) {
        assert builder != null && builder.getType() != Material.AIR && name != null;

        this.builder = builder;
        this.id = id;
        this.builder.setName(name);
        this.builder.setLore(description);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.builder = new ItemBuilder();
        this.id = d.getInteger("id");
        return this.builder.read(d);
    }

    @Override
    public void write(DataWriter d) {
        this.builder.setHideStandardLore(false);
        this.builder.setAmount(0);
        this.builder.write(d);
        d.put("id", id);
    }

    @Override
    public void destroy() {
        this.builder.destroy();
    }

    public ItemBuilder getBuilder() {
        return builder.setAmount(1).setHideStandardLore(true);
    }

    public void setBuilder(ItemBuilder builder) {
        this.builder = builder;
    }

    public String getName() {
        return this.builder.getName();
    }

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
