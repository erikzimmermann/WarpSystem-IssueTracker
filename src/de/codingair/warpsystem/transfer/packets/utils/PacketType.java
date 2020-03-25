package de.codingair.warpsystem.transfer.packets.utils;

import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ClearInvitesPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.*;
import de.codingair.warpsystem.transfer.packets.general.*;
import de.codingair.warpsystem.transfer.packets.spigot.*;

public enum PacketType {
    ERROR(0, null),
    UploadIconPacket(1, UploadIconPacket.class),
    DeployIconPacket(2, DeployIconPacket.class),
    InitialPacket(3, InitialPacket.class),
    RequestInitialPacket(4, RequestInitialPacket.class),
    RequestServerStatusPacket(5, RequestServerStatusPacket.class),

    PublishGlobalWarpPacket(10, PublishGlobalWarpPacket.class),
    PrepareTeleportPacket(11, PrepareGlobalWarpTeleportPacket.class),
    TeleportPacket(12, TeleportPacket.class),
    DeleteGlobalWarpPacket(13, DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(14, RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(15, SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(16, UpdateGlobalWarpPacket.class),
    PerformCommandOnSpigotPacket(17, PerformCommandOnSpigotPacket.class),
    PerformCommandOnBungeePacket(18, PerformCommandOnBungeePacket.class),
    RequestUUIDPacket(19, RequestUUIDPacket.class),
    SendUUIDPacket(20, SendUUIDPacket.class),
    TeleportPlayerToPlayerPacket(21, TeleportPlayerToPlayerPacket.class),
    TeleportPlayerToCoordsPacket(22, TeleportPlayerToCoordsPacket.class),
    PrepareServerSwitchPacket(23, PrepareServerSwitchPacket.class),
    PrepareLoginMessagePacket(24, PrepareLoginMessagePacket.class),
    MessagePacket(25, MessagePacket.class),

    TeleportCommandOptions(30, TeleportCommandOptionsPacket.class),
    ClearInvitesPacket(31, ClearInvitesPacket.class),
    PrepareTeleportPlayerToPlayerPacket(32, PrepareTeleportPlayerToPlayerPacket.class),
    PrepareTeleportRequestPacket(33, PrepareTeleportRequestPacket.class),
    StartTeleportToPlayerPacket(34, de.codingair.warpsystem.transfer.packets.general.StartTeleportToPlayerPacket.class),

    SendPlayerWarpsPacket(40, SendPlayerWarpsPacket.class),
    RegisterServerForPlayerWarpsPacket(41, RegisterServerForPlayerWarpsPacket.class),
    MoveLocalPlayerWarpsPacket(42, MoveLocalPlayerWarpsPacket.class),
    SendPlayerWarpUpdatesPacket(43, SendPlayerWarpUpdatePacket.class),
    PrepareCoordinationTeleportPacket(44, PrepareCoordinationTeleportPacket.class),
    SendPlayerWarpOptionsPacket(45, SendPlayerWarpOptionsPacket.class),
    DeletePlayerWarpPacket(46, DeletePlayerWarpPacket.class),
    PlayerWarpTeleportProcessPacket(47, PlayerWarpTeleportProcessPacket.class),

    IsOperatorPacket(50, IsOperatorPacket.class),
    SendDisablePacket(51, SendDisablePacket.class),

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
