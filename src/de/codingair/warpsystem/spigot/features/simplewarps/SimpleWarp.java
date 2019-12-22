package de.codingair.warpsystem.spigot.features.simplewarps;

import de.codingair.codingapi.tools.io.DataWriter;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.Serializable;
import de.codingair.warpsystem.spigot.features.warps.importfilter.WarpData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleWarp implements Serializable {
    private String name;
    private String permission;
    private Location location;

    private Date created;
    private Date lastChange;
    private String lastChanger;
    private int teleports;

    private double costs;

    public SimpleWarp() {
    }

    public SimpleWarp(String s) throws Exception {
        apply(s);
    }

    public SimpleWarp(WarpData data) throws IllegalStateException {
        this.name = data.getName();
        this.permission = data.getPermission();
        this.location = new Location(data.getWorld(), data.getX(), data.getY(), data.getZ(), data.getYaw(), data.getPitch());

        this.created = new Date();
        this.lastChange = new Date();
        this.lastChanger = "System";
        this.costs = 0;
        this.teleports = 0;
    }

    public SimpleWarp(Player player, String name, String permission) {
        this.name = name;
        this.permission = permission;
        this.location = new Location(player.getLocation());

        this.created = new Date();
        this.lastChange = new Date();
        this.lastChanger = player.getName();
        this.teleports = 0;

        this.costs = 0;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.name = ((String) d.get("Name")).replace(" ", "_");
        this.permission = d.get("Permission");
        this.location = d.getLocation("Location");
        this.created = d.getDate("Created");
        this.lastChange = d.getDate("LastChange");
        this.lastChanger = d.get("LastChanger");
        this.teleports = d.getInteger("Teleports");
        this.costs = d.getDouble("Costs");
        return true;
    }

    @Override
    public void write(DataWriter d) {
        this.location.trim(4);

        d.put("Name", this.name);
        d.put("Permission", this.permission);
        d.put("Location", this.location);
        d.put("Created", this.created.getTime());
        d.put("LastChange", this.lastChange.getTime());
        d.put("LastChanger", this.lastChanger);
        d.put("Teleports", this.teleports);
        d.put("Costs", this.costs);
    }

    @Override
    public String toString() {
        JSON json = new JSON();
        write(json);
        return json.toJSONString();
    }

    @Override
    public void destroy() {

    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return ChatColor.translateAlternateColorCodes('&', name.replace("_", " "));
    }

    public String getName(boolean stripColors) {
        return stripColors ? ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)) : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getCreated() {
        return created;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public String getLastChanger() {
        return lastChanger;
    }

    public void setLastChanger(String lastChanger) {
        this.lastChanger = lastChanger;
    }

    public int getTeleports() {
        return teleports;
    }

    public void increaseTeleports() {
        if(this.teleports == Integer.MAX_VALUE) return;
        this.teleports++;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }

    private void apply(String s) throws Exception {
        JSON json = (JSON) new JSONParser().parse(s);
        read(json);
    }

    public void apply(SimpleWarp warp) {
        try {
            this.apply(warp.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public SimpleWarp clone() {
        try {
            return new SimpleWarp(toString());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasPermission() {
        return this.permission != null;
    }
}
