package de.codingair.warpsystem.spigot.features.nativeportals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
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
        for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
            if(!nativePortal.isVisible()) continue;

            boolean test0 = nativePortal.isInPortal(e.getPlayer(), e.getTo());
            boolean test1 = nativePortal.isInPortal(e.getPlayer(), e.getFrom());

            if(test0 && !test1) {
                for(de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener l : nativePortal.getListeners()) {
                    l.onEnter(e.getPlayer());
                }
            } else if(!test0 && test1) {
                for(de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener l : nativePortal.getListeners()) {
                    l.onLeave(e.getPlayer());
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
                || API.getRemovable(player, NPEditor.class) != null) {
            e.setCancelled(true);
        }

        for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
            if(nativePortal.isInPortal(player, e.getTo()) || nativePortal.isInPortal(player, e.getFrom())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPortal(EntityPortalEvent e) {
        if(e.getEntity() instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e.getEntity();

            for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
                if(nativePortal.isInPortal(le, e.getTo()) || nativePortal.isInPortal(le, e.getFrom())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortalPlaceAround(BlockPlaceEvent e) {
        for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
            if(nativePortal == null || nativePortal.getType() == null) continue;
            if(!nativePortal.isVisible()) continue;

            switch(nativePortal.getType()) {
                case WATER:
                case LAVA:
                    if(nativePortal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    break;

                case NETHER:
                    if(nativePortal.isAround(e.getBlock().getLocation(), 1, true))
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), nativePortal::update, 1);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(PlayerBucketFillEvent e) {
        for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
            if(nativePortal == null || nativePortal.getType() == null) continue;

            switch(nativePortal.getType()) {
                case WATER:
                    if(!e.getBlockClicked().getType().name().contains("WATER")) continue;
                    if(nativePortal.isAround(e.getBlockClicked().getLocation(), 0, true)) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), nativePortal::update, 1);
                    }
                    break;

                case LAVA:
                    if(!e.getBlockClicked().getType().name().contains("LAVA")) continue;
                    if(nativePortal.isAround(e.getBlockClicked().getLocation(), 0, true)) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), nativePortal::update, 1);
                    }
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNetherPortalBreak(BlockBreakEvent e) {
        for(NativePortal nativePortal : NativePortalManager.getInstance().getNativePortals()) {
            if(nativePortal == null || nativePortal.getType() == null) continue;
            if(!nativePortal.isVisible()) continue;

            switch(nativePortal.getType()) {
                case NETHER:
                    if(nativePortal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    else if(nativePortal.isAround(e.getBlock().getLocation(), 1, true)) {
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), nativePortal::update, 1);
                    }
                    break;
                case END:
                    if(nativePortal.isAround(e.getBlock().getLocation(), 0, true))
                        e.setCancelled(true);
                    break;
            }
        }
    }

}
