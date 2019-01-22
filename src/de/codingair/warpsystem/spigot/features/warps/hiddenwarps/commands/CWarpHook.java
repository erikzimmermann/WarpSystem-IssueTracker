package de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.destinations.Destination;
import de.codingair.warpsystem.spigot.base.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.HiddenWarp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.managers.HiddenWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarpHook {

    /**
     * Return true to cancel!
     */
    public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
        if(!FeatureType.HIDDEN_WARPS.isActive() || !sender.hasPermission(WarpSystem.PERMISSION_USE_HiddenWarps)) return false;
        HiddenWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
        if(m.existsWarp(argument)) {
            HiddenWarp warp = m.getWarp(argument);
            WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, new Destination(warp.getName(), DestinationType.HiddenWarp), warp.getName(), warp.getCosts(),
                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.HiddenWarps", true));

            return true;
        }
        return false;
    }

    public void addArguments(CommandSender sender, List<String> suggestions) {
        if(!FeatureType.HIDDEN_WARPS.isActive() || !sender.hasPermission(WarpSystem.PERMISSION_USE_HiddenWarps)) return;
        HiddenWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
        for(HiddenWarp value : m.getWarps().values()) {
            if(value.getPermission() == null || sender.hasPermission(value.getPermission())) {
                suggestions.add(value.getName());
            }
        }
    }
}
