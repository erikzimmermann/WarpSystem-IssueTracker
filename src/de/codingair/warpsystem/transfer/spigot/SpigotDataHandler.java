package de.codingair.warpsystem.transfer.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.DataHandler;
import de.codingair.warpsystem.transfer.packets.utils.*;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpigotDataHandler implements DataHandler {
    private JavaPlugin plugin;
    private ChannelListener listener = new ChannelListener(this);
    private SpigotPacketHandler packetHandler = new SpigotPacketHandler(this);
    private HashMap<String, Callback> callbacks = new HashMap<>();
    private List<PacketListener> listeners = new ArrayList<>();

    public SpigotDataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);
    }

    public void send(Packet packet) {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            Player player = Bukkit.getOnlinePlayers().toArray(new Player[0])[0];

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            PacketType type = PacketType.getByObject(packet);

            if(type.equals(PacketType.ERROR)) {
                System.out.println("[ERROR] Couldn't find a PacketType!");
                return;
            }

            if(packet instanceof RequestPacket) {
                if(callbacks.get(((RequestPacket) packet).getUniqueId().toString()) != null) ((RequestPacket) packet).checkUUID(this.callbacks.keySet());
                callbacks.put(((RequestPacket) packet).getUniqueId().toString(), ((RequestPacket) packet).getCallback());
            }

            try {
                out.writeUTF(REQUEST_CHANNEL);
                out.writeInt(type.getId());
                packet.write(out);
            } catch(IOException e) {
                e.printStackTrace();
            }

            for(PacketListener listener : listeners) {
                if(listener.onSend(packet)) return;
            }

            player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
        }
    }

    public void onReceive(Packet packet) {
        this.packetHandler.handle(packet, "BungeeCord");

        if(packet instanceof AnswerPacket) {
            UUID uniqueId = ((AssignedPacket) packet).getUniqueId();
            Callback callback;
            if((callback = this.callbacks.remove(uniqueId.toString())) == null) return;
            callback.accept(((AnswerPacket) packet).getValue());
        }

        for(PacketListener listener : listeners) {
            listener.onReceive(packet, "BungeeCord");
        }
    }

    public void register(PacketListener listener) {
        this.listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }
}
