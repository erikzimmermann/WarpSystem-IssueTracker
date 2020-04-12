package de.codingair.warpsystem.bungee.api.chatinput;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.ChatInputGUITogglePacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatInputManager implements Listener, PacketListener {
    private List<String> using = new ArrayList<>();
    private HashMap<String, String> cache = new HashMap<>();

    public ChatInputManager() {
        WarpSystem.getInstance().getDataHandler().register(this);
        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), this);
    }

    @EventHandler(priority = -64)
    public void onChatSave(ChatEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
        String name = ((ProxiedPlayer) e.getSender()).getName();
        if(this.using.contains(name)) {
            if(e.isCommand()) e.setMessage("$c." + e.getMessage());

            cache.put(name, e.getMessage().substring(0, Math.min(e.getMessage().length(), 256)));
            e.setCancelled(true);
            e.setMessage("");
        }
    }

    @EventHandler(priority = 64)
    public void onChatCache(ChatEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
        String name = ((ProxiedPlayer) e.getSender()).getName();

        if(this.using.remove(name)) {
            String message = cache.remove(name);
            e.setCancelled(false);
            e.setMessage(message);
        }
    }

    @EventHandler
    public void onQuit(ServerDisconnectEvent e) {
        this.using.remove(e.getPlayer().getName());
        this.cache.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(ServerSwitchEvent e) {
        this.using.remove(e.getPlayer().getName());
        this.cache.remove(e.getPlayer().getName());
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.ChatInputGUITogglePacket) {
            ChatInputGUITogglePacket p = (ChatInputGUITogglePacket) packet;

            if(p.isUsing()) {
                if(!this.using.contains(p.getName())) this.using.add(p.getName());
            } else this.using.remove(p.getName());
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
