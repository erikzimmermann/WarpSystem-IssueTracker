package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.BungeeJSON;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import de.codingair.warpsystem.spigot.base.utils.cooldown.Cooldown;
import de.codingair.warpsystem.spigot.base.utils.cooldown.CooldownDataPacket;
import de.codingair.warpsystem.spigot.base.utils.cooldown.CooldownPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class CooldownManager implements Listener, PacketListener {
    //expired cooldown will be removed on access (get, save)
    private final HashMap<UUID, List<Cooldown>> cache = new HashMap<>();
    private ConfigFile file;

    public void load() {
        file = WarpSystem.getInstance().getFileManager().loadFile("Cooldown", "/");
        Configuration config = file.getConfig();

        long time = config.getLong("Date", -1);
        if(time == -1) return; //date does not exist -> stop here

        for(String key : config.getKeys()) {
            try {
                UUID id = UUID.fromString(key);

                List<?> data = file.getConfig().getList(key);
                if(data != null) {
                    for(Object s : data) {
                        if(s instanceof Map) {
                            try {
                                Cooldown cooldown = new Cooldown(id);
                                BungeeJSON json = new BungeeJSON((Map<?, ?>) s);
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

        Configuration config = file.getConfig();
        long time = System.currentTimeMillis();

        cache.entrySet().removeIf(entry -> {
            List<BungeeJSON> configData = new ArrayList<>();
            List<Cooldown> data = entry.getValue();

            data.removeIf(cooldown -> {
                if(cooldown.getRemainingTime() == 0) return true;

                BungeeJSON json = new BungeeJSON();
                cooldown.write(json, time);
                configData.add(json);
                return false;
            });

            if(!configData.isEmpty()) config.set(entry.getKey().toString(), configData);
            return data.isEmpty();
        });

        if(!cache.isEmpty()) config.set("Date", time);

        file.save();
    }

    private void add(Cooldown cooldown) {
        if(cooldown.getRemainingTime() != 0) cache.computeIfAbsent(cooldown.getPlayer(), k -> new ArrayList<>()).add(cooldown);
    }

    @EventHandler
    public void onInit(ServerInitializeEvent e) {
        cache.forEach(((uuid, data) -> WarpSystem.getInstance().getDataHandler().send(new CooldownDataPacket(data.toArray(new Cooldown[0])), e.getInfo())));
    }

    @EventHandler(priority = -64)
    public void onJoin(ServerConnectedEvent e) {
        if(WarpSystem.getInstance().getServerManager().isOnline(e.getServer().getInfo())) {
            List<Cooldown> data = cache.get(e.getPlayer().getUniqueId());
            if(data != null) {
                WarpSystem.getInstance().getDataHandler().send(new CooldownDataPacket(data.toArray(new Cooldown[0])), e.getServer().getInfo());
            }
        }
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
