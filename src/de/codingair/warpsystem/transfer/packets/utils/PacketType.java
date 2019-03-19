package de.codingair.warpsystem.transfer.packets.utils;

import de.codingair.warpsystem.transfer.packets.bungee.*;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.*;

public enum PacketType {
    ERROR(0, null),
    UploadIconPacket(1, UploadIconPacket.class),
    DeployIconPacket(2, DeployIconPacket.class),
    InitialPacket(3, InitialPacket.class),
    RequestInitialPacket(4, RequestInitialPacket.class),
    RequestServerStatusPacket(5, RequestServerStatusPacket.class),

    PublishGlobalWarpPacket(10, PublishGlobalWarpPacket.class),
    PrepareTeleportPacket(11, PrepareTeleportPacket.class),
    TeleportPacket(12, TeleportPacket.class),
    DeleteGlobalWarpPacket(13, DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(14, RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(15, SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(16, UpdateGlobalWarpPacket.class),
    PerformCommandPacket(17, PerformCommandPacket.class),
    RequestUUIDPacket(18, RequestUUIDPacket.class),
    SendUUIDPacket(19, SendUUIDPacket.class),
    TeleportPlayerToPlayerPacket(20, TeleportPlayerToPlayerPacket.class),
    TeleportPlayerToCoordsPacket(21, TeleportPlayerToCoordsPacket.class),
    PrepareServerSwitchPacket(22, PrepareServerSwitchPacket.class),
    PrepareLoginMessagePacket(23, PrepareLoginMessagePacket.class),

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
