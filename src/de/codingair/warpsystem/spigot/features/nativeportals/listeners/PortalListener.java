package de.codingair.warpsystem.spigot.features.nativeportals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.nativeportals.Portal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.GEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWalk(PlayerWalkEvent e) {
        for(Portal portal : NativePortalManager.getInstance().getPortals()) {
            if(!portal.isVisible()) continue;

            boolean test0 = portal.isInPortal(e.getPlayer(), e.getTo());
            boolean test1 = portal.isInPortal(e.getPlayer(), e.getFrom());

            if(test0 && !test1) {
                for(de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener l : portal.getListeners()) {
                    l.onEnter(e.getPlayer());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if(NativePortalManager.getInstance().getNoTeleport().contains(player)
                || NativePortalManager.getInstance().isEditing(player)
                || WarpSystem.getInstance().getTeleportManager().isTeleporting(player)
                || API.getRemovable(player, GEditor.class) != null) {
            e.setCancelled(true);
        }

        for(Portal portal : NativePortalManager.getInstance().getPortals()) {
            if(portal.isInPortal(player, e.getTo()) || portal.isInPortal(player, e.getFrom())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPortal(EntityPortalEvent e) {
        if(e.getEntity() instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e.getEntity();

            for(Portal portal : NativePortalManager.getInstance().getPortals()) {
                if(portal.isInPortal(le, e.getTo()) || portal.isInPortal(le, e.getFrom())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortalPlaceAround(BlockPlaceEvent e) {
        for(Portal portal : NativePortalManager.getInstance().getPortals()) {
            if(portal == null || portal.getType() == null) continue;
            if(!portal.isVisible()) continue;

            switch(portal.getType()) {
                case WATER:
                case LAVA:
                    if(portal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    break;

                case NETHER:
                    if(portal.isAround(e.getBlock().getLocation(), 1, true))
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(PlayerBucketFillEvent e) {
        for(Portal portal : NativePortalManager.getInstance().getPortals()) {
            if(portal == null || portal.getType() == null) continue;

            switch(portal.getType()) {
                case WATER:
                    if(!e.getBlockClicked().getType().name().contains("WATER")) continue;
                    if(portal.isAround(e.getBlockClicked().getLocation(), 0, true)) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
                    }
                    break;

                case LAVA:
                    if(!e.getBlockClicked().getType().name().contains("LAVA")) continue;
                    if(portal.isAround(e.getBlockClicked().getLocation(), 0, true)) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
                    }
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNetherPortalBreak(BlockBreakEvent e) {
        for(Portal portal : NativePortalManager.getInstance().getPortals()) {
            if(portal == null || portal.getType() == null) continue;
            if(!portal.isVisible()) continue;

            switch(portal.getType()) {
                case NETHER:
                    if(portal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    else if(portal.isAround(e.getBlock().getLocation(), 1, true)) {
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
                    }
                    break;
                case END:
                    if(portal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    break;
            }
        }
    }

}
