package de.codingair.warpsystem.transfer.bungee;

import de.codingair.warpsystem.transfer.DataHandler;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChannelListener implements Listener {
    private BungeeDataHandler bungeeDataHandler;
//    private List<byte[]> bytes = new ArrayList<>();

    public ChannelListener(BungeeDataHandler bungeeDataHandler) {
        this.bungeeDataHandler = bungeeDataHandler;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if(e.getTag().equals("BungeeCord")) {

            ServerInfo server = BungeeCord.getInstance().getPlayer(e.getReceiver().toString()).getServer().getInfo();
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(/*use == null ? */e.getData()/* : use*/));

            try {
                if(!in.readUTF().equals(DataHandler.REQUEST_CHANNEL)) return;

                PacketType type = PacketType.getById(in.readInt());
                Packet packet = (Packet) type.getPacket().newInstance();

                packet.read(in);
                this.bungeeDataHandler.onReceive(packet, server, e.getReceiver());
            } catch(IOException | IllegalAccessException | InstantiationException e1) {
                e1.printStackTrace();
            }
        }
    }

}
