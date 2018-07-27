package de.codingair.warpsystem.transfer.spigot;

import de.codingair.warpsystem.gui.affiliations.Icon;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.DeployIconPacket;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
                for(SIcon sIcon : ((DeployIconPacket) packet).icon) {
                    Icon icon = new Icon(sIcon);
                    Bukkit.getOnlinePlayers().iterator().next().getInventory().addItem(icon.getItem());
                }
                Bukkit.getOnlinePlayers().iterator().next().updateInventory();
                break;

            case TeleportPacket:
                Player player = Bukkit.getPlayer(((TeleportPacket) packet).getPlayer());
                SGlobalWarp warp = ((TeleportPacket) packet).getWarp();
                Location location = new Location(Bukkit.getWorld(warp.getLoc().getWorld()), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(), warp.getLoc().getYaw(), warp.getLoc().getPitch());

                if(player != null) player.teleport(location);
                break;

            case SendGlobalWarpNamesPacket:
                WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().addAll(((SendGlobalWarpNamesPacket) packet).getNames());
                System.out.println("Got a list with names!");
                break;

            case UpdateGlobalWarpPacket:
                switch(((UpdateGlobalWarpPacket) packet).getAction()) {
                    case ADD:
                        WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().add(((UpdateGlobalWarpPacket) packet).getName());
                        break;

                    case DELETE:
                        WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().remove(((UpdateGlobalWarpPacket) packet).getName());
                        break;
                }
                break;
        }
    }
}
