package de.codingair.warpsystem.spigot.features.signs.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.guis.WarpSignGUI;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getClickedBlock() == null || e.getClickedBlock().getState() instanceof Sign) {
            Sign s = e.getClickedBlock() == null ? null : (Sign) e.getClickedBlock().getState();

            SignManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS);
            WarpSign sign = manager.getByLocation(s.getLocation());
            if(sign != null) {
                if(e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY_WARP_SIGNS) && e.getPlayer().getItemInHand().getType().name().toLowerCase().contains("sign")) {
                    String[] lines = s.getLines();
                    for(int i = 0; i < 4; i++) {
                        lines[i] = lines[i].replace("§", "&");
                        s.setLine(i, lines[i]);
                    }

                    s.update(true, true);

                    WarpSign clone = sign.clone();
                    if(clone.getDestination() == null) clone.setDestination(new Destination());

                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new WarpSignGUI(e.getPlayer(), sign, clone).open(), 1L);
                    return;
                }

                if(!WarpSystem.hasPermission(e.getPlayer(), WarpSystem.PERMISSION_USE_WARP_SIGNS)) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                sign.perform(e.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        SignManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS);

        try {
            Sign s = e.getBlock() == null ? null : (Sign) e.getBlock().getState();
            if(s == null) return;
            WarpSign sign = manager.getByLocation(s.getLocation());
            if(sign == null) return;

            if(!e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY_WARP_SIGNS)) {
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                e.setCancelled(true);
            } else if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Creative_Mode_Needed"));
                e.setCancelled(true);
            } else {
                List<WarpSignGUI> l = API.getRemovables(WarpSignGUI.class);
                for(WarpSignGUI gui : l) {
                    gui.close();
                    gui.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("WarpSign_Removed"));
                }
                l.clear();

                manager.getWarpSigns().remove(sign);
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("WarpSign_Removed"));
            }
        } catch(Exception ignored) {
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(SignChangeEvent e) {
        if(!e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY_WARP_SIGNS)) return;

        if(e.getLine(0).equalsIgnoreCase("[warps]")) {
            WarpSign sign = new WarpSign(Location.getByLocation(e.getBlock().getLocation()), new Destination());
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new WarpSignGUI(e.getPlayer(), sign, sign.clone()).open(), 1L);
        }
    }

}
