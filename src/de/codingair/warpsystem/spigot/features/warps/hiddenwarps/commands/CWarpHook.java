package de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands;

import de.codingair.warpsystem.spigot.base.WarpSystem;
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
        HiddenWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
        if(m.existsWarp(argument)) {
            HiddenWarp warp = m.getWarp(argument);
            WarpSystem.getInstance().getTeleportManager().tryToTeleport((Player) sender, warp.getPermission(), warp.getLocation(), warp.getName(), warp.getCosts(), false, false,
                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.HiddenWarps", true));

            return true;
        }
        return false;
    }

    public void addArguments(CommandSender sender, List<String> suggestions) {
        HiddenWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
        for(HiddenWarp value : m.getWarps().values()) {
            if(value.getPermission() == null || sender.hasPermission(value.getPermission())) {
                suggestions.add(value.getName());
            }
        }
    }
}
