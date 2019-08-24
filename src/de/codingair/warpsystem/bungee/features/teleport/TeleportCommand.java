package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public interface TeleportCommand {
    default List<ProxiedPlayer> getPlayers(ProxiedPlayer player) {
        List<ProxiedPlayer> l = new ArrayList<>();

        if(player.hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_BUNGEE_ACCESS)) {
            l.addAll(BungeeCord.getInstance().getPlayers());
        } else {
            for(ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
                if(!p.getName().equals(player.getName()) && p.getServer().getInfo().equals(player.getServer().getInfo())) {
                    l.add(p);
                }
            }
        }

        return l;
    }
}
