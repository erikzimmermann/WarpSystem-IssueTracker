package de.codingair.warpsystem.spigot.features.spawn.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import de.codingair.warpsystem.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class SpawnListener implements Listener, PacketListener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(PlayerSpawnLocationEvent e) {
        Spawn spawn = SpawnManager.getInstance().getSpawn();
        if(spawn != null) {
            boolean b = spawn.getUsage() == Spawn.Usage.EVERY_JOIN || spawn.getUsage() == Spawn.Usage.LOCAL_EVERY_JOIN || spawn.getUsage() == Spawn.Usage.GLOBAL_EVERY_JOIN;

            if(!e.getPlayer().hasPlayedBefore()) {
                if(b || spawn.getUsage() == Spawn.Usage.FIRST_JOIN || spawn.getUsage() == Spawn.Usage.LOCAL_FIRST_JOIN || spawn.getUsage() == Spawn.Usage.GLOBAL_FIRST_JOIN) {
                    spawn.onJoin(e, true);
                }
            } else if(b) spawn.onJoin(e, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerRespawnEvent e) {
        if(WarpSystem.getInstance().isOnBungeeCord()) {
            String respawn = SpawnManager.getInstance().getRespawnServer();
            if(respawn != null && !respawn.equals(WarpSystem.getInstance().getCurrentServer())) {
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().getDataHandler().send(new TeleportSpawnPacket(e.getPlayer().getName(), true)), 2L);
                return;
            }
        }

        Spawn spawn = SpawnManager.getInstance().getSpawn();
        if(spawn != null && spawn.isValid() && spawn.getRespawnUsage() != Spawn.RespawnUsage.DISABLED) {
            Location l = spawn.getLocation();
            if(l != null && l.getWorld() != null) e.setRespawnLocation(spawn.getLocation());
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendGlobalSpawnOptionsPacket) {
            SendGlobalSpawnOptionsPacket p = (SendGlobalSpawnOptionsPacket) packet;

            SpawnManager.getInstance().applyGlobalOptions(p.getSpawn(), p.getRespawn());
        } else if(packet.getType() == PacketType.TeleportSpawnPacket) {
            TeleportSpawnPacket p = (TeleportSpawnPacket) packet;

            Spawn spawn = SpawnManager.getInstance().getSpawn();

            if(spawn != null) {
                TeleportOptions options = new TeleportOptions();
                spawn.prepareTeleportOptions(p.getPlayer(), options);
                if(p.isRespawn()) options.setMessage(null);

                TeleportListener.setSpawnPositionOrTeleport(p.getPlayer(), options);
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
