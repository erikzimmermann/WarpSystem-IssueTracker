package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.bungee.features.teleport.utils.TeleportCommandOptions;
import de.codingair.warpsystem.transfer.packets.bungee.PerformCommandOnSpigotPacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;

public class TeleportCommandListener implements Listener {

    @EventHandler
    public void onPreProcess(ChatEvent e) {
        if(e.getMessage() == null) return;
        if(!e.getMessage().startsWith("/")) return;
        String cmd = e.getMessage().substring(1);
        if(cmd.contains(" ")) cmd = cmd.split(" ")[0];

        Command c = null;
        for(Map.Entry<String, Command> command : BungeeCord.getInstance().getPluginManager().getCommands()) {
            if(isCommand(command.getValue(), cmd)) {
                c = command.getValue();
                break;
            }
        }

        ServerInfo info = ((ProxiedPlayer) e.getSender()).getServer().getInfo();
        if(c != null && !isEnabled(info, c)) {
            WarpSystem.getInstance().getDataHandler().send(new PerformCommandOnSpigotPacket(((ProxiedPlayer) e.getSender()).getName(), e.getMessage().substring(1)), info);
            e.setCancelled(true);
        }
    }

    public boolean isEnabled(ServerInfo info, Command command) {
        TeleportCommandOptions options = TeleportManager.getInstance().getOptions(info);

        switch(command.getName().toLowerCase()) {
            case "tphere":
            case "teleport":
                return options != null && options.isTp();
            case "tpa":
                return options != null && options.isTpa();
            case "tpaall":
                return options != null && options.isTpaAll();
            case "tpahere":
                return options != null && options.isTpaHere();
            case "tpall":
                return options != null && options.isTpAll();
            case "tpatoggle":
                return options != null && options.isTpaToggle();
            case "tptoggle":
                return options != null && options.isTpToggle();
        }

        return true;
    }

    public boolean isCommand(Command command, String name) {
        if(command.getName().equalsIgnoreCase(name)) return true;

        for(String alias : command.getAliases()) {
            if(alias.equalsIgnoreCase(name)) return true;
        }

        return false;
    }
}
