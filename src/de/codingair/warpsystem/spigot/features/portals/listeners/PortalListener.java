package de.codingair.warpsystem.spigot.features.portals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.utils.Ticker;
import de.codingair.warpsystem.spigot.api.blocks.listeners.RuleListener;
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
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PortalListener implements Listener, Ticker {
    private static PortalListener instance;
    private final HashMap<Player, Portal> waiting = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public PortalListener() {
        instance = this;
        API.addTicker(this);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onSecond() {
        try {
            lock.tryLock(100, TimeUnit.MILLISECONDS);
            try {
                waiting.entrySet().removeIf(entry -> {
                    if(entry.getValue().enteredPortal(entry.getKey(), entry.getKey().getLocation()) == 1) {
                        entry.getValue().perform(entry.getKey());
                        return false;
                    } else return true;
                });
            } finally {
                lock.unlock();
            }
        } catch(InterruptedException ignored) {
        }
    }

    public static void waiting(Player player, Portal portal) {
        instance.lock.lock();
        try {
            instance.waiting.putIfAbsent(player, portal);
        } finally {
            instance.lock.unlock();
        }
    }

    public static void done(Player player) {
        instance.lock.lock();
        try {
            instance.waiting.remove(player);
        } finally {
            instance.lock.unlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        List<Portal> portals = new ArrayList<>(PortalManager.getInstance().getPortals());
        for(Portal portal : portals) {
            portal.updatePlayer(e.getPlayer());
        }
        portals.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        PortalEditor editor = PortalEditor.EDITORS.remove(e.getPlayer().getName());
        if(editor != null) editor.getBackup().cancel(editor.getClone());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWalk(PlayerWalkEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(!portal.isVisible() || portal.isEditMode()) continue;

            int test = portal.enteredPortal(e.getPlayer(), e.getFrom(), e.getTo());

            if(test == 1) RuleListener.noDamageTo(e.getPlayer());

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

                done(e.getPlayer());
            } else if(test == 2) {
                //entered and left
                if(portal.isSkip()) {
                    for(de.codingair.warpsystem.spigot.features.portals.utils.PortalListener l : portal.getListeners()) {
                        l.onEnter(e.getPlayer());
                    }
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
    public void onBlockPlace(BlockPlaceEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;
            if(!portal.isVisible() || portal.isEditMode()) continue;

            if(portal.isAround(e.getBlock().getLocation(), 1)) {
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketFill(PlayerBucketFillEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;
            if(!portal.isVisible() || portal.isEditMode()) continue;

            if(portal.isAround(e.getBlockClicked().getLocation(), 0)) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal == null) continue;

            if(portal.isEditMode() && portal.getEditing() != null) {
                if(portal.getEditing().isAround(e.getBlock().getLocation(), 0)) {
                    e.setCancelled(true);
                }
            } else if(portal.isVisible()) {
                if(portal.isAround(e.getBlock().getLocation(), 0))
                    e.setCancelled(true);
                else if(portal.isAround(e.getBlock().getLocation(), 1)) {
                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), portal::update, 1);
                }
            }
        }
    }

}
