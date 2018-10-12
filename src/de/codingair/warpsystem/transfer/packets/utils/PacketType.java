package de.codingair.warpsystem.transfer.packets.utils;

import de.codingair.warpsystem.transfer.packets.bungee.*;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.*;

public enum PacketType {
    ERROR(0, null),
    UploadIconPacket(1, UploadIconPacket.class),
    DeployIconPacket(2, DeployIconPacket.class),

    PublishGlobalWarpPacket(3, PublishGlobalWarpPacket.class),
    PrepareTeleportPacket(4, PrepareTeleportPacket.class),
    TeleportPacket(5, TeleportPacket.class),
    DeleteGlobalWarpPacket(6, DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(7, RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(8, SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(9, UpdateGlobalWarpPacket.class),
    PerformCommandPacket(10, PerformCommandPacket.class),
    RequestUUIDPacket(11, RequestUUIDPacket.class),
    SendUUIDPacket(12, SendUUIDPacket.class),

    BooleanPacket(100, BooleanPacket.class),
    IntegerPacket(101, IntegerPacket.class),

    AnswerPacket(200, AnswerPacket.class),
    ;

    private int id;
    private Class<?> packet;

    PacketType(int id, Class<?> packet) {
        this.id = id;
        this.packet = packet;
    }

    public int getId() {
        return id;
    }

    public Class<?> getPacket() {
        return packet;
    }

    public static PacketType getById(int id) {
        for(PacketType packetType : values()) {
            if(packetType.getId() == id) return packetType;
        }

        return ERROR;
    }

    public static PacketType getByObject(Object packet) {
        if(packet == null) return ERROR;

        for(PacketType packetType : values()) {
            if(packetType.equals(ERROR)) continue;

            if(packetType.getPacket().equals(packet.getClass())) return packetType;
        }

        return ERROR;
    }
}
