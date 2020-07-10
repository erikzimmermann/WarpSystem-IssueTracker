package de.codingair.warpsystem.spigot.base.utils;

import com.earth2me.essentials.Warps;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Notifier {
    public static void notifyPlayers(Player player) {
        if(player == null) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                notifyPlayers(p);
            }
        } else {
            if(player.hasPermission(WarpSystem.PERMISSION_NOTIFY) && WarpSystem.updateAvailable) {
                String v = WarpSystem.getInstance().getUpdateNotifier().getVersion();
                if(!v.startsWith("v")) v = "v" + v;

                TextComponent tc0 = new TextComponent(Lang.getPrefix() + "§7A new update is available §8[§b" + v + "§8 - §b" + WarpSystem.getInstance().getUpdateNotifier().getUpdateInfo() + "§8]§7. Download it §7»");
                TextComponent click = new TextComponent("§chere");
                TextComponent tc1 = new TextComponent("§7«!");

                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WarpSystem.getInstance().getUpdateNotifier().getDownload()));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                tc0.addExtra(click);
                tc0.addExtra(tc1);
                tc0.setColor(ChatColor.GRAY);

                player.sendMessage("");
                player.sendMessage("");
                player.spigot().sendMessage(tc0);
                player.sendMessage("");
            }

            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
            if(!file.getConfig().getString("Do_Not_Edit.Last_Version").equals(WarpSystem.getInstance().getDescription().getVersion())) {
                file.getConfig().set("Do_Not_Edit.Last_Version", WarpSystem.getInstance().getDescription().getVersion());
                file.saveConfig();
            }
        }
    }
}
