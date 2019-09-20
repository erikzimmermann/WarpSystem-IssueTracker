package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import de.codingair.codingapi.tools.JSON.JSONObject;

import java.util.List;
import java.util.Objects;

public class Icon extends FeatureObject {
    private String name;
    private ItemStack item;
    private int slot;
    private Icon category;
    private boolean isCategory = false;

    public Icon() {
    }

    public Icon(Icon icon) {
        super(icon);
        this.name = icon.getName();
        this.item = icon.getItem().clone();
        this.slot = icon.getSlot();
        this.isCategory = icon.isCategory();
        this.slot = icon.getSlot();
        this.category = icon.getCategory();
    }

    public Icon(String name, ItemStack item, Icon category, int slot, String permission, List<ActionObject> actions) {
        super(permission, false, actions);
        this.name = name;
        this.item = item;
        this.category = category;
        this.slot = slot;
    }

    public Icon(String name, ItemStack item, Icon category, int slot, String permission, ActionObject... actions) {
        super(permission, false, actions);
        this.name = name;
        this.item = item;
        this.category = category;
        this.slot = slot;
    }

    public Icon clone() {
        return new Icon(this);
    }

    public int getDepth() {
        if(category == null) return 0;
        else return 1 + category.getDepth();
    }

    public void apply(Icon icon) {
        super.apply(icon);

        this.name = icon.getName();
        this.item = icon.getItem();
        this.slot = icon.getSlot();
        this.isCategory = icon.isCategory();
        this.slot = icon.getSlot();
        this.category = icon.getCategory();
    }

    @Override
    public void destroy() {
        super.destroy();

        this.item = null;
        this.slot = 0;
        this.isCategory = false;
        this.name = null;
        this.category = null;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        super.read(json);

        this.name = json.get("name") == null ? null : (String) json.get("name");
        ItemBuilder builder = ItemBuilder.getFromJSON((String) json.get("item"));
        this.item = builder.getItem();

        this.slot = Integer.parseInt(json.get("slot") + "");
        this.isCategory = Boolean.parseBoolean(json.get("isCategory") + "");

        this.category = json.get("category") == null ? null : IconManager.getInstance().getCategory((String) json.get("category"));
        return true;
    }

    @Override
    public void write(JSONObject json) {
        super.write(json);

        ItemBuilder builder = new ItemBuilder(this.item);
        json.put("name", this.name);
        json.put("item", builder.toJSONString());
        json.put("slot", this.slot);
        json.put("isCategory", this.isCategory);
        json.put("category", this.category == null ? null : this.category.getName());
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Icon)) return false;
        Icon icon = (Icon) o;
        return super.equals(o) &&
                slot == icon.slot &&
                isCategory == icon.isCategory &&
                name.equals(icon.name) &&
                item.equals(icon.item) &&
                Objects.equals(category, icon.category);
    }

    public String getName() {
        return name;
    }

    public String getNameWithoutColor() {
        return name == null ? null : ChatColor.stripColor(name);
    }

    public Icon setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStack getItem() {
        return new ItemBuilder(item).setName(this.name == null ? null : "§r" + ChatColor.translateAlternateColorCodes('&', this.name)).checkFirstLine().getItem();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(item).setName(this.name == null ? null : ChatColor.translateAlternateColorCodes('&', this.name));
    }

    public Icon changeItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);
        this.item = new ItemBuilder(this.item).setType(item.getType()).setData(builder.getData()).getItem();
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

    public Icon getCategory() {
        return category;
    }

    public Icon setCategory(Icon category) {
        this.category = category;
        return this;
    }

    public Icon addAction(ActionObject action) {
        return (Icon) super.addAction(action);
    }

    public boolean isCategory() {
        return isCategory;
    }

    public void setCategory(boolean category) {
        isCategory = category;
    }
}
