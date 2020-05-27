package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.player.data.UUIDFetcher;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.RequestUUIDPacket;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UUIDManager {
    private HashMap<String, UUID> uniqueIds = new HashMap<>();
    private TimeMap<String, UUID> tempIds = new TimeMap<>();
    private long initialization = 0;

    public void downloadAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            download(player);
        }
    }

    public void removeAll() {
        this.uniqueIds.clear();
    }

    public void initialize(Player player) {
        if(isCached(player.getName())) {
            convertFromCached(player);
            initialization = System.currentTimeMillis();
        } else if(get(player) == null) Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            download(player);
        }, 20);
    }

    public void download(Player player) {
        if(WarpSystem.getInstance().isOnBungeeCord()) {
            WarpSystem.getInstance().getDataHandler().send(new RequestUUIDPacket(player.getName(), new Callback<UUID>() {
                @Override
                public void accept(UUID uniqueId) {
                    if(!player.isOnline() || uniqueId == null) {
                        destroy(player);
                        return;
                    }

                    injectId(player, uniqueId);
                }
            }));
        } else injectId(player, player.getUniqueId());
    }

    private void injectId(Player player, UUID uniqueId) {
        if(initialization == 0) initialization = System.currentTimeMillis();

        if(WarpSystem.getInstance().isOnBungeeCord()) uniqueIds.put(player.getName(), uniqueId);
        Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(player, uniqueId));
    }

    private void destroy(Player player) {
        this.uniqueIds.remove(player.getName());
        this.tempIds.remove(player.getName());
    }

    public boolean isCached(String name) {
        return tempIds.containsKey(name.toLowerCase());
    }

    public UUID getCached(String name) {
        if(!isCached(name)) return null;
        tempIds.setExpire(name.toLowerCase(), 60 * 2);
        return tempIds.get(name.toLowerCase());
    }

    public void convertFromCached(Player player) {
        if(tempIds.containsKey(player.getName().toLowerCase()) && !uniqueIds.containsKey(player.getName())) {
            uniqueIds.put(player.getName(), tempIds.remove(player.getName().toLowerCase()));
        }
    }

    public void downloadFromMojang(String name, Callback<UUID> callback) {
        if(WarpSystem.getInstance().isOnBungeeCord()) {
            UUID id = tempIds.get(name.toLowerCase());
            if(id != null) {
                callback.accept(id);
                tempIds.setExpire(name.toLowerCase(), 60 * 2);
                return;
            }

            UUIDFetcher.getUUIDAsync(name, WarpSystem.getInstance(), new Callback<UUID>() {
                @Override
                public void accept(UUID id) {
                    if(id == null) callback.accept(null);
                    else {
                        tempIds.put(name.toLowerCase(), id, 60 * 2);
                        callback.accept(id);
                    }
                }
            });
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if(player == null) callback.accept(null);
            else {
                tempIds.put(name.toLowerCase(), player.getUniqueId(), 60 * 2);
                callback.accept(player.getUniqueId());
            }
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
        if(isEmpty()) initialization = 0;
    }

    public boolean isEmpty() {
        return this.uniqueIds.isEmpty();
    }

    public boolean isRegistered(Player player) {
        if(initialization == 0) return false;
        if(WarpSystem.getInstance().isOnBungeeCord()) return this.uniqueIds.containsKey(player.getName());
        else return true;
    }
}
