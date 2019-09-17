package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class CTpAll extends Command {
    public CTpAll() {
        super("tpall", WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPALL);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;

        if(args.length == 1 && args[0].equalsIgnoreCase("true")) {
            if(TeleportManager.getInstance().hasOpenInvites((ProxiedPlayer) sender)) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Open_Requests"));
                return;
            }

            List<ProxiedPlayer> players = new ArrayList<>();
            int max = 0;

            if(!TeleportManager.getInstance().getAccessibleServers().contains(((ProxiedPlayer) sender).getServer().getInfo())) {
                for(ProxiedPlayer receiver : ((ProxiedPlayer) sender).getServer().getInfo().getPlayers()) {
                    if(!receiver.getName().equals(sender.getName())) {
                        max++;
                        if(!TeleportManager.getInstance().deniesTpaRequests(receiver)) players.add(receiver);
                    }
                }
            } else {
                for(ProxiedPlayer receiver : BungeeCord.getInstance().getPlayers()) {
                    if(receiver.getName().equals(sender.getName())) continue;

                    if(!receiver.getName().equals(sender.getName())
                            && TeleportManager.getInstance().getAccessibleServers().contains(receiver.getServer().getInfo())) {
                        max++;
                        if(!TeleportManager.getInstance().deniesTpaRequests(receiver)) players.add(receiver);
                    }
                }
            }

            TeleportManager.getInstance().sendTeleportRequest((ProxiedPlayer) sender, true, false, players.toArray(new ProxiedPlayer[0]));
            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", players.size() + "").replace("%MAX%", max + ""));
        } else if(args.length == 1 && args[0].equalsIgnoreCase("false")) {
            List<ProxiedPlayer> players = new ArrayList<>();
            int max = 0;

            for(ProxiedPlayer receiver : BungeeCord.getInstance().getPlayers()) {
                if(receiver.getName().equals(sender.getName())) continue;

                if(!receiver.getName().equals(sender.getName())
                        && TeleportManager.getInstance().getAccessibleServers().contains(receiver.getServer().getInfo())) {
                    max++;
                    if(!TeleportManager.getInstance().deniesForceTps(receiver)) players.add(receiver);
                }
            }

            ProxiedPlayer target = (ProxiedPlayer) sender;
            for(ProxiedPlayer player : players) {
                //tp
                TeleportPlayerToPlayerPacket packet = new TeleportPlayerToPlayerPacket(sender.getName(), player.getName(), target.getName(), false);
                if(!player.getServer().getInfo().equals(target.getServer().getInfo())) {
                    player.connect(target.getServer().getInfo(), (connected, throwable) -> {
                        if(connected)
                            WarpSystem.getInstance().getDataHandler().send(packet, target.getServer().getInfo());
                    });
                } else
                    WarpSystem.getInstance().getDataHandler().send(packet, target.getServer().getInfo());
            }

            sender.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", players.size() + "").replace("%MAX%", max + ""));
        } else {
            sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpAll <§eteleport request: true|false§7>");
        }
    }
}
