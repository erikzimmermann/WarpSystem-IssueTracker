package de.codingair.warpsystem.transfer.spigot;

import de.codingair.codingapi.server.Sound;
import de.codingair.warpsystem.gui.affiliations.Action;
import de.codingair.warpsystem.gui.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpigotPacketHandler implements PacketHandler {
    private SpigotDataHandler dataHandler;

    public SpigotPacketHandler(SpigotDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void handle(Packet packet, String extra) {
        switch(PacketType.getByObject(packet)) {
            case ERROR:
                System.out.println("Couldn't handle anything!");
                break;

            case DeployIconPacket:
                break;

            case TeleportPacket:
                Player player = Bukkit.getPlayer(((TeleportPacket) packet).getPlayer());
                SGlobalWarp warp = ((TeleportPacket) packet).getWarp();
                Location location = new Location(Bukkit.getWorld(warp.getLoc().getWorld()), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(), warp.getLoc().getYaw(), warp.getLoc().getPitch());
                String warpDisplayName = ((TeleportPacket) packet).getTeleportDisplayName();

                if(player != null) {
                    player.teleport(location);
                    Sound.ENDERMAN_TELEPORT.playSound(player);

                    if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message", true)) {
                        player.sendMessage(Lang.getPrefix() + Lang.get("Teleported_To", new Example("ENG", "&7You have been teleported to '&b%warp%&7'."), new Example("GER", "&7Du wurdest zu '&b%warp%&7' teleportiert.")).replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName)));
                    }
                }
                break;

            case SendGlobalWarpNamesPacket:
                WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().putAll(((SendGlobalWarpNamesPacket) packet).getNames());
                break;

            case UpdateGlobalWarpPacket:
                switch(((UpdateGlobalWarpPacket) packet).getAction()) {
                    case ADD:
                        WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().put(((UpdateGlobalWarpPacket) packet).getName(), ((UpdateGlobalWarpPacket) packet).getServer());
                        break;

                    case DELETE:
                        WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().remove(((UpdateGlobalWarpPacket) packet).getName());

                        List<GlobalWarp> delete = new ArrayList<>();
                        for(GlobalWarp warpIcon : WarpSystem.getInstance().getIconManager().getGlobalWarps()) {
                            String name = warpIcon.getAction(Action.SWITCH_SERVER).getValue();
                            if(name.equalsIgnoreCase(((UpdateGlobalWarpPacket) packet).getName())) delete.add(warpIcon);
                        }

                        for(GlobalWarp globalWarp : delete) {
                            WarpSystem.getInstance().getIconManager().getGlobalWarps().remove(globalWarp);
                        }

                        delete.clear();
                        break;
                }
                break;
        }
    }
}
