package de.codingair.warpsystem.spigot.features.portals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import de.codingair.warpsystem.spigot.features.portals.utils.PortalBlock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;

public class EditorListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        if(editor != null && e.getPlayer().getInventory().getHeldItemSlot() == 8) {
            PlayerItem item = API.getRemovable(e.getPlayer(), PlayerItem.class);

            if(item.getType() != XMaterial.GHAST_TEAR.parseMaterial()) return;

            Block b = e.getPlayer().getTargetBlock((Set<Material>) null, 10);
            if(b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial() && b.getType() != XMaterial.CHEST.parseMaterial() && b.getType() != XMaterial.TRAPPED_CHEST.parseMaterial()) {
                Material m = b.getType();

                IReflection.MethodAccessor isFuel = IReflection.getSaveMethod(Material.class, "isFuel", boolean.class);
                boolean fuel = isFuel != null && (boolean) isFuel.invoke(m);

                if(!m.isOccluding() && (fuel || !m.isSolid())) {
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
                e.getPlayer().getInventory().setItem(8, item);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        if(editor != null && !editor.getFastEditingTool().locationsSet()) {
            BlockType type = BlockType.getByEditMaterial(e.getItemInHand());

            if(type == null) return;

            PortalManager.getInstance().getEditor(e.getPlayer()).addPosition(e.getBlock().getLocation(), type);
            e.setCancelled(false);
        } else if(API.getRemovable(e.getPlayer(), HotbarGUI.class) != null) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        int slot = e.getPlayer().getInventory().getHeldItemSlot();
        if(editor != null && slot > 1 && slot < 7) {
            if(editor.getFastEditingTool().locationsSet() && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                e.setCancelled(true);

                //fill area
                Location first = editor.getFastEditingTool().getFirst();
                Location second = editor.getFastEditingTool().getSecond();
                int minX = Math.min(first.getBlockX(), second.getBlockX());
                int minY = Math.min(first.getBlockY(), second.getBlockY());
                int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
                int maxX = Math.max(first.getBlockX(), second.getBlockX());
                int maxY = Math.max(first.getBlockY(), second.getBlockY());
                int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

                BlockType type = BlockType.getByEditMaterial(e.getPlayer().getItemInHand());
                int i = 0;

                for(int x = minX; x <= maxX; x++) {
                    for(int y = minY; y <= maxY; y++) {
                        for(int z = minZ; z <= maxZ; z++) {
                            Location l = new Location(first.getWorld(), x, y, z);
                            if(l.getBlock().getType() == Material.AIR) {
                                i++;
                                PortalBlock block = editor.addPosition(l, type);
                                block.updateBlock(editor.getPortal());
                            }
                        }
                    }
                }

                editor.getFastEditingTool().play();
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Blocks_added").replace("%AMOUNT%", i + ""));
            } else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if(editor.removePosition(e.getClickedBlock().getLocation())) {
                    e.setCancelled(false);

                    if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        Sound s = Environment.getBreakSoundOf(e.getClickedBlock());
                        if(s != null) s.playSound(e.getPlayer());
                        Particle.BLOCK_DUST.send(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5));
                    }

                    e.getClickedBlock().setType(Material.AIR);
                }
            }
        }
    }

}
