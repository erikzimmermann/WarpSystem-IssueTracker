package de.codingair.warpsystem.spigot.features.portals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.List;

public class PortalListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        List<Portal> portals = new ArrayList<>(PortalManager.getInstance().getPortals());
        for(Portal portal : portals) {
            portal.updatePlayer(e.getPlayer());
        }
        portals.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWalk(PlayerWalkEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(!portal.isVisible()) continue;

            int test = portal.enteredPortal(e.getPlayer(), e.getFrom(), e.getTo());

            if(test == 1) {
                //entered
                for(de.codingair.warpsystem.spigot.features.portals.utils.PortalListener l : portal.getListeners()) {
                    l.onEnter(e.getPlayer());
                }
            } else if(test == -1) {
                //left
                for(de.codingair.warpsystem.spigot.features.portals.utils.PortalListener l : portal.getListeners()) {
                    l.onLeave(e.getPlayer());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if(PortalManager.getInstance().getNoTeleport().contains(player)
                || PortalManager.getInstance().isEditing(player)
                || WarpSystem.getInstance().getTeleportManager().isTeleporting(player)
                || API.getRemovable(player, PortalEditor.class) != null) {
            e.setCancelled(true);
        }

        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal.enteredPortal(player, e.getFrom()) == 1) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(EntityPortalEvent e) {
        if(e.getEntity() instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e.getEntity();

            for(Portal portal : PortalManager.getInstance().getPortals()) {
                if(portal.enteredPortal(le, e.getFrom()) == 1) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortalPlaceAround(BlockPlaceEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;
            if(!portal.isVisible() || portal.isEditMode()) continue;

            if(portal.isAround(e.getBlock().getLocation(), 1, true))
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(PlayerBucketFillEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;

            if(portal.isAround(e.getBlockClicked().getLocation(), 0, true)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNetherPortalBreak(BlockBreakEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;
            if(!portal.isVisible() || portal.isEditMode()) continue;

            if(portal.isAround(e.getBlock().getLocation(), 0, true))
                e.setCancelled(true);
            else if(portal.isAround(e.getBlock().getLocation(), 1, true))
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
        }
    }

}
