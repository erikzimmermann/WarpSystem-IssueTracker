package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().replaceFirst("/", "");
        if(cmd.contains(" ")) cmd = cmd.split(" ")[0];

        PluginCommand command = Bukkit.getPluginCommand(cmd);
        if(command == null) return;
        if(command.getPlugin().getName().equals(WarpSystem.getInstance().getDescription().getName()) && !(command.getExecutor() instanceof CommandBuilder)) {
            e.getPlayer().sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
        }
    }

}
