package de.codingair.warpsystem.commands;

import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.codingair.warpsystem.Language.Lang;
import de.codingair.warpsystem.gui.guis.GWarps;
import de.codingair.warpsystem.Language.Example;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Category;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CWarps implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "This action is only for players!"), new Example("GER", "Diese Aktion ist nur für Spieler!")));
            return false;
        }
        Player p = (Player) sender;

        if(!WarpSystem.activated) return false;

        if(WarpSystem.maintenance && !p.hasPermission(WarpSystem.PERMISSION_ByPass_Maintenance)) {
            p.sendMessage(Lang.getPrefix() + Lang.get("Warning_Maintenance",
                    new Example("ENG", "&cThe WarpSystem is currently in maintenance mode, please try it later again."),
                    new Example("GER", "&cDas WarpSystem ist momentan im Wartungs-Modus, bitte versuche es später erneut.")));
            return false;
        }

        Category category = null;

        if(args.length != 0) {
            category = WarpSystem.getInstance().getIconManager().getCategory(args[0]);
        }

        new GWarps(p, category, false).open();
        Sound.LEVEL_UP.playSound(p);
        return false;
    }
}
