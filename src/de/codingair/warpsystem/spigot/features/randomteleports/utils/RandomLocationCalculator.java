package de.codingair.warpsystem.spigot.features.randomteleports.utils;

import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLocationCalculator implements Runnable {
    private Player player;
    private PermissionPlayer check;
    private Callback<Location> callback;
    private long start;

    public RandomLocationCalculator(Player player, Callback<Location> callback) {
        this.player = player;
        this.check = new PermissionPlayer(player);
        this.callback = callback;
    }

    @Override
    public void run() {
        Location location = null;
        try {
            location = calculate(this.player);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        if(location != null) {
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);
        }
        callback.accept(location);
    }

    private Location calculate(Player player) throws InterruptedException {
        start = System.currentTimeMillis();
        Location location = new Location(player.getLocation());
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();

        double minRange = RandomTeleporterManager.getInstance().getMinRange();
        double maxRange = RandomTeleporterManager.getInstance().getMaxRange();

        Random r = new Random();
        int i = 0;

        long maxTime = (long) ((maxRange - minRange) / 2);
        if(maxTime < 1000) maxTime = 1000;
        if(maxTime > 5000) maxTime = 5000;

        do {
            if(i == 0) i++;
            else if(i == 1) {
                player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Searching"));
                i++;
            }

            double xNext = r.nextDouble() * (maxRange - minRange) + minRange;
            double zNext = r.nextDouble() * (maxRange - minRange) + minRange;

            if(r.nextBoolean()) xNext *= -1;
            if(r.nextBoolean()) zNext *= -1;

            location.setX(x + xNext);
            location.setZ(z + zNext);

            if(start + maxTime < System.currentTimeMillis()) return null;
            if(correct(location, false)) location.setY(calculateYCoord(location));
        } while(!correct(location, true));

        return location;
    }

    private int calculateYCoord(Location location) {
        Location loc = location.clone();
        loc.setY(72);

        if(location.getWorld().getEnvironment() == World.Environment.NETHER) {
            boolean underRoof = false;
            int free = 0;

            while(free > 2 && underRoof) {
                if(underRoof) {
                    if(loc.getBlock().getType() == Material.AIR) free++;
                } else if(loc.getBlock().getType() != Material.AIR) underRoof = true;

                loc.setY(loc.getY() - 1);
            }

            while(loc.getBlock().getType() == Material.AIR) {
                loc.setY(loc.getY() - 1);
            }

            loc.setY(loc.getY() + 1);
            return loc.getBlockY();
        } else {
            if(loc.getBlock().getType() != Material.AIR) {
                while(loc.getBlock().getType() != Material.AIR) {
                    loc.setY(loc.getY() + 5);
                }

                while(loc.getBlock().getType() == Material.AIR) {
                    loc.setY(loc.getY() - 1);
                }

                loc.setY(loc.getY() + 1);
                return loc.getBlockY();
            } else {
                while(loc.getBlock().getType() == Material.AIR) {
                    loc.setY(loc.getY() - 5);
                }

                while(loc.getBlock().getType() != Material.AIR) {
                    loc.setY(loc.getY() + 1);
                }

                return loc.getBlockY();
            }
        }
    }

    private boolean correct(Location location, boolean safety) throws InterruptedException {
        if(safety && !isSafe(location)) return false;
        if(RandomTeleporterManager.getInstance().getBiomeList() != null && !RandomTeleporterManager.getInstance().getBiomeList().contains(location.getBlock().getBiome())) return false;
        if(RandomTeleporterManager.getInstance().isProtectedRegions() && isProtected(location)) return false;

        if(location.getBlock().getType().name().toLowerCase().contains("lava")) return false;
        Location below = location.clone();
        below.setY(below.getY() - 1);

        return !below.getBlock().getType().name().toLowerCase().contains("lava");
    }

    private boolean isSafe(Location location) {
        Block b = location.clone().subtract(0, 1, 0).getBlock();

        List<String> unsafe = new ArrayList<>();

        unsafe.add("VOID");
        unsafe.add("LAVA");
        unsafe.add("FIRE");

        for(String s : unsafe) {
            if(b.getType().name().toLowerCase().contains(s.toLowerCase())) return false;
        }

        return true;
    }

    private boolean isProtected(Location location) throws InterruptedException {
        synchronized(this) {
            BlockBreakEvent event = new BlockBreakEvent(location.getBlock(), this.check);

            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(event);

                synchronized(RandomLocationCalculator.this) {
                    RandomLocationCalculator.this.notify();
                }
            });

            this.wait();
            return event.isCancelled();
        }
    }
}
