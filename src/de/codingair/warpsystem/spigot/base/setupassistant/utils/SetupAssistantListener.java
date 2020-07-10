package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.setupassistant.SetupAssistantManager;
import de.codingair.warpsystem.spigot.base.setupassistant.bungee.SetupAssistantStorePacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class SetupAssistantListener implements Listener, PacketListener {
    private IReflection.ConstructorAccessor chatPacket = null;
    private Object type = null;

    @EventHandler
    public void onFinalJoin(PlayerFinalJoinEvent e) {
        SetupAssistantManager.getInstance().onJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SetupAssistant a = SetupAssistantManager.getInstance().getAssistant();
        if(a != null && a.getPlayer().equals(e.getPlayer())) {
            a.onQuit();
        }
    }

    private Object buildComponent(String message) {
        if(chatPacket == null) {
            Class<?> packet = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutChat");

            if(Version.get().isBiggerThan(15)) {
                Class<?> type = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "ChatMessageType");
                this.type = type.getEnumConstants()[0];
                chatPacket = IReflection.getConstructor(packet, PacketUtils.IChatBaseComponentClass, type, UUID.class);
            } else {
                chatPacket = IReflection.getConstructor(packet, PacketUtils.IChatBaseComponentClass);
            }
        }
        
        if(Version.get().isBiggerThan(15)) {
            return chatPacket.newInstance(PacketUtils.getRawIChatBaseComponent(message), type, UUID.randomUUID());
        } else return chatPacket.newInstance(PacketUtils.getRawIChatBaseComponent(message));
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SetupAssistantStorePacket) {
            String message = ((SetupAssistantStorePacket) packet).getMessage();
            SetupAssistant assistant = SetupAssistantManager.getInstance().getAssistant();
            if(assistant != null) assistant.queue(buildComponent(message));
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
