package de.codingair.warpsystem.transfer.spigot;

import de.codingair.warpsystem.transfer.DataHandler;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChannelListener implements PluginMessageListener {
    private SpigotDataHandler spigotDataHandler;

    public ChannelListener(SpigotDataHandler spigotDataHandler) {
        this.spigotDataHandler = spigotDataHandler;
    }

    @Override
    public void onPluginMessageReceived(String tag, Player player, byte[] bytes) {
        if(tag.equals(DataHandler.GET_CHANNEL)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

            try {
                PacketType type = PacketType.getById(in.readInt());
                Packet packet = (Packet) type.getPacket().newInstance();

                packet.read(in);
                this.spigotDataHandler.onReceive(packet);
            } catch(IOException | IllegalAccessException | InstantiationException e1) {
                e1.printStackTrace();
            }
        }
    }
}
