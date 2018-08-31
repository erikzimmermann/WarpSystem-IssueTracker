package de.codingair.warpsystem.spigot.features.nativeportals.listeners;

import de.codingair.codingapi.server.Sound;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EditorListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if(NativePortalManager.getInstance().isEditing(e.getPlayer()) && e.getBlock().getType().equals(PortalType.EDIT.getBlockMaterial())) {
            NativePortalManager.getInstance().getEditor(e.getPlayer()).addPosition(e.getBlock().getLocation());
            e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if(NativePortalManager.getInstance().isEditing(e.getPlayer()) && e.getBlock().getType().equals(PortalType.EDIT.getBlockMaterial())) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            Sound.DIG_STONE.playSound(e.getPlayer());
            NativePortalManager.getInstance().getEditor(e.getPlayer()).removePosition(e.getBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if(NativePortalManager.getInstance().isEditing(e.getPlayer()) && e.getClickedBlock().getType().equals(PortalType.EDIT.getBlockMaterial())) {
            e.getClickedBlock().setType(Material.AIR);
            Sound.DIG_STONE.playSound(e.getPlayer(), 1, 0.9F);
            NativePortalManager.getInstance().getEditor(e.getPlayer()).removePosition(e.getClickedBlock().getLocation());
            e.setCancelled(true);
        }
    }

}
