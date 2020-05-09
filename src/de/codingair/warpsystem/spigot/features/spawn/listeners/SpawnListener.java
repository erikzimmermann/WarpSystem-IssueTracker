package de.codingair.warpsystem.spigot.features.spawn.listeners;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;

public class SpawnListener implements Listener, PacketListener {
    private List<Player> handle = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        handle.add(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        if(!handle.remove(e.getPlayer()) || e.alreadyTeleported()) return;

        Spawn spawn = SpawnManager.getInstance().getSpawn();
        if(spawn != null) {
            Player player = e.getPlayer();

            if(spawn.getUsage() == Spawn.Usage.EVERY_JOIN || spawn.getUsage() == Spawn.Usage.LOCAL_EVERY_JOIN || spawn.getUsage() == Spawn.Usage.GLOBAL_EVERY_JOIN) {
                if(!player.hasPlayedBefore()) {
                    spawn.firstJoin(e);
                } else {
                    if(WarpSystem.getInstance().getTeleportManager().isTeleporting(e.getPlayer()) || spawn.switchServer()) return;

                    TeleportOptions options = new TeleportOptions();
                    options.setMessage(null);
                    options.setAfterEffects(false);
                    options.setSkip(true);
                    spawn.perform(e.getPlayer(), options);
                }
            } else if(spawn.getUsage() == Spawn.Usage.FIRST_JOIN || spawn.getUsage() == Spawn.Usage.LOCAL_FIRST_JOIN || spawn.getUsage() == Spawn.Usage.GLOBAL_FIRST_JOIN) {
                if(!player.hasPlayedBefore()) {
                    spawn.firstJoin(e);
                }
            }
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
            e.setRespawnLocation(spawn.getLocation());
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
