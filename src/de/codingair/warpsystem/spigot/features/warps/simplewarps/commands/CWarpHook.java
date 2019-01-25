package de.codingair.warpsystem.spigot.features.warps.simplewarps.commands;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarpHook {

    /**
     * Return true to cancel!
     */
    public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
        if(!FeatureType.SIMPLE_WARPS.isActive() || !sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) return false;
        SimpleWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
        if(m.existsWarp(argument)) {
            SimpleWarp warp = m.getWarp(argument);
            WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, Origin.SimpleWarp, new Destination(warp.getName(), DestinationType.SimpleWarp), warp.getName(), warp.getCosts(),
                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.SimpleWarps", true));

            return true;
        }
        return false;
    }

    public void addArguments(CommandSender sender, List<String> suggestions) {
        if(!FeatureType.SIMPLE_WARPS.isActive() || !sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) return;
        SimpleWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
        for(SimpleWarp value : m.getWarps().values()) {
            if(value.getPermission() == null || sender.hasPermission(value.getPermission())) {
                suggestions.add(value.getName());
            }
        }
    }
}
