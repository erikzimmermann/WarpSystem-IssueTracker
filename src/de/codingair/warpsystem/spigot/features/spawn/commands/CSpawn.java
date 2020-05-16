package de.codingair.warpsystem.spigot.features.spawn.commands;

import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

public class CSpawn extends WSCommandBuilder {
    public CSpawn() {
        super("Spawn", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_SPAWN) {

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                runCommand(sender, label, args);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Spawn spawn = SpawnManager.getInstance().getSpawn();

                if(spawn == null || !spawn.isValid()) sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                else if(!spawn.switchServer() && (spawn.getUsage() == Spawn.Usage.FIRST_JOIN || spawn.getUsage() == Spawn.Usage.EVERY_JOIN || spawn.getUsage() == Spawn.Usage.DISABLED)) {
                    sender.sendMessage(SpigotConfig.unknownCommandMessage);
                } else spawn.perform((Player) sender);

                return false;
            }
        }.setOnlyPlayers(true));

        setHighestPriority(true);
    }
}
