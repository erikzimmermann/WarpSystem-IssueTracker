package de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class FastEditingTool extends PlayerItem {
    private long last = 0;
    private Location first = null, second = null;
    private final PortalBlockEditor editor;
    private boolean remove = false;

    public FastEditingTool(PortalBlockEditor editor, Player player) {
        super(WarpSystem.getInstance(), player, new ItemBuilder(XMaterial.BLAZE_ROD).setHideName(true).getItem());
        this.editor = editor;

        setFreezed(true);
    }

    @Override
    public void onInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
        if(System.currentTimeMillis() - last < 50) return;
        else last = System.currentTimeMillis();

        if(locationsSet()) {
            if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                reset();
                update();
            } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if(!remove) {
                    remove = true;
                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                        if(!remove) return;
                        remove = false;
                        update();
                    }, 20);
                    update();
                    return;
                }

                remove = false;

                int minX = Math.min(first.getBlockX(), second.getBlockX());
                int minY = Math.min(first.getBlockY(), second.getBlockY());
                int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
                int maxX = Math.max(first.getBlockX(), second.getBlockX());
                int maxY = Math.max(first.getBlockY(), second.getBlockY());
                int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

                int i = 0;
                for(int x = minX; x <= maxX; x++) {
                    for(int y = minY; y <= maxY; y++) {
                        for(int z = minZ; z <= maxZ; z++) {
                            Location l = new Location(first.getWorld(), x, y, z);
                            if(editor.removePosition(l)) {
                                i++;
                                l.getBlock().setType(Material.AIR);
                            }
                        }
                    }
                }

                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Blocks_removed").replace("%AMOUNT%", i + ""));
                update();
                play();
            }
        } else {
            if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
                first = e.getClickedBlock().getLocation();
                update();
                play();
            } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                second = e.getClickedBlock().getLocation();
                update();
                play();
            } else if(e.getAction() == Action.LEFT_CLICK_AIR) {
                first = e.getPlayer().getLocation();
                update();
                play();
            } else if(e.getAction() == Action.RIGHT_CLICK_AIR) {
                second = e.getPlayer().getLocation();
                update();
                play();
            }

            if(locationsSet()) editor.update();
        }
    }

    public boolean locationsSet() {
        return first != null && second != null;
    }
    
    public void play() {
        Sound.UI_BUTTON_CLICK.playSound(getPlayer(), 0.7F, 1F);
    }

    public void reset() {
        first = null;
        second = null;
        editor.update();
        Sound.UI_BUTTON_CLICK.playSound(getPlayer(), 0.7F, 1F);
    }

    public Location getFirst() {
        return first;
    }

    public Location getSecond() {
        return second;
    }

    private void update() {
        MessageAPI.sendActionBar(getPlayer(), buildName(), WarpSystem.getInstance(), Integer.MAX_VALUE);
    }

    private String buildName() {
        return (first == null ? "" : "§8(§7" + first.getBlockX() + ", " + first.getBlockY() + ", " + first.getBlockZ() + "§8) ")
                + StringFormatter.LEFT_RIGHT(Lang.get("Fast_Editing"), locationsSet() ? "§e" + Lang.get("Reset") : Lang.get("Select"), locationsSet() ? (remove ? "§4§l§n" : "§c") + Lang.get("Remove") : Lang.get("Select"))
                + (second == null ? "" : "§8 (§7" + second.getBlockX() + ", " + second.getBlockY() + ", " + second.getBlockZ() + "§8)");
    }

    @Override
    public void onHover(PlayerItemHeldEvent e) {
        update();
    }

    @Override
    public void onUnhover(PlayerItemHeldEvent e) {
        MessageAPI.sendActionBar(getPlayer(), Lang.get("Drop_To_Leave"), WarpSystem.getInstance(), Integer.MAX_VALUE);
    }
}
