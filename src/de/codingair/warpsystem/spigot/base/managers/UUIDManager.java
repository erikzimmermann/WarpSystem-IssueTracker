package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.RequestUUIDPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UUIDManager {
    private HashMap<String, UUID> uniqueIds = new HashMap<>();

    public void downloadAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            download(player);
        }
    }

    public void removeAll() {
        this.uniqueIds.clear();
    }

    public void download(Player player) {
        if(WarpSystem.getInstance().isOnBungeeCord()) {
            WarpSystem.getInstance().getDataHandler().send(new RequestUUIDPacket(player.getName(), new Callback<UUID>() {
                @Override
                public void accept(UUID uniqueId) {
                    if(!uniqueIds.containsKey(player.getName())) uniqueIds.put(player.getName(), uniqueId);
                    else uniqueIds.replace(player.getName(), uniqueId);
                }
            }));
        }
    }

    public UUID get(Player player) {
        if(WarpSystem.getInstance().isOnBungeeCord()) return this.uniqueIds.get(player.getName());
        else return player.getUniqueId();
    }

    public Player getPlayerBy(UUID uniqueId) {
        if(uniqueId == null) return null;

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            for(String key : this.uniqueIds.keySet()) {
                if(this.uniqueIds.get(key).equals(uniqueId)) return Bukkit.getPlayer(key);
            }

            return null;
        } else return Bukkit.getPlayer(uniqueId);
    }

    public void remove(Player player) {
        this.uniqueIds.remove(player.getName());
    }
}
