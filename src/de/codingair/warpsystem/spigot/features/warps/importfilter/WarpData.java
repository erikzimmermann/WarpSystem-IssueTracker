package de.codingair.warpsystem.spigot.features.warps.importfilter;

public class WarpData {
    private String name, category, permission, world;
    private double x, y, z;
    private float yaw, pitch;

    public WarpData(String name, String category, String permission, String world, double x, double y, double z, float yaw, float pitch) {
        this.name = name;
        this.category = category;
        this.permission = permission;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPermission() {
        return permission;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
