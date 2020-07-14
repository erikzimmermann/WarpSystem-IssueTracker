package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.cooldown.Cooldown;
import de.codingair.warpsystem.spigot.base.utils.cooldown.CooldownDataPacket;
import de.codingair.warpsystem.spigot.base.utils.cooldown.CooldownPacket;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class CooldownManager implements PacketListener {
    //expired cooldown will be removed on access (get, save)
    private final HashMap<UUID, HashMap<Integer, Cooldown>> cache = new HashMap<>();
    private static final TimeMap<Player, Integer> COOLDOWN_MESSAGE_BUFFER = new TimeMap<>();
    private ConfigFile file;

    public void load() {
        file = WarpSystem.getInstance().getFileManager().loadFile("Cooldown", "/Memory/");
        FileConfiguration config = file.getConfig();

        long time = config.getLong("Date", -1);
        if(time == -1) return; //date does not exist -> stop here

        for(String key : config.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);

                List<?> data = file.getConfig().getList(key);
                if(data != null) {
                    for(Object s : data) {
                        if(s instanceof Map) {
                            try {
                                Cooldown cooldown = new Cooldown(id);
                                JSON json = new JSON((Map<?, ?>) s);
                                cooldown.read(json, time);
                                add(cooldown);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch(IllegalArgumentException ignored) {
                //might be the date tag
            }
        }
    }

    public void save() {
        file.clearConfig();

        FileConfiguration config = file.getConfig();
        long time = System.currentTimeMillis();

        List<Integer> originList = new ArrayList<>();
        if(!WarpSystem.getInstance().isOnBungeeCord()) {
            originList.add(Origin.TeleportRequest.hashCode());
            originList.add(Origin.TeleportCommand.hashCode());
            originList.add(Origin.RandomTP.hashCode());
        }

        cache.entrySet().removeIf(entry -> {
            List<JSON> configData = new ArrayList<>();
            HashMap<Integer, Cooldown> data = entry.getValue();

            data.entrySet().removeIf(sub -> {
                Cooldown cooldown = sub.getValue();

                if(cooldown.getRemainingTime() == 0) return true;

                if(originList.contains(cooldown.getHashId())) return false;

                JSON json = new JSON();
                cooldown.write(json, time);
                configData.add(json);
                return false;
            });

            if(!configData.isEmpty()) config.set(entry.getKey().toString(), configData);
            return data.isEmpty();
        });

        if(!cache.isEmpty()) config.set("Date", time);

        file.saveConfig();
    }

    public Cooldown getCooldown(UUID uuid, int hashCode) {
        HashMap<Integer, Cooldown> data = cache.get(uuid);
        if(data == null) return null;

        Cooldown cooldown = data.get(hashCode);
        if(cooldown != null && cooldown.getRemainingTime() == 0) {
            data.remove(cooldown);
            if(data.isEmpty()) cache.remove(uuid);
            cooldown = null;
        }

        return cooldown;
    }

    public void register(Player player, Origin origin) {
        long time = origin.getCooldown();
        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Cooldown) || time == 0) return;
        Cooldown cooldown = new Cooldown(WarpSystem.getInstance().getUUIDManager().get(player), System.currentTimeMillis() + time, origin.ordinal());

        add(cooldown);
        if(WarpSystem.getInstance().isOnBungeeCord()) {
            //upload to bungee
            WarpSystem.getInstance().getDataHandler().send(new CooldownPacket(cooldown));
        }
    }

    public void register(Player player, long time, int hash) {
        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Cooldown) || time == 0) return;
        add(new Cooldown(WarpSystem.getInstance().getUUIDManager().get(player), System.currentTimeMillis() + time, hash));
    }

    private void add(Cooldown cooldown) {
        if(cooldown.getRemainingTime() != 0) cache.computeIfAbsent(cooldown.getPlayer(), k -> new HashMap<>()).put(cooldown.getHashId(), cooldown);
    }

    public long getRemainingCooldown(Player player, int hashCode) {
        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Cooldown)) {
            cache.remove(WarpSystem.getInstance().getUUIDManager().get(player));
            return 0;
        }
        return getRemainingCooldown(WarpSystem.getInstance().getUUIDManager().get(player), hashCode);
    }

    public long getRemainingCooldown(UUID uuid, int hashCode) {
        Cooldown c = getCooldown(uuid, hashCode);
        if(c == null) return 0;
        else return c.getRemainingTime();
    }

    public boolean checkPlayer(Player player, Origin origin) {
        return checkPlayer(player, origin.ordinal());
    }

    public boolean checkPlayer(Player player, int hash) {
        long cooldown = getRemainingCooldown(player, hash);
        if(cooldown > 0) {
            Integer time = COOLDOWN_MESSAGE_BUFFER.get(player);
            if(time != null && time == (int) (cooldown / 1000)) return true;
            COOLDOWN_MESSAGE_BUFFER.put(player, (int) (cooldown / 1000), 1000);

            player.sendMessage(Lang.getPrefix() + Lang.get("Cooldown_Info").replace("%TIME%", StringFormatter.convertInTimeFormat(cooldown + 1000)));
            return true;
        }

        return false;
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.CooldownPacket) {
            CooldownPacket p = (CooldownPacket) packet;
            add(p.getCooldown());
        } else if(packet.getType() == PacketType.CooldownDataPacket) {
            CooldownDataPacket p = (CooldownDataPacket) packet;
            for(Cooldown cooldown : p.getCooldown()) {
                add(cooldown);
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
