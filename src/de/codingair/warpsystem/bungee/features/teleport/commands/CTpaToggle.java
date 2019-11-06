package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.transfer.packets.bungee.SendDisablePacket;
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

        Lang.PREMIUM_CHAT(sender);
        WarpSystem.getInstance().getDataHandler().send(new SendDisablePacket(sender.getName(), "TELEPORT_COMMANDS"), ((ProxiedPlayer) sender).getServer().getInfo());
    }
}
