package de.codingair.warpsystem.bungee.features.teleport;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompleterListener implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        if(!e.getCursor().startsWith("/")) return;
        String cmd = e.getCursor().split(" ")[0];
        cmd = cmd.replaceFirst("/", "");
        String[] args = e.getCursor().split(" ");

        if(cmd.equalsIgnoreCase("teleport") || cmd.equalsIgnoreCase("tp")) {
            e.getSuggestions().clear();

            int deep = args.length - 1;

            if(e.getCursor().endsWith(" ")) {
                if(deep == 1 && Character.isDigit(args[1].charAt(0)) && BungeeCord.getInstance().getPlayer(args[1]) == null) return;
                if(deep == 0 || deep == 1) {
                    for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
                        e.getSuggestions().add(player.getName());
                    }
                }
            } else {
                if(deep == 1 || deep == 2) {
                    String last = args[deep];

                    for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
                        if(!player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;
                        e.getSuggestions().add(player.getName());
                    }
                }
            }
        }
    }
}
