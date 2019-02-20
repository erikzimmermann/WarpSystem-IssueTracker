package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CTeleport extends Command {
    public CTeleport() {
        super("teleport", WarpSystem.PERMISSION_TELEPORT_COMMAND, "tp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;

        ProxiedPlayer p = (ProxiedPlayer) sender;

        try {
            if(args.length == 1 || args.length == 2) {
                //player [to player]
                if(args.length == 1) {
                    //Teleport sender to 0
                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
                    tp(p, p, target);
                } else {
                    //Teleport 0 to 1
                    ProxiedPlayer player = BungeeCord.getInstance().getPlayer(args[0]);
                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[1]);
                    tp(p, player, target);
                }
            } else if(args.length == 3 || args.length == 4) {
                //player to coords

                if(args.length == 3) {
                    //Teleport sender to coords
                    double x = args[0].replace(",", ".").contains(".") ? Double.parseDouble(args[0].replace(",", ".")) : Integer.parseInt(args[0]);
                    double y = args[1].replace(",", ".").contains(".") ? Double.parseDouble(args[1].replace(",", ".")) : Integer.parseInt(args[1]);
                    double z = args[2].replace(",", ".").contains(".") ? Double.parseDouble(args[2].replace(",", ".")) : Integer.parseInt(args[2]);
                    tp(p, p, x, y, z);
                } else {
                    //Teleport 0 to coords
                    ProxiedPlayer player = BungeeCord.getInstance().getPlayer(args[0]);

                    double x = args[1].replace(",", ".").contains(".") ? Double.parseDouble(args[1].replace(",", ".")) : Integer.parseInt(args[1]);
                    double y = args[2].replace(",", ".").contains(".") ? Double.parseDouble(args[2].replace(",", ".")) : Integer.parseInt(args[2]);
                    double z = args[3].replace(",", ".").contains(".") ? Double.parseDouble(args[3].replace(",", ".")) : Integer.parseInt(args[3]);
                    tp(p, player, x, y, z);
                }
            } else {
                //HELP
                //TODO: translate
                sender.sendMessage("§cUse: /tp <player> [player] | /tp [player] <x> <y> <z>");
            }
        } catch(NumberFormatException ex) {
            //HELP
            //TODO: translate
            sender.sendMessage("§cUse: /tp <player> [player] | /tp [player] <x> <y> <z>");
        }
    }

    private void tp(ProxiedPlayer gate, ProxiedPlayer player, double x, double y, double z) {
        if(gate == null || player == null) {
            //Not online!
            return;
        }

        TeleportPlayerToCoordsPacket packet = new TeleportPlayerToCoordsPacket(gate.getName(), player.getName(), x, y, z);
        WarpSystem.getInstance().getDataHandler().send(packet, player.getServer().getInfo());
    }

    private void tp(ProxiedPlayer gate, ProxiedPlayer player, ProxiedPlayer target) {
        if(gate == null || player == null || target == null) {
            //Not online!
            return;
        }

        Runnable runnable = () -> {
            TeleportPlayerToPlayerPacket packet = new TeleportPlayerToPlayerPacket(gate.getName(), player.getName(), target.getName());
            WarpSystem.getInstance().getDataHandler().send(packet, target.getServer().getInfo());
        };

        if(!player.getServer().equals(target.getServer())) {
            player.connect(target.getServer().getInfo(), (connected, throwable) -> {
                if(connected) runnable.run();
            });
        } else runnable.run();
    }
}
