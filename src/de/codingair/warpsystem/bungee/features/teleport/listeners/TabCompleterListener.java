package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.bungee.features.teleport.utils.TeleportCommandOptions;
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

        TeleportCommandOptions options = TeleportManager.getInstance().getOptions(((ProxiedPlayer) e.getSender()).getServer().getInfo());

        if(cmd.equalsIgnoreCase("tpa")) {
            e.getSuggestions().clear();
            if(options == null || !options.isTpa()) return;
            if(!((ProxiedPlayer) e.getSender()).hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA)) return;

            if(!TeleportManager.getInstance().isAccessible(((ProxiedPlayer) e.getSender()).getServer().getInfo())) {
                for(ProxiedPlayer player : ((ProxiedPlayer) e.getSender()).getServer().getInfo().getPlayers()) {
                    if(player.getName().equals(((ProxiedPlayer) e.getSender()).getName())) continue;
                    e.getSuggestions().add(player.getName());
                }
            } else {
                for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
                    if(player.getName().equals(((ProxiedPlayer) e.getSender()).getName())) continue;
                    if(!((ProxiedPlayer) e.getSender()).getServer().getInfo().equals(player.getServer().getInfo()) && (
                            !TeleportManager.getInstance().isAccessible(((ProxiedPlayer) e.getSender()).getServer().getInfo()) || !TeleportManager.getInstance().isAccessible(player.getServer().getInfo())
                    )) continue;
                    e.getSuggestions().add(player.getName());
                }
            }
        } else if(cmd.equalsIgnoreCase("tpahere")) {
            e.getSuggestions().clear();
            if(options == null || !options.isTpaHere()) return;
            if(!((ProxiedPlayer) e.getSender()).hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_HERE)) return;

            if(!TeleportManager.getInstance().isAccessible(((ProxiedPlayer) e.getSender()).getServer().getInfo())) {
                for(ProxiedPlayer player : ((ProxiedPlayer) e.getSender()).getServer().getInfo().getPlayers()) {
                    if(player.getName().equals(((ProxiedPlayer) e.getSender()).getName())) continue;
                    e.getSuggestions().add(player.getName());
                }
            } else {
                for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
                    if(player.getName().equals(((ProxiedPlayer) e.getSender()).getName())) continue;
                    if(!((ProxiedPlayer) e.getSender()).getServer().getInfo().equals(player.getServer().getInfo()) && (
                            !TeleportManager.getInstance().isAccessible(((ProxiedPlayer) e.getSender()).getServer().getInfo()) || !TeleportManager.getInstance().isAccessible(player.getServer().getInfo())
                    )) continue;
                    e.getSuggestions().add(player.getName());
                }
            }
        }
    }
}
