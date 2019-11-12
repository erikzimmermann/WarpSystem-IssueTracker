package de.codingair.warpsystem.spigot.features.tempwarps.utils;

import de.codingair.codingapi.tools.items.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Key {
    private String name;
    private double time;
    private ItemStack item;

    public Key(String name, double time, ItemStack item) {
        this.name = name;
        this.time = time;
        this.item = item;
    }

    public Key() {
    }

    public String getName() {
        return name;
    }

    public String getStrippedName() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name));
    }

    /**
     * Only, if the key wasn't added already!
     */
    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return (int) time;
    }

    public double getTimeExact() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public ItemStack getItem() {
        return new ItemBuilder(item).setName(this.name == null ? null : "§r" + ChatColor.translateAlternateColorCodes('&', this.name)).checkFirstLine().getItem();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(item).setName(this.name == null ? null : ChatColor.translateAlternateColorCodes('&', this.name));
    }

    public Key changeItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);
        this.item = new ItemBuilder(this.item).setType(item.getType()).setData(builder.getData()).getItem();
        return this;
    }

    public Key setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();

        json.put("Name", this.name);
        json.put("Time", getTime());
        json.put("Item", new ItemBuilder(this.item).toJSONString());

        return json.toJSONString();
    }

    public static Key getByJSONString(String s) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            String name = (String) json.get("Name");
            int time = Integer.parseInt(json.get("Time") + "");
            ItemStack item = ItemBuilder.getFromJSON((String) json.get("Item")).getItem();

            return new Key(name, time, item);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Key clone() {
        return new Key(this.name, this.time, this.item);
    }
}
