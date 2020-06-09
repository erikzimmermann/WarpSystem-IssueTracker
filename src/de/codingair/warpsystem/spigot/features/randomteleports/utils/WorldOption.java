package de.codingair.warpsystem.spigot.features.randomteleports.utils;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldOption implements Serializable {
    private final String worldName;
    private final World world;
    private double startX, startY, startZ, min, max;
    private List<String> target;

    public WorldOption(String worldName) {
        this.worldName = worldName;
        world = Bukkit.getWorld(worldName);
    }

    @Override
    public boolean read(DataWriter d) {
        if(target != null) target.clear();

        this.startX = d.getDouble("startX", world == null ? 0 : world.getSpawnLocation().getX());
        this.startY = d.getDouble("startY", world == null ? 0 : world.getSpawnLocation().getY());
        this.startZ = d.getDouble("startZ", world == null ? 0 : world.getSpawnLocation().getZ());
        this.min = d.getDouble("min_range", RandomTeleporterManager.getInstance().getDefValues().min);
        this.max = d.getDouble("max_range", RandomTeleporterManager.getInstance().getDefValues().max);
        this.target = d.getList("target_worlds");
        return true;
    }

    @Override
    public String toString() {
        return "WorldOption{" +
                "worldName=" + (worldName == null ? "null" : "'" + worldName + "'") +
                ", world=" + world +
                ", startX=" + startX +
                ", startY=" + startY +
                ", startZ=" + startZ +
                ", min=" + min +
                ", max=" + max +
                ", target_worlds=" + (this.target == null ? "null" : Arrays.toString(this.target.toArray(new String[0]))) +
                '}';
    }

    @Override
    public void write(DataWriter d) {
        throw new IllegalStateException("WorldOption data will not be saved.");
    }

    @Override
    public void destroy() {
        if(this.target != null) this.target.clear();
    }

    public void prepareStart(Location l, World execution) {
        l.setWorld(getRandomWorld(execution));

        if(usesWorldSpawn()) {
            Location spawn = execution.getSpawnLocation();
            l.setX(spawn.getX());
            l.setY(spawn.getY());
            l.setZ(spawn.getZ());
        } else {
            l.setX(getStartX());
            l.setY(getStartY());
            l.setZ(getStartZ());
        }
    }

    public String getWorldName() {
        return worldName;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public World getWorld() {
        return world;
    }

    private boolean usesWorldSpawn() {
        return startX == -1 && startY == -1 && startZ == -1;
    }

    private List<World> prepare() {
        List<World> l = new ArrayList<>();
        for(String s : this.target) {
            World w = Bukkit.getWorld(s);
            if(w != null) l.add(w);
        }
        return l;
    }

    public World getRandomWorld(World execution) {
        List<World> l = prepare();
        if(l.isEmpty()) return execution;

        execution = l.get((int) (Math.random() * l.size()));
        l.clear();
        return execution;
    }
}
