package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.bungee.base.utils.PacketVanishInfo;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class VanishManager implements PacketListener, Listener {
    private final List<String> vanished = new ArrayList<>();

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.PacketVanishInfo) {
            PacketVanishInfo p = (PacketVanishInfo) packet;
            if(p.isVanished()) {
                if(!vanished.contains(p.getPlayer())) vanished.add(p.getPlayer().toLowerCase());
            } else vanished.remove(p.getPlayer());
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }

    @EventHandler
    public void onInit(ServerInitializeEvent e) {
        List<String> l = new ArrayList<>(vanished);
        for(String s : l) {
            ProxiedPlayer p = BungeeCord.getInstance().getPlayer(s);
            if(p == null) vanished.remove(s);
            else if(p.getServer().getInfo().equals(e.getInfo())) vanished.remove(s);
        }
        l.clear();
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        vanished.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(ServerSwitchEvent e) {
        vanished.remove(e.getPlayer().getName());
    }

    public List<String> getVanished() {
        return vanished;
    }

    public boolean isVanished(String player) {
        return vanished.contains(player.toLowerCase());
    }
}
