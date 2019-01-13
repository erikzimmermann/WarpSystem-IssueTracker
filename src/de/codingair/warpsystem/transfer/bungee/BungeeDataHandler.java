package de.codingair.warpsystem.transfer.bungee;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.DataHandler;
import de.codingair.warpsystem.transfer.packets.utils.*;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BungeeDataHandler implements DataHandler {
    private Plugin plugin;
    private ChannelListener listener = new ChannelListener(this);
    private BungeePacketHandler packetHandler = new BungeePacketHandler(this);
    private HashMap<UUID, Callback> callbacks = new HashMap<>();
    private List<PacketListener> listeners = new ArrayList<>();

    public BungeeDataHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        BungeeCord.getInstance().getPluginManager().registerListener(this.plugin, this.listener);
        BungeeCord.getInstance().registerChannel(GET_CHANNEL);
    }

    @Override
    public void onDisable() {
        BungeeCord.getInstance().getPluginManager().unregisterListener(this.listener);
        BungeeCord.getInstance().unregisterChannel(GET_CHANNEL);
    }

    public void send(Packet packet, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        PacketType type = PacketType.getByObject(packet);

        if(type.equals(PacketType.ERROR)) {
            System.out.println("[WarpSystem - ERROR] Couldn't find a PacketType!");
            return;
        }

        if(packet instanceof RequestPacket) {
            if(callbacks.get(((RequestPacket) packet).getUniqueId()) != null) ((RequestPacket) packet).checkUUID(this.callbacks.keySet());
            callbacks.put(((RequestPacket) packet).getUniqueId(), ((RequestPacket) packet).getCallback());
        }

        try {
            out.writeInt(type.getId());
            packet.write(out);
        } catch(IOException e) {
            e.printStackTrace();
        }

        for(PacketListener listener : listeners) {
            if(listener.onSend(packet)) return;
        }

        server.sendData(GET_CHANNEL, stream.toByteArray());
    }

    public void onReceive(Packet packet, ServerInfo server, Connection sender) {
        this.packetHandler.handle(packet, server.getName(), sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getName() : null);

        if(packet instanceof AnswerPacket) {
            UUID uniqueId = ((AssignedPacket) packet).getUniqueId();
            Callback callback;
            if((callback = this.callbacks.remove(uniqueId)) == null) return;
            callback.accept(((AnswerPacket) packet).getValue());
        }

        for(PacketListener listener : listeners) {
            listener.onReceive(packet, server.getName());
        }
    }

    public void register(PacketListener listener) {
        this.listeners.add(listener);
    }

    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }
}
