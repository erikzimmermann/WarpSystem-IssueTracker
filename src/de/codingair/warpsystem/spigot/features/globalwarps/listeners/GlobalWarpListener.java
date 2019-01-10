package de.codingair.warpsystem.spigot.features.globalwarps.listeners;

import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.codingapi.tools.time.TimeMap;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class GlobalWarpListener implements Listener, PacketListener {
    private TimeMap<String, TeleportPacket> teleport = new TimeMap<>();
    private TimeList<Player> noTeleport = new TimeList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        join(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        join(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if(noTeleport.contains(e.getPlayer())) e.setCancelled(true);
    }

    private void join(Player player) {
        TeleportPacket packet = teleport.remove(player.getName());

        if(packet != null) {
            noTeleport.add(player, 1);

            SGlobalWarp warp = packet.getWarp();
            Location location = new Location(Bukkit.getWorld(warp.getLoc().getWorld()), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(), warp.getLoc().getYaw(), warp.getLoc().getPitch());
            String warpDisplayName = packet.getTeleportDisplayName();

            if(player != null) {
                if(location.getWorld() == null) {
                    player.sendMessage(Lang.getPrefix() + "§4World '" + warp.getLoc().getWorld() + "' is missing. Please contact an admin!");
                    return;
                }

                String message = null;
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message", true) || packet.getCosts() > 0) {
                    if(packet.getCosts() > 0) {
                        message = Lang.getPrefix() + Lang.get("Money_Paid").replace("%AMOUNT%", packet.getCosts() + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName));
                    } else {
                        message = Lang.getPrefix() + Lang.get("Teleported_To").replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName));
                    }
                }

                String finalMessage = message;
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().getTeleportManager().teleport(player, location, warpDisplayName, 0, true, true, finalMessage, true, null), 2L);
            }
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        switch(PacketType.getByObject(packet)) {
            case SendGlobalWarpNamesPacket:
                GlobalWarpManager gwManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
                if(((SendGlobalWarpNamesPacket) packet).isStart()) {
                    gwManager.getGlobalWarps().clear();
                }
                gwManager.getGlobalWarps().putAll(((SendGlobalWarpNamesPacket) packet).getNames());
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

                if(player != null) {
                    teleport.put(((TeleportPacket) packet).getPlayer(), (TeleportPacket) packet);
                    join(player);
                } else teleport.put(((TeleportPacket) packet).getPlayer(), (TeleportPacket) packet, 10);
                break;
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
