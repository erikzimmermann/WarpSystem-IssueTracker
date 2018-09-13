package de.codingair.warpsystem.spigot.api.packetreader;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.data.PacketReader;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlobalPacketReaderManager {
    private List<GlobalPacketReader> globalPacketReaderList = new ArrayList<>();

    public void onEnable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            injectAll(player);
        }
    }

    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            uninjectAll(player);
        }

        this.globalPacketReaderList.clear();
    }

    public void register(GlobalPacketReader reader, boolean update) {
        if(update) injectAll(reader);
        this.globalPacketReaderList.add(reader);
    }

    public void unregister(GlobalPacketReader reader, boolean update) {
        if(update) uninjectAll(reader);
        this.globalPacketReaderList.remove(reader);
    }

    public void injectAll(GlobalPacketReader reader) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            inject(player, reader);
        }
    }

    public void uninjectAll(GlobalPacketReader reader) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            uninject(player, reader);
        }
    }

    public void injectAll(Player player) {
        this.globalPacketReaderList.forEach(r -> inject(player, r));
    }

    public void uninjectAll(Player player) {
        this.globalPacketReaderList.forEach(r -> uninject(player, r));
    }

    public void inject(Player player, GlobalPacketReader reader) {
        new PacketReader(player, reader.getName(), WarpSystem.getInstance()){
            @Override
            public boolean readPacket(Object packet) {
                return reader.readPacket(packet);
            }

            @Override
            public boolean writePacket(Object packet) {
                return reader.writePacket(packet);
            }
        }.inject();
    }

    public void uninject(Player player, GlobalPacketReader reader) {
        List<PacketReader> readerList = API.getRemovables(player, PacketReader.class);

        for(PacketReader r : readerList) {
            if(r.getName() != null && r.getName().equals(reader.getName())) r.unInject();
        }

        readerList.clear();
    }

}
