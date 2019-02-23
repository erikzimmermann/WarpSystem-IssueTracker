package de.codingair.warpsystem.transfer.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.codingapi.tools.time.TimeListener;
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
    private HashMap<UUID, Callback> callbacks = new HashMap<>();
    private final List<PacketListener> listeners = new ArrayList<>();

    private TimeList<UUID> timeOut = new TimeList<>();
    private TimeListener<UUID> timeOutListener;

    public SpigotDataHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);

        timeOut.addListener(timeOutListener = new TimeListener<UUID>() {
            @Override
            public void onRemove(UUID item) {
                Callback callback = callbacks.remove(item);
                if(callback != null) callback.accept(null);
            }

            @Override
            public void onTick(UUID item, int timeLeft) {

            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.plugin, GET_CHANNEL, this.listener);

        this.timeOut.removeListener(timeOutListener);
    }

    public void send(Packet packet) {
        send(packet, -1);
    }

    public void send(Packet packet, int timeOut) {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            Player player = Bukkit.getOnlinePlayers().toArray(new Player[0])[0];

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            PacketType type = PacketType.getByObject(packet);

            if(type.equals(PacketType.ERROR)) {
                System.out.println("[ERROR] Couldn't find a PacketType! [" + packet.getClass().getName() + "]");
                return;
            }

            if(packet instanceof RequestPacket) {
                if(callbacks.get(((RequestPacket) packet).getUniqueId()) != null) ((RequestPacket) packet).checkUUID(this.callbacks.keySet());
                callbacks.put(((RequestPacket) packet).getUniqueId(), ((RequestPacket) packet).getCallback());

                if(timeOut > 0) this.timeOut.add(((RequestPacket) packet).getUniqueId(), timeOut);
            }

            try {
                out.writeUTF(REQUEST_CHANNEL);
                out.writeInt(type.getId());
                packet.write(out);
            } catch(IOException e) {
                e.printStackTrace();
            }

            List<PacketListener> listeners = new ArrayList<>(this.listeners);
            for(PacketListener listener : listeners) {
                if(listener.onSend(packet)) return;
            }
            listeners.clear();

            player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
        }
    }

    public void onReceive(Packet packet) {
        this.packetHandler.handle(packet, "BungeeCord");

        if(packet instanceof AnswerPacket) {
            UUID uniqueId = ((AssignedPacket) packet).getUniqueId();
            Callback callback;
            if((callback = this.callbacks.remove(uniqueId)) == null) return;
            callback.accept(((AnswerPacket) packet).getValue());

            this.timeOut.remove(uniqueId);
        }

        List<PacketListener> listeners = new ArrayList<>(this.listeners);
        for(PacketListener listener : listeners) {
            listener.onReceive(packet, "BungeeCord");
        }
        listeners.clear();
    }

    public void register(PacketListener listener) {
        if(!this.listeners.contains(listener)) this.listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }
}
