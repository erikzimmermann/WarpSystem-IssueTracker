package de.codingair.warpsystem.spigot.features.randomteleports.utils;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldOption implements Serializable {
    private String worldName;
    private World world;
    private double startX, startY, startZ, min, max;

    public WorldOption(String worldName) {
        this.worldName = worldName;
        world = Bukkit.getWorld(worldName);
    }

    public WorldOption(String worldName, double startX, double startY, double startZ, int min, int max) {
        this(worldName);
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.startX = d.getDouble("startX", world == null ? 0 : world.getSpawnLocation().getX());
        this.startY = d.getDouble("startY", world == null ? 0 : world.getSpawnLocation().getY());
        this.startZ = d.getDouble("startZ", world == null ? 0 : world.getSpawnLocation().getZ());
        this.min = d.getDouble("min_range", RandomTeleporterManager.getInstance().getMinRange());
        this.max = d.getDouble("max_range", RandomTeleporterManager.getInstance().getMaxRange());
        return true;
    }

    @Override
    public String toString() {
        return "WorldOption{" +
                "worldName='" + worldName + '\'' +
                ", world=" + world +
                ", startX=" + startX +
                ", startY=" + startY +
                ", startZ=" + startZ +
                ", min=" + min +
                ", max=" + max +
                '}';
    }

    @Override
    public void write(DataWriter d) {
        //world value will be added in manager class
        d.put("startX", this.startX);
        d.put("startY", this.startY);
        d.put("startZ", this.startZ);
        d.put("min_range", this.min);
        d.put("max_range", this.max);
    }

    @Override
    public void destroy() {

    }

    public String getWorldName() {
        return worldName;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public void setStartZ(double startZ) {
        this.startZ = startZ;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
