package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
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
            FeatureType feature = getFeatureName(command);
            if(feature.isActive()) return;

            e.setCancelled(true);
            if(e.getPlayer().hasPermission(WarpSystem.PERMISSION_NOTIFY) && feature != null) {
                if(feature == FeatureType.GLOBAL_WARPS) {
                    e.getPlayer().sendMessage(new String[] {Lang.getPrefix() + "§7You have to §cinstall§7 this plugin §con your BungeeCord", "§7and enable '§4§l" + feature.getName() + "§7' in the Config.yml to use this command!"});
                } else {
                    e.getPlayer().sendMessage(Lang.getPrefix() + "§7You have to enable '§4§l" + feature.getName() + "§7' in the Config.yml to use this command!");
                }
            } else e.getPlayer().sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
        }
    }

    private FeatureType getFeatureName(PluginCommand command) {
        String name = command.getName().toLowerCase();
        if(name.startsWith("warp")) return FeatureType.WARPS;
        else if(name.startsWith("portal")) return FeatureType.PORTALS;
        else if(name.startsWith("globalwarp")) return FeatureType.GLOBAL_WARPS;
        else if(name.startsWith("nativeportal")) return FeatureType.NATIVE_PORTALS;
        else if(name.startsWith("tempwarp")) return FeatureType.TEMP_WARPS;
        else if(name.startsWith("setwarp")) return FeatureType.WARPS;
        else if(name.startsWith("deletewarp")) return FeatureType.WARPS;
        else if(name.startsWith("editwarp")) return FeatureType.WARPS;
        else return null;
    }

}
