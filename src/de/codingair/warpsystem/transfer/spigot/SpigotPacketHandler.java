package de.codingair.warpsystem.transfer.spigot;

import de.codingair.codingapi.server.Sound;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

                    player.teleport(location);
                    Sound.ENDERMAN_TELEPORT.playSound(player);

                    if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message", true) || ((TeleportPacket) packet).getCosts() > 0) {
                        if(((TeleportPacket) packet).getCosts() > 0) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Money_Paid", new Example("ENG", "&7You have paid &c%AMOUNT% coin(s) &7to teleport to '&b%warp%&7'."), new Example("GER", "&7Du hast &c%AMOUNT% Coin(s) &7bezahlt, um dich nach '&b%warp%&7' zu teleportieren!")).replace("%AMOUNT%", ((TeleportPacket) packet).getCosts() + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName)));
                        } else {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Teleported_To", new Example("ENG", "&7You have been teleported to '&b%warp%&7'."), new Example("GER", "&7Du wurdest zu '&b%warp%&7' teleportiert.")).replace("%warp%", ChatColor.translateAlternateColorCodes('&', warpDisplayName)));
                        }
                    }
                }
                break;
        }
    }
}
