package de.codingair.warpsystem.spigot.features.portals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import org.bukkit.GameMode;
import net.md_5.bungee.api.chat.TextComponent;
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
        if(editor != null) {
            PlayerItem item = API.getRemovable(e.getPlayer(), PlayerItem.class);

            Block b = e.getPlayer().getTargetBlock((Set<Material>) null, 10);
            if(b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial() && b.getType() != XMaterial.CHEST.parseMaterial() && b.getType() != XMaterial.TRAPPED_CHEST.parseMaterial()) {
                Material m = b.getType();

                IReflection.MethodAccessor isFuel = IReflection.getSaveMethod(Material.class, "isFuel", boolean.class);
                boolean fuel = isFuel != null && (boolean) isFuel.invoke(m);

                if(!m.isOccluding() && (fuel || !m.isSolid())) {
                    String name = "§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §e" + m.name() + Lang.PREMIUM_LORE;
                    if(!name.equals(item.getDisplayName())) {
                        item.setDisplayName(name);
                        e.getPlayer().getInventory().setItem(7, item);
                    }
                    return;
                }
            }

            String name = "§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §c-" + Lang.PREMIUM_LORE;
            if(!name.equals(item.getDisplayName())) {
                item.setDisplayName(name);
                e.getPlayer().getInventory().setItem(7, item);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        if(PortalManager.getInstance().isEditing(e.getPlayer())) {
            BlockType type = BlockType.getByEditMaterial(e.getItemInHand());

            if(type == null) return;
            PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
            if(!editor.getPortal().getBlocks().isEmpty() && editor.getPortal().getBlocks().get(0).getType() != type) {
                Lang.PREMIUM_TITLE(e.getPlayer(), "§7You cannot use §edifferent types§7");
                e.setCancelled(true);
                return;
            }

            editor.addPosition(e.getBlock().getLocation(), type);
            e.setCancelled(false);
        } else if(API.getRemovable(e.getPlayer(), HotbarGUI.class) != null) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        PortalBlockEditor editor = PortalManager.getInstance().getEditor(e.getPlayer());
        if(editor != null) {
            if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

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
