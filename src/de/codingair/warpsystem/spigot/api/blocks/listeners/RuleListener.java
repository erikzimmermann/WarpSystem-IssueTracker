package de.codingair.warpsystem.spigot.api.blocks.listeners;

import de.codingair.codingapi.API;
import de.codingair.warpsystem.spigot.api.blocks.StaticLavaBlock;
import de.codingair.warpsystem.spigot.api.blocks.utils.StaticBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RuleListener implements Listener {

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        List<StaticBlock> l = API.getRemovables(StaticBlock.class);

        for(StaticBlock loc : l) {
            org.bukkit.block.Block b = loc.getLocation().getBlock();
            if(b.getWorld().getName().equals(e.getBlock().getLocation().getWorld().getName()) && b.getLocation().getBlockX() == e.getBlock().getLocation().getBlockX() && b.getLocation().getBlockY() == e.getBlock().getLocation().getBlockY() && b.getLocation().getBlockZ() == e.getBlock().getLocation().getBlockZ()) {
                e.setCancelled(true);
            }
        }

        l.clear();
    }

    @EventHandler
    public void onBurn(BlockIgniteEvent e) {
        if(e.getIgnitingBlock() == null) return;
        List<StaticLavaBlock> l = API.getRemovables(StaticLavaBlock.class);

        for(StaticLavaBlock lava : l) {
            if(lava.isSpreadFire()) continue;

            org.bukkit.block.Block b = lava.getLocation().getBlock();
            if(b.getWorld().getName().equals(e.getIgnitingBlock().getLocation().getWorld().getName()) && b.getLocation().getBlockX() == e.getIgnitingBlock().getLocation().getBlockX() && b.getLocation().getBlockY() == e.getIgnitingBlock().getLocation().getBlockY() && b.getLocation().getBlockZ() == e.getIgnitingBlock().getLocation().getBlockZ()) {
                e.setCancelled(true);
            }
        }

        l.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageEvent e) {
        if((e instanceof EntityDamageByBlockEvent && e.getCause().equals(EntityDamageByBlockEvent.DamageCause.LAVA)) || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if(!touchesOnlyKnownLava(e.getEntity())) return;

            e.setCancelled(true);
            e.setDamage(0);
            e.getEntity().setFireTicks(0);
        }
    }

    private List<Location> getBlocksAround(Entity e) {
        List<Location> locs = new ArrayList<>();
        double diff = 0.3;

        locs.add(e.getLocation().clone());
        locs.add(e.getLocation().clone().add(new Vector(diff, 0, 0)));
        locs.add(e.getLocation().clone().add(new Vector(-diff, 0, 0)));
        locs.add(e.getLocation().clone().add(new Vector(0, 0, diff)));
        locs.add(e.getLocation().clone().add(new Vector(0, 0, -diff)));

        List<Block> finalLocs = new ArrayList<>();

        for(Location loc : locs) {
            if(!finalLocs.contains(loc.getBlock())) finalLocs.add(loc.getBlock());
        }

        locs.clear();

        for(Block finalLoc : finalLocs) {
            locs.add(finalLoc.getLocation());
        }

        finalLocs.clear();

        return locs;
    }

    private boolean isLava(Block block) {
        return block.getType().name().contains("LAVA");
    }

    private boolean touchesOnlyKnownLava(Entity e) {
        List<Location> locs = getBlocksAround(e);

        List<Block> isLava = new ArrayList<>();

        for(Location loc : locs) {
            if(isLava.contains(loc.getBlock())) continue;
            if(isLava(loc.getBlock())) isLava.add(loc.getBlock());
        }

        locs.clear();
        if(isLava.isEmpty()) return false;

        List<StaticLavaBlock> l = API.getRemovables(StaticLavaBlock.class);

        int i = 0;
        for(StaticLavaBlock lava : l) {
            for(Block loc : isLava) {
                org.bukkit.block.Block b = lava.getLocation().getBlock();

                if(b.getWorld().getName().equals(loc.getWorld().getName()) && b.getLocation().getBlockX() == loc.getLocation().getBlockX() && b.getLocation().getBlockY() == loc.getLocation().getBlockY() && b.getLocation().getBlockZ() == loc.getLocation().getBlockZ()) {
                    i++;
                }
            }
        }

        boolean isOnlyKnownLava = i == isLava.size();

        l.clear();
        isLava.clear();

        return isOnlyKnownLava;
    }
}
