package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils;

import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Icon extends FeatureObject {
    private String name;
    private ItemStack item;
    private int slot;
    private Icon page;
    private boolean isPage = false;

    public Icon() {
    }

    public Icon(Icon icon) {
        super(icon);
        this.name = icon.getName();
        this.item = icon.getItem().clone();
        this.slot = icon.getSlot();
        this.isPage = icon.isPage();
        this.slot = icon.getSlot();
        this.page = icon.getPage();
    }

    public Icon(String name, ItemStack item, Icon page, int slot, String permission, List<ActionObject<?>> actions) {
        super(permission, false, actions);
        this.name = name;
        this.item = item;
        this.page = page;
        this.slot = slot;
    }

    public Icon(String name, ItemStack item, Icon page, int slot, String permission, ActionObject<?>... actions) {
        super(permission, false, actions);
        this.name = name;
        this.item = item;
        this.page = page;
        this.slot = slot;
    }

    public Icon clone() {
        return new Icon(this);
    }

    public int getDepth() {
        if(page == null) return 0;
        else return 1 + page.getDepth();
    }

    public void apply(Icon icon) {
        super.apply(icon);

        this.name = icon.getName();
        this.item = icon.getItem();
        this.slot = icon.getSlot();
        this.isPage = icon.isPage();
        this.slot = icon.getSlot();
        this.page = icon.getPage();
    }

    @Override
    public void destroy() {
        super.destroy();

        this.item = null;
        this.slot = 0;
        this.isPage = false;
        this.name = null;
        this.page = null;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        super.read(d);

        this.name = d.getString("name");
        this.item = d.getItemStack("item");

        this.slot = d.getInteger("slot");

        if(d.get("isCategory") != null) {
            this.isPage = Boolean.parseBoolean(d.get("isCategory") + "");
        } else {
            this.isPage = d.getBoolean("isPage");
        }

        if(d.get("category") != null) {
            this.page = d.get("category") == null ? null : IconManager.getInstance().getPage(d.get("category"));
        } else {
            this.page = d.get("page") == null ? null : IconManager.getInstance().getPage(d.get("page"));
        }

        return true;
    }

    @Override
    public void write(DataWriter d) {
        super.write(d);

        d.put("name", this.name);
        d.put("item", this.item);
        d.put("slot", this.slot);
        d.put("isPage", this.isPage);
        d.put("page", this.page == null ? null : this.page.getName());
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Icon)) return false;
        Icon icon = (Icon) o;
        return super.equals(o) &&
                slot == icon.slot &&
                isPage == icon.isPage &&
                Objects.equals(getNameWithoutColor(), icon.getNameWithoutColor()) &&
                item.equals(icon.item) &&
                Objects.equals(page, icon.page);
    }

    public String getName() {
        return name;
    }

    public String getNameWithoutColor() {
        return name == null ? null : Color.removeColor(Color.translateAlternateColorCodes('&', name));
    }

    public Icon setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStack getRaw() {
        return this.item;
    }

    public ItemStack getItem() {
        return getItemBuilder().getItem();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(item).setName(this.name == null ? null : "§r" + ChatColor.translateAlternateColorCodes('&', this.name)).setHideName(name == null);
    }

    public ItemBuilder getItemBuilderWithPlaceholders(Player player) {
        ItemBuilder builder = getItemBuilder().checkFirstLine();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if(builder.getName() != null) builder.setName(PlaceholderAPI.setPlaceholders(player, builder.getName()));

            if(builder.getLore() != null) {
                for(int i = 0; i < builder.getLore().size(); i++) {
                    builder.getLore().add(i, PlaceholderAPI.setPlaceholders(player, builder.getLore().remove(i)));
                }
            }
        }

        return builder;
    }

    public Icon changeItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);
        ItemBuilder base = new ItemBuilder(this.item);

        builder.setName(base.getName());
        builder.setLore(base.getLore());
        builder.setHideStandardLore(true);
        builder.setHideEnchantments(true);
        builder.setEnchantments(base.getEnchantments());

        this.item = builder.getItem();
        return this;
    }

    public Icon setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Icon setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public Icon getPage() {
        return page;
    }

    public Icon setPage(Icon page) {
        this.page = page;
        return this;
    }

    public Icon addAction(ActionObject action) {
        return (Icon) super.addAction(action);
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean category) {
        isPage = category;
    }
}
