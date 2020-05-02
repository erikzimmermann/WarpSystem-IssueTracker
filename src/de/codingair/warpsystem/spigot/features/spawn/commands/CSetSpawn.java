package de.codingair.warpsystem.spigot.features.spawn.commands;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.features.spawn.guis.SpawnEditor;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CSetSpawn extends CommandBuilder {
    public CSetSpawn() {
        super("setspawn", "A WarpSystem-Command", new WarpSystemBaseComponent(WarpSystem.PERMISSION_MODIFY_SPAWN) {

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                SpawnManager.getInstance().updateSpawn(((Player) sender).getLocation());
                sender.sendMessage(Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved"));

                SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Advanced_Options_Info"), WarpSystem.getInstance());

                TextComponent tc = new TextComponent(Lang.get("Advanced_Options_Info_Edit"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setspawn edit"));
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));

                message.replace("%EDIT%", tc);

                message.send((Player) sender);

                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Spawn spawn = SpawnManager.getInstance().getSpawn();

                if(spawn == null) {
                    sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /§e" + label);
                    return false;
                }

                new SpawnEditor((Player) sender, spawn).open();
                return false;
            }
        });
    }
}
