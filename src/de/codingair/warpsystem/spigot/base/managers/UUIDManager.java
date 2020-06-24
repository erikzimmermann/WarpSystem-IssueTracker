package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.ApplyUUIDPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class UUIDManager {
    private final HashMap<String, UUID> uniqueIds = new HashMap<>();
    private Boolean bungee;

    public void removeAll() {
        this.uniqueIds.clear();
    }

    protected void injectId(Player player, UUID uniqueId, boolean joined) {
        if(WarpSystem.getInstance().isOnBungeeCord()) uniqueIds.put(player.getName(), uniqueId);
        Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(player, uniqueId, joined));
    }

    public UUID get(Player player) {
        if(bungee()) return this.uniqueIds.get(player.getName());
        else return player.getUniqueId();
    }

    public void remove(Player player) {
        this.uniqueIds.remove(player.getName());
    }

    public boolean isEmpty() {
        return this.uniqueIds.isEmpty();
    }

    public boolean isRegistered(Player player) {
        if(bungee()) return this.uniqueIds.containsKey(player.getName());
        else return true;
    }

    public UUIDListener listener() {
        return new UUIDListener();
    }

    protected boolean bungee() {
        if(bungee == null) bungee = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord", false);
        return bungee;
    }

    public class UUIDListener implements Listener, PacketListener {
        private UUIDListener() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onJoin(PlayerJoinEvent e) {
            if(!bungee()) Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(e.getPlayer(), e.getPlayer().getUniqueId(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onQuit(PlayerQuitEvent e) {
            WarpSystem.getInstance().getUUIDManager().remove(e.getPlayer());
        }

        @Override
        public void onReceive(Packet packet, String extra) {
            if(bungee() && packet.getType() == PacketType.ApplyUUIDPacket) {
                ApplyUUIDPacket p = (ApplyUUIDPacket) packet;
                Player player = Bukkit.getPlayer(p.getName());
                injectId(player, p.getId(), p.isJoined());
            }
        }

        @Override
        public boolean onSend(Packet packet) {
            return false;
        }
    }
}
