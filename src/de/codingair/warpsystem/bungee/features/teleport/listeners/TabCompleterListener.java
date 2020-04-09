package de.codingair.warpsystem.bungee.features.teleport.listeners;

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
        String[] args = e.getCursor().split(" ", -1);

        if(cmd.equalsIgnoreCase("tp") || cmd.equalsIgnoreCase("teleport")) {
            if(args.length > 3) {
                e.getSuggestions().clear();
                return;
            }

            String last = args[args.length - 1].toLowerCase();
            if(last.isEmpty()) last = null;

            for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
                if(args.length == 1) e.getSuggestions().add(player.getName());
                else if(last == null || player.getName().toLowerCase().startsWith(last)) e.getSuggestions().add(player.getName());
            }
        }
    }
}
