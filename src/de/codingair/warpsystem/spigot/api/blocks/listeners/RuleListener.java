package de.codingair.warpsystem.spigot.api.blocks.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.tools.TimeList;
import de.codingair.warpsystem.spigot.api.blocks.StaticLavaBlock;
import de.codingair.warpsystem.spigot.api.blocks.utils.StaticBlock;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RuleListener implements Listener {
    private static TimeList<Entity> NO_DAMAGE;

    public RuleListener() {
        if(NO_DAMAGE == null) NO_DAMAGE = new TimeList<>();
    }

    public static void noDamageTo(Entity entity) {
        if(NO_DAMAGE != null) NO_DAMAGE.add(entity, 1);
    }

    @EventHandler
    public void onChange(BlockPhysicsEvent e) {
        List<StaticBlock> l = API.getRemovables(null, StaticBlock.class);
        for(StaticBlock loc : l) {
            if(!(loc instanceof StaticLavaBlock)) continue;

            StaticLavaBlock lava = (StaticLavaBlock) loc;
            org.bukkit.block.Block b = lava.getLocation().getBlock();
            if(b.getWorld().getName().equals(e.getBlock().getLocation().getWorld().getName()) && b.getLocation().getBlockX() == e.getBlock().getLocation().getBlockX() && b.getLocation().getBlockY() == e.getBlock().getLocation().getBlockY() && b.getLocation().getBlockZ() == e.getBlock().getLocation().getBlockZ()) {
                e.setCancelled(true);
            }
        }
        l.clear();
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        List<StaticBlock> l = API.getRemovables(null, StaticBlock.class);
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
        List<StaticLavaBlock> l = API.getRemovables(null, StaticLavaBlock.class);
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
    public void onHit(EntityCombustEvent e) {
        if(NO_DAMAGE.contains(e.getEntity())) {
            e.setCancelled(true);
            e.setDuration(0);
            e.getEntity().setFireTicks(-200);
            return;
        }

        if(!touchesKnownStaticBlocks(e.getEntity())) return;

        e.setDuration(0);
        e.getEntity().setFireTicks(-200);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageEvent e) {
        if(NO_DAMAGE.contains(e.getEntity())) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(-200);
            return;
        }

        if((e instanceof EntityDamageByBlockEvent && e.getCause().equals(EntityDamageByBlockEvent.DamageCause.LAVA)) || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            if(!touchesKnownStaticBlocks(e.getEntity())) return;

            e.setCancelled(true);
            e.setDamage(0);
            e.getEntity().setFireTicks(-200);
        }
    }

    private void addTo(List<Location> list, Location origin, Location toAdd) {
        if(!origin.getBlock().getLocation().equals(toAdd.getBlock().getLocation())) list.add(toAdd);
    }

    private List<Location> getBlocksAround(Entity e) {
        List<Location> locs = new ArrayList<>();
        double diff = 0.5;

        Location origin;

        for(double i = -diff; i <= diff; i += diff) {
            locs.add(origin = e.getLocation().add(0, i + 0.1, 0));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, 0)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, 0)));
            addTo(locs, origin, origin.clone().add(new Vector(0, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(0, 0, -diff)));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, -diff)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, -diff)));
        }

        return locs;
    }

    private boolean touchesKnownStaticBlocks(Entity e) {
        List<Location> locs = getBlocksAround(e);

        if(locs.isEmpty()) return false;

        boolean result = false;
        List<StaticBlock> l = API.getRemovables(null, StaticBlock.class);
        for(StaticBlock staticBlock : l) {
            if(result) break;

            for(Location loc : locs) {
                org.bukkit.block.Block b = staticBlock.getLocation().getBlock();

                if(b.getWorld().getName().equals(loc.getWorld().getName()) && b.getLocation().getBlockX() == loc.getBlockX() && b.getLocation().getBlockY() == loc.getBlockY() && b.getLocation().getBlockZ() == loc.getBlockZ()) {
                    result = true;
                    break;
                }
            }
        }
        l.clear();

        locs.clear();

        return result;
    }
}
