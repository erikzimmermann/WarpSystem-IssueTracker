package de.codingair.warpsystem.bungee.features.teleport.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CTpHere extends Command {
    private CTeleport teleport;

    public CTpHere(CTeleport teleport) {
        super("tphere");

        this.teleport = teleport;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Only_For_Players")));
            return;
        }

        if(!sender.hasPermission(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP)) {
            sender.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("No_Permission")));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        try {
            if(args.length == 1) {
                //Teleport sender to 0
                ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
                teleport.tp(p, target, p);
            } else {
                //HELP
                p.sendMessage(new TextComponent(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpHere <§eplayer§7>"));
            }
        } catch(NumberFormatException ex) {
            //HELP
            p.sendMessage(new TextComponent(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpHere <§eplayer§7>"));
        }
    }
}
