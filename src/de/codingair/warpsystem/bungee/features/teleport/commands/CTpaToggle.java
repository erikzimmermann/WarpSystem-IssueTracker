package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CTpaToggle extends Command {
    public CTpaToggle() {
        super("tpatoggle");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Only_For_Players")));
            return;
        }

        if(!sender.hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_TOGGLE)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("No_Permission")));
            return;
        }

        if(TeleportManager.getInstance().toggleDenyTpaRequest((ProxiedPlayer) sender))
            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_toggled_disabling"));
        else
            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_toggled_enabling"));
    }
}
