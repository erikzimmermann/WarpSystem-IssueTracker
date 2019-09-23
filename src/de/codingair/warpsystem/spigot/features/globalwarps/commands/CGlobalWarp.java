package de.codingair.warpsystem.spigot.features.globalwarps.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CGlobalWarp extends CommandBuilder implements BungeeFeature {
    public CGlobalWarp() {
        super("globalwarp", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_GLOBAL_WARPS) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().isOnBungeeCord()) sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                else sender.sendMessage(Lang.getPrefix() + Lang.get("Connect_BungeeCord"));
                return false;
            }
        }, true, "gw", "gwarp");

        WarpSystem.getInstance().getBungeeFeatureList().add(this);
    }

    @Override
    public void onConnect() {
        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.addAll(GlobalWarpManager.getInstance().getGlobalWarps().keySet());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(GlobalWarpManager.getInstance().exists(argument)) {
                    WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, Origin.GlobalWarp, new Destination(argument, DestinationType.GlobalWarp), argument, 0,
                            WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true));
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                }
                return false;
            }
        });
    }

    @Override
    public void onDisconnect() {
        getBaseComponent().removeChild((String) null);
    }
}
