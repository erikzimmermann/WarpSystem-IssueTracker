package de.codingair.warpsystem.spigot.features.spawn.commands;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_MODIFY_SPAWN)) {
                        TextComponent tc = new TextComponent(Lang.getPrefix() + Lang.get("Hidden_command_info"));
                        tc.setColor(ChatColor.GRAY);
                        SimpleMessage message = new SimpleMessage(tc, WarpSystem.getInstance());

                        tc = new TextComponent(Lang.get("Advanced_Options_Info_Edit"));
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setspawn edit"));
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Lang.get("Click_Hover"))}));

                        message.replace("%HERE%", tc);
                        message.send((Player) sender);
                        spawn.perform((Player) sender);
                    } else sender.sendMessage(SpigotConfig.unknownCommandMessage);
                } else spawn.perform((Player) sender);

                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("firstjoin", WarpSystem.PERMISSION_MODIFY_SPAWN) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Spawn spawn = SpawnManager.getInstance().getSpawn();
                if(spawn == null || spawn.getFirstJoin() == null || spawn.getFirstJoin().getWorld() == null) getBaseComponent().runCommand(sender, label, args);
                else spawn.teleportToFirstJoin((Player) sender);
                return false;
            }
        });
    }
}
