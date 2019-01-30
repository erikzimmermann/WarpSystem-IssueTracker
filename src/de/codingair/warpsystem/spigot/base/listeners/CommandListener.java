package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().replaceFirst("/", "");
        if(cmd.contains(" ")) cmd = cmd.split(" ")[0];

        PluginCommand command = Bukkit.getPluginCommand(cmd);
        if(command == null) return;
        if(command.getExecutor() instanceof Plugin && ((Plugin) command.getExecutor()).getName().equals(WarpSystem.getInstance().getName())) {
            e.setCancelled(true);
            String feature = getFeatureName(command);

            if(e.getPlayer().hasPermission(WarpSystem.PERMISSION_NOTIFY) && feature != null) {
                if(feature.equalsIgnoreCase("GlobalWarps")) {
                    e.getPlayer().sendMessage(new String[] {Lang.getPrefix() + "§7You have to §cinstall§7 this plugin §con your BungeeCord", "§7and enable '§4§l" + feature + "§7' in the Config.yml to use this command!"});
                } else {
                    e.getPlayer().sendMessage(Lang.getPrefix() + "§7You have to enable '§4§l" + feature + "§7' in the Config.yml to use this command!");
                }
            } else e.getPlayer().sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
        }
    }

    private String getFeatureName(PluginCommand command) {
        String name = command.getName().toLowerCase();
        if(name.startsWith("warp")) return "Warps";
        else if(name.startsWith("portal")) return "Portals";
        else if(name.startsWith("globalwarp")) return "GlobalWarps";
        else if(name.startsWith("nativeportal")) return "NativePortals";
        else if(name.startsWith("tempwarp")) return "TempWarps";
        else if(name.startsWith("setwarp")) return "Warps";
        else if(name.startsWith("deletewarp")) return "Warps";
        else if(name.startsWith("editwarp")) return "Warps";
        else return null;
    }

}
