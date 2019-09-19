package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class CTpaAll extends Command {
    public CTpaAll() {
        super("tpaall");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Only_For_Players")));
            return;
        }

        if(!sender.hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_ALL)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("No_Permission")));
            return;
        }

        if(TeleportManager.getInstance().hasOpenInvites((ProxiedPlayer) sender)) {
            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Open_Requests"));
            return;
        }

        List<ProxiedPlayer> players = new ArrayList<>();
        int max = 0;

        if(!TeleportManager.getInstance().isAccessible(((ProxiedPlayer) sender).getServer().getInfo())) {
            for(ProxiedPlayer receiver : ((ProxiedPlayer) sender).getServer().getInfo().getPlayers()) {
                if(!receiver.getName().equals(sender.getName())) {
                    max++;
                    if(!TeleportManager.getInstance().deniesTpaRequests(receiver)) players.add(receiver);
                }
            }
        } else {
            for(ProxiedPlayer receiver : BungeeCord.getInstance().getPlayers()) {
                if(!receiver.getName().equals(sender.getName())
                        && TeleportManager.getInstance().isAccessible(receiver.getServer().getInfo())) {
                    max++;
                    if(!TeleportManager.getInstance().deniesTpaRequests(receiver)) players.add(receiver);
                }
            }
        }

        TeleportManager.getInstance().sendTeleportRequest((ProxiedPlayer) sender, true, false, players.toArray(new ProxiedPlayer[0]));
        sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", players.size() + "").replace("%MAX%", max + ""));
    }
}
