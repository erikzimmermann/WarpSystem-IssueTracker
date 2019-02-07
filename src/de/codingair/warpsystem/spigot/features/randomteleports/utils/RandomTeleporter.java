package de.codingair.warpsystem.spigot.features.randomteleports.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

public class RandomTeleporter implements Runnable{
    private Player player;
    private long start;

    public RandomTeleporter(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        Location location = calculate(this.player);
        if(location != null) player.teleport(location);
        else System.out.println("DONE");
    }

    private Location calculate(Player player) {
        start = System.currentTimeMillis();
        Location location = new Location(player.getLocation());
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();

        double minRange = RandomTeleporterManager.getInstance().getMinRange();
        double maxRange = RandomTeleporterManager.getInstance().getMaxRange();

        Random r = new Random();

        do {
            if(start + 5000 < System.currentTimeMillis()) return null;

            double xNext = r.nextDouble() * (maxRange - minRange) + minRange;
            double zNext = r.nextDouble() * (maxRange - minRange) + minRange;

            if(r.nextBoolean()) x *= -1;
            if(r.nextBoolean()) z *= -1;

            location.setX(x + xNext);
            location.setZ(z + zNext);
            location.setY(100);
            location.setY(Environment.getNextBottomBlock(location).getY() + 1);

            while(location.getBlock().getType().name().toLowerCase().contains("water")) {
                location.setY(location.getY() + 1);
            }

        } while(!correct(location));

        return location;
    }

    private boolean correct(Location location) {
        if(!RandomTeleporterManager.getInstance().getBiomeList().contains(location.getBlock().getBiome())) return false;

        if(RandomTeleporterManager.getInstance().isWorldGuardSupport() && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            return !isInRegion(location);
        }

        return true;
    }

    private boolean isInRegion(Location location) {
        WorldGuardPlugin pl = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        RegionManager m = pl.getRegionManager(location.getWorld());

        if(m != null) {
            for(ProtectedRegion value : m.getRegions().values()) {
                if(value.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                    return true;
                }
            }
        }

        return false;
    }
}
