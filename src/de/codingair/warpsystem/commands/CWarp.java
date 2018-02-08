package de.codingair.warpsystem.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarp extends CommandBuilder {
    public CWarp() {
        super("Warp", new BaseComponent() {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {

            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "This action is only for players!"), new Example("GER", "Diese Aktion ist nur für Spieler!")));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {

            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_HELP", new Example("ENG", "&7Use: &e" + label + " <Warp-Name>"), new Example("GER", "&7Benutze: &e/" + label + " <Warp-Name>")));
                return false;
            }
        }.setOnlyPlayers(true), true);

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                for(Warp warp : WarpSystem.getInstance().getIconManager().getWarps()) {
                    if(!warp.getNameWithoutColor().isEmpty()) suggestions.add(warp.getNameWithoutColor().replace(" ", "_"));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                argument = argument.replace("_", "");
                Warp warp = null;
                for(Warp w : WarpSystem.getInstance().getIconManager().getWarps()) {
                    if(w.getNameWithoutColor().equalsIgnoreCase(argument)) warp = w;

                    if(warp != null) break;
                }

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS", new Example("ENG", "&cThis warp does not exist."), new Example("GER", "&cDieser Warp existiert nicht.")));
                    return false;
                }

                WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, warp);
                return false;
            }
        });
    }
}
