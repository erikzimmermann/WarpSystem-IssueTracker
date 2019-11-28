package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils;

import de.codingair.codingapi.server.Color;
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

    public Icon(String name, ItemStack item, Icon page, int slot, String permission, List<ActionObject> actions) {
        super(permission, false, actions);
        this.name = name;
        this.item = item;
        this.page = page;
        this.slot = slot;
    }

    public Icon(String name, ItemStack item, Icon page, int slot, String permission, ActionObject... actions) {
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
    public boolean read(JSONObject json) throws Exception {
        super.read(json);

        this.name = json.get("name") == null ? null : (String) json.get("name");
        ItemBuilder builder = ItemBuilder.getFromJSON(json.get("item", true));
        this.item = builder.getItem();

        this.slot = Integer.parseInt(json.get("slot") + "");

        if(json.get("isCategory") != null) {
            this.isPage = Boolean.parseBoolean(json.get("isCategory") + "");
        } else {
            this.isPage = Boolean.parseBoolean(json.get("isPage") + "");
        }

        if(json.get("category") != null) {
            this.page = json.get("category") == null ? null : IconManager.getInstance().getPage(json.get("category"));
        } else {
            this.page = json.get("page") == null ? null : IconManager.getInstance().getPage(json.get("page"));
        }

        return true;
    }

    @Override
    public void write(JSONObject json) {
        super.write(json);

        ItemBuilder builder = new ItemBuilder(this.item);
        json.put("name", this.name);
        json.put("item", builder.toJSONString());
        json.put("slot", this.slot);
        json.put("isPage", this.isPage);
        json.put("page", this.page == null ? null : this.page.getName());
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Icon)) return false;
        Icon icon = (Icon) o;
        return super.equals(o) &&
                slot == icon.slot &&
                isPage == icon.isPage &&
                getNameWithoutColor().equals(icon.getNameWithoutColor()) &&
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
