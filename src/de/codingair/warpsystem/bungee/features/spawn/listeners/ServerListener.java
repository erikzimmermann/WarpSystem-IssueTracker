package de.codingair.warpsystem.bungee.features.spawn.listeners;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import de.codingair.warpsystem.bungee.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener, PacketListener {

    @EventHandler
    public void onInit(ServerInitializeEvent e) {
        WarpSystem.getInstance().getDataHandler().send(SpawnManager.getInstance().getInfoPacket(), e.getInfo());
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendGlobalSpawnOptionsPacket) {
            SendGlobalSpawnOptionsPacket p = (SendGlobalSpawnOptionsPacket) packet;
            SpawnManager.getInstance().update(BungeeCord.getInstance().getServerInfo(extra), p.getSpawn(), p.getRespawn());
        } else if(packet.getType() == PacketType.TeleportSpawnPacket) {
            TeleportSpawnPacket p = (TeleportSpawnPacket) packet;

            ProxiedPlayer player = BungeeCord.getInstance().getPlayer(p.getPlayer());
            ServerInfo server = BungeeCord.getInstance().getServerInfo(p.isRespawn() ? SpawnManager.getInstance().getRespawn() : SpawnManager.getInstance().getSpawn());

            if(player != null && server != null) {
                if(WarpSystem.getInstance().getServerManager().isOnline(server)) {
                    ServerManager.sendPlayerTo(server, player, new Callback<ServerInfo>() {
                        @Override
                        public void accept(ServerInfo server) {
                            WarpSystem.getInstance().getDataHandler().send(p, server);
                        }
                    });
                } else player.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Server_Is_Not_Online")));
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
