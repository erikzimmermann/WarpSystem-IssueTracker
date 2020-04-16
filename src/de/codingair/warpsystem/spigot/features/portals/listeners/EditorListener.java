package de.codingair.warpsystem.spigot.features.portals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EditorListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        if(editor != null) {
            PlayerItem item = PlayerItem.getPlayerItems(e.getPlayer()).remove(0);

            Block b = e.getPlayer().getTargetBlock(null, 10);
            if(b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial() && b.getType() != XMaterial.CHEST.parseMaterial() && b.getType() != XMaterial.TRAPPED_CHEST.parseMaterial()) {
                Material m = b.getType();
                if(!m.isOccluding() && (m.isFuel() || !m.isSolid())) {
                    String name = "§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §e" + m.name();
                    if(!name.equals(item.getDisplayName())) {
                        item.setDisplayName(name);
                        e.getPlayer().getInventory().setItem(7, item);
                    }
                    return;
                }
            }

            String name = "§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §c-";
            if(!name.equals(item.getDisplayName())) {
                item.setDisplayName(name);
                e.getPlayer().getInventory().setItem(7, item);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if(PortalManager.getInstance().isEditing(e.getPlayer())) {
            BlockType type = BlockType.getByEditMaterial(e.getBlockPlaced().getType());

            if(type == null) return;

            PortalManager.getInstance().getEditor(e.getPlayer()).addPosition(e.getBlock().getLocation(), type);
            e.setCancelled(false);
        } else if(API.getRemovable(e.getPlayer(), HotbarGUI.class) != null) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if(PortalManager.getInstance().isEditing(e.getPlayer())) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
//            Sound.DIG_STONE.playSound(e.getPlayer());
//            PortalManager.getInstance().getEditor(e.getPlayer()).removePosition(e.getBlock().getLocation());
        } else if(API.getRemovable(e.getPlayer(), HotbarGUI.class) != null) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        if(editor != null) {
            if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

            e.getClickedBlock().setType(Material.AIR);
//            Sound.DIG_STONE.playSound(e.getPlayer(), 1, 0.9F);
            editor.removePosition(e.getClickedBlock().getLocation());
            e.setCancelled(true);
        }
    }

}
