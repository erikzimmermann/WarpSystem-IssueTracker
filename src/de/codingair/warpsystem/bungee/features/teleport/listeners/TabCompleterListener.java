package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.bungee.features.teleport.utils.TeleportCommandOptions;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompleterListener implements Listener {
    public static final String ID_TP = "§§WS-TP";
    public static final String ID_TPA = "§§WS-TPA";
    public static final String ID_TPA_HERE = "§§WS-TPA-HERE";
    public static final String ID_TP_HERE = "§§WS-TP-HERE";
    public static final String ACCESS = "§WS-TP-Access";

    private void finish(TabCompleteResponseEvent e) {
        if(e.getSuggestions().isEmpty()) e.setCancelled(true); //important to avoid error message on client
    }

    @EventHandler
    public void onResponse(TabCompleteResponseEvent e) {
        if(e.getSuggestions().size() != 2) return;

        boolean tp = false, tpa = false, tpaHere = false, tpHere = false;
        if((tp = e.getSuggestions().remove(ID_TP)) || (tpa = e.getSuggestions().remove(ID_TPA)) || (tpaHere = e.getSuggestions().remove(ID_TPA_HERE)) || (tpHere = e.getSuggestions().remove(ID_TP_HERE))) {
            boolean hasAccess = e.getSuggestions().remove(ACCESS);
            String cursor = e.getSuggestions().remove(0);

            String[] args = cursor.split(" ");

            ProxiedPlayer receiver = (ProxiedPlayer) e.getReceiver();
            ServerInfo info = receiver.getServer().getInfo();

            if(tp) {
                e.getSuggestions().clear();
                if(!hasAccess) {
                    for(ProxiedPlayer player : info.getPlayers()) {
                        e.getSuggestions().add(player.getName());
                    }
                    return;
                }

                int deep = args.length - 1;

                if(cursor.endsWith(" ")) {
                    if(deep == 1 && Character.isDigit(args[1].charAt(0)) && Players.getPlayer(args[1]) == null) {
                        finish(e);
                        return;
                    }
                    if(deep == 0 || deep == 1) {
                        for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
                            for(ProxiedPlayer player : server.getPlayers()) {
                                e.getSuggestions().add(player.getName());
                            }
                        }
                    }
                } else {
                    if(deep == 1 || deep == 2) {
                        String last = args[deep];

                        for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
                            for(ProxiedPlayer player : server.getPlayers()) {
                                if(!player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;
                                e.getSuggestions().add(player.getName());
                            }
                        }
                    }
                }
            } else if(tpa) {
                e.getSuggestions().clear();
                if(!hasAccess) {
                    for(ProxiedPlayer player : info.getPlayers()) {
                        if(player.getName().equals(receiver.getName())) continue;
                        if(!WarpSystem.getVanishManager().isVanished(player.getName())) e.getSuggestions().add(player.getName()); //check vanished player names
                    }
                    finish(e);
                    return;
                }

                String last = args[args.length - 1];

                for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
                    for(ProxiedPlayer player : server.getPlayers()) {
                        if(player.getName().equals(receiver.getName())) continue;
                        if(!cursor.endsWith(" ") && !player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;
                        if(!WarpSystem.getVanishManager().isVanished(player.getName())) e.getSuggestions().add(player.getName()); //check vanished player names
                    }
                }
            } else if(tpaHere) {
                e.getSuggestions().clear();
                if(!hasAccess) {
                    for(ProxiedPlayer player : info.getPlayers()) {
                        if(player.getName().equals(receiver.getName())) continue;
                        if(!WarpSystem.getVanishManager().isVanished(player.getName())) e.getSuggestions().add(player.getName()); //check vanished player names
                    }
                    finish(e);
                    return;
                }
                String last = args[args.length - 1];

                for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
                    for(ProxiedPlayer player : server.getPlayers()) {
                        if(player.getName().equals(receiver.getName())) continue;
                        if(!cursor.endsWith(" ") && !player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;
                        if(!WarpSystem.getVanishManager().isVanished(player.getName())) e.getSuggestions().add(player.getName()); //check vanished player names
                    }
                }
            } else if(tpHere) {
                e.getSuggestions().clear();
                if(!hasAccess) {
                    for(ProxiedPlayer player : info.getPlayers()) {
                        e.getSuggestions().add(player.getName());
                    }
                    finish(e);
                    return;
                }

                String last = args[args.length - 1];

                for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
                    TeleportCommandOptions access = TeleportManager.getInstance().getOptions(server);
                    if(access != null && access.isTp()) {
                        for(ProxiedPlayer player : server.getPlayers()) {
                            if(!cursor.endsWith(" ") && !player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;

                            e.getSuggestions().add(player.getName());
                        }
                    }
                }
            }
	    finish(e);
        }
    }
}
