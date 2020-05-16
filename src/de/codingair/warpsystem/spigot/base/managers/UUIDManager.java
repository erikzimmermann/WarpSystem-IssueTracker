package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.player.data.UUIDFetcher;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.transfer.packets.spigot.RequestUUIDPacket;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UUIDManager {
    private final int CHECKS = 1;
    private HashMap<String, UUID> uniqueIds = new HashMap<>();
    private TimeMap<String, UUID> tempIds = new TimeMap<>();
    private TimeMap<String, PlayerFinalJoinEvent.Data> approve = new TimeMap<>();
    private HashMap<String, Integer> checks = new HashMap<>();

    public void downloadAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            download(player);
        }
    }

    public void applyReload() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            approve.put(player.getName(), new PlayerFinalJoinEvent.Data(player));
            checks.put(player.getName(), CHECKS);
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
                    if(!player.isOnline() || uniqueId == null) {
                        destroy(player);
                        return;
                    }

                    if(!uniqueIds.containsKey(player.getName())) uniqueIds.put(player.getName(), uniqueId);
                    else uniqueIds.replace(player.getName(), uniqueId);

                    check(player, new Callback<PlayerFinalJoinEvent.Data>() {
                        @Override
                        public void accept(PlayerFinalJoinEvent.Data data) {
                            data.setId(uniqueId);
                        }
                    });
                }
            }));
        } else check(player, new Callback<PlayerFinalJoinEvent.Data>() {
            @Override
            public void accept(PlayerFinalJoinEvent.Data data) {
                data.setId(player.getUniqueId());
            }
        });
    }

    private void destroy(Player player) {
        this.uniqueIds.remove(player.getName());
        this.tempIds.remove(player.getName());
        this.checks.remove(player.getName());
        this.approve.remove(player.getName());
    }

    public void check(Player player, Callback<PlayerFinalJoinEvent.Data> apply) {
        Integer current = checks.remove(player.getName());

        PlayerFinalJoinEvent.Data data;
        if(current == null) {
            current = 0;
            approve.put(player.getName(), data = new PlayerFinalJoinEvent.Data(player));
        } else data = approve.get(player.getName());
        current++;

        apply.accept(data);

        if(current >= CHECKS) {
            approve.remove(player.getName());
            Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(data));
        } else checks.put(player.getName(), current);
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
    }

    public boolean isEmpty() {
        return this.uniqueIds.isEmpty();
    }
}
