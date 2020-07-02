package de.codingair.warpsystem.bungee.base.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.setupassistant.bungee.SetupAssistantStorePacket;
import de.codingair.warpsystem.spigot.base.setupassistant.bungee.ToggleSetupAssistantPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Chat;

import java.lang.reflect.Field;

public class SetupAssistantListener implements Listener, PacketListener {
    private ProxiedPlayer editing = null;
    private String message = null;
    private Connection.Unsafe backup = null;

    @EventHandler(priority = -99)
    public void onChatRemove(ChatEvent e) {
        if(editing == null) return;

        Connection c = e.getSender();
        if(c instanceof ProxiedPlayer && c.equals(editing)) {
            //sender is editing
            message = e.getMessage();
            e.setCancelled(true);
            e.setMessage("");
        }
    }

    @EventHandler(priority = 99)
    public void onChatAdd(ChatEvent e) {
        if(editing == null) return;

        Connection c = e.getSender();
        if(c instanceof ProxiedPlayer && c.equals(editing)) {
            e.setCancelled(false);
            e.setMessage(message);
            message = null;
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        if(e.getPlayer().equals(editing)) {
            this.editing = null;
            this.backup = null;
        }
    }

    public void inject(UserConnection c) throws NoSuchFieldException, IllegalAccessException {
        if(backup != null) return;

        Field unsafe = UserConnection.class.getDeclaredField("unsafe");
        unsafe.setAccessible(true);

        backup = (Connection.Unsafe) unsafe.get(c);

        unsafe.set(c, (Connection.Unsafe) definedPacket -> {
            if(definedPacket instanceof Chat) {
                //block every bungee initiated chat message > queue
                Chat chat = (Chat) definedPacket;
                if(!chat.getMessage().isEmpty())
                    WarpSystem.getInstance().getDataHandler().send(new SetupAssistantStorePacket(chat.getMessage()), c.getServer().getInfo());
                return;
            }

            //forward
            backup.sendPacket(definedPacket);
        });
    }

    public void backup(UserConnection c) throws NoSuchFieldException, IllegalAccessException {
        if(backup == null) return;
        Field unsafe = UserConnection.class.getDeclaredField("unsafe");
        unsafe.setAccessible(true);
        unsafe.set(c, backup);
        backup = null;
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.ToggleSetupAssistantPacket) {
            String name = ((ToggleSetupAssistantPacket) packet).getName();
            if(name == null) {
                if(editing instanceof UserConnection) {
                    try {
                        backup((UserConnection) editing);
                    } catch(NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                editing = null;
            } else editing = BungeeCord.getInstance().getPlayer(name);

            if(editing instanceof UserConnection) {
                try {
                    inject((UserConnection) editing);
                } catch(NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
