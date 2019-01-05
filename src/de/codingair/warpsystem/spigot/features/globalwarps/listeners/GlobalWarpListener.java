package de.codingair.warpsystem.spigot.features.globalwarps.listeners;

import de.codingair.codingapi.server.Sound;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlobalWarpListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        switch(PacketType.getByObject(packet)) {
            case SendGlobalWarpNamesPacket:
                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().putAll(((SendGlobalWarpNamesPacket) packet).getNames());
                break;

            case UpdateGlobalWarpPacket:
                switch(((UpdateGlobalWarpPacket) packet).getAction()) {
                    case ADD:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().put(((UpdateGlobalWarpPacket) packet).getName(), ((UpdateGlobalWarpPacket) packet).getServer());
                        break;

                    case DELETE:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().remove(((UpdateGlobalWarpPacket) packet).getName());

                        List<GlobalWarp> delete = new ArrayList<>();
                        for(GlobalWarp warpIcon : manager.getGlobalWarps()) {
                            String name = warpIcon.getAction(Action.SWITCH_SERVER).getValue();
                            if(name.equalsIgnoreCase(((UpdateGlobalWarpPacket) packet).getName())) delete.add(warpIcon);
                        }

                        for(GlobalWarp globalWarp : delete) {
                            manager.getGlobalWarps().remove(globalWarp);
                        }

                        delete.clear();
                        break;
                }
                break;

            case TeleportPacket:
                Player player = Bukkit.getPlayer(((TeleportPacket) packet).getPlayer());
                SGlobalWarp warp = ((TeleportPacket) packet).getWarp();
                Location location = new Location(Bukkit.getWorld(warp.getLoc().getWorld()), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(), warp.getLoc().getYaw(), warp.getLoc().getPitch());
                String warpDisplayName = ((TeleportPacket) packet).getTeleportDisplayName();

                if(player != null) {
                    if(location.getWorld() == null) {
                        player.sendMessage(Lang.getPrefix() + "§4World '" + warp.getLoc().getWorld() + "' is missing. Please contact an admin!");
                        return;
                    }

                    String message = null;
                    if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message", true) || ((TeleportPacket) packet).getCosts() > 0) {
                        if(((TeleportPacket) packet).getCosts() > 0) {
                            message = Lang.getPrefix() + Lang.get("Money_Paid").replace("%AMOUNT%", ((TeleportPacket) packet).getCosts() + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName));
                        } else {
                            message = Lang.getPrefix() + Lang.get("Teleported_To").replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName));
                        }
                    }

                    WarpSystem.getInstance().getTeleportManager().teleport(player, location, warpDisplayName, 0, true, true, message);
                }
                break;
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
