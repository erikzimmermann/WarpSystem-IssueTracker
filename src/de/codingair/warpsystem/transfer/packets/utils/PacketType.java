package de.codingair.warpsystem.transfer.packets.utils;

import de.codingair.warpsystem.bungee.base.utils.PacketVanishInfo;
import de.codingair.warpsystem.spigot.base.setupassistant.bungee.SetupAssistantStorePacket;
import de.codingair.warpsystem.spigot.base.setupassistant.bungee.ToggleSetupAssistantPacket;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.RandomTPWorldsPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ToggleForceTeleportsPacket;
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
    ChatInputGUITogglePacket(6, ChatInputGUITogglePacket.class),
    SendGlobalSpawnOptionsPacket(7, SendGlobalSpawnOptionsPacket.class),
    TeleportSpawnPacket(8, TeleportSpawnPacket.class),
    PacketVanishInfo(9, PacketVanishInfo.class),

    PublishGlobalWarpPacket(10, PublishGlobalWarpPacket.class),
    GlobalWarpTeleportPacket(11, GlobalWarpTeleportPacket.class),
    TeleportPacket(12, GlobalWarpTeleportPacket.class),
    DeleteGlobalWarpPacket(13, DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(14, RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(15, SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(16, UpdateGlobalWarpPacket.class),
    PerformCommandOnSpigotPacket(17, PerformCommandOnSpigotPacket.class),
    PerformCommandOnBungeePacket(18, PerformCommandOnBungeePacket.class),
    RequestUUIDPacket(19, RequestUUIDPacket.class),
    SendUUIDPacket(20, SendUUIDPacket.class),
    TeleportPlayerToPlayerPacket(21, TeleportPlayerToPlayerPacket.class),
    PrepareServerSwitchPacket(23, PrepareServerSwitchPacket.class),
    PrepareLoginMessagePacket(24, PrepareLoginMessagePacket.class),
    MessagePacket(25, MessagePacket.class),

    ToggleForceTeleportsPacket(35, ToggleForceTeleportsPacket.class),

    SendPlayerWarpsPacket(40, SendPlayerWarpsPacket.class),
    RegisterServerForPlayerWarpsPacket(41, RegisterServerForPlayerWarpsPacket.class),
    MoveLocalPlayerWarpsPacket(42, MoveLocalPlayerWarpsPacket.class),
    SendPlayerWarpUpdatesPacket(43, SendPlayerWarpUpdatePacket.class),
    PrepareCoordinationTeleportPacket(44, PrepareCoordinationTeleportPacket.class),
    SendPlayerWarpOptionsPacket(45, SendPlayerWarpOptionsPacket.class),
    DeletePlayerWarpPacket(46, DeletePlayerWarpPacket.class),
    PlayerWarpTeleportProcessPacket(47, PlayerWarpTeleportProcessPacket.class),

    RandomTPWorldsPacket(51, RandomTPWorldsPacket.class),
    ToggleSetupAssistantPacket(53, ToggleSetupAssistantPacket.class),
    SetupAssistantStorePacket(54, SetupAssistantStorePacket.class),
  
    IsOperatorPacket(60, IsOperatorPacket.class),
    SendDisablePacket(61, SendDisablePacket.class),
    IsOnlinePacket(62, IsOnlinePacket.class),
    GetOnlineCountPacket(63, GetOnlineCountPacket.class),

    ApplyUUIDPacket(75, ApplyUUIDPacket.class),

    BooleanPacket(100, BooleanPacket.class),
    IntegerPacket(101, IntegerPacket.class),
    LongPacket(102, LongPacket.class),

    AnswerPacket(200, AnswerPacket.class),
    ;

    private int id;
    private Class<?> packet;

    PacketType(int id, Class<?> packet) {
        this.id = id;
        this.packet = packet;
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

    public int getId() {
        return id;
    }

    public Class<?> getPacket() {
        return packet;
    }
}
