package de.codingair.warpsystem.bungee.features.teleport.managers;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TabCompleterListener;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TeleportCommandListener;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TeleportPacketListener;
import de.codingair.warpsystem.bungee.features.teleport.utils.TeleportCommandOptions;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeleportManager implements Manager {
    private final HashMap<ServerInfo, TeleportCommandOptions> commandOptions = new HashMap<>();
    private final List<String> denyForceTpRequests = new ArrayList<>();
    private final List<String> denyForceTps = new ArrayList<>();

    public static TeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT);
    }

    @Override
    public boolean load(boolean loader) {
        if(!loader) WarpSystem.log("  > Initializing TeleportManager");

        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), new TabCompleterListener());
        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), new TeleportCommandListener());

        WarpSystem.getInstance().getDataHandler().register(new TeleportPacketListener());
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
    }

    public void removeOptions(ServerInfo info) {
        this.commandOptions.remove(info);
    }

    public void registerOptions(ServerInfo info, int options) {
        removeOptions(info);
        TeleportCommandOptions o;
        this.commandOptions.put(info, o = new TeleportCommandOptions(options));
    }

    public TeleportCommandOptions getOptions(ServerInfo info) {
        return this.commandOptions.get(info);
    }

    public boolean isAccessible(ServerInfo info) {
        TeleportCommandOptions options = getOptions(info);
        return options != null;
    }

    public boolean deniesForceTps(ProxiedPlayer player) {
        return this.denyForceTps.contains(player.getName());
    }

    public void setDenyForceTps(ProxiedPlayer player, boolean deny) {
        if(deny) {
            if(!this.denyForceTps.contains(player.getName())) this.denyForceTps.add(player.getName());
        } else this.denyForceTps.remove(player.getName());
    }

    public boolean deniesForceTpRequests(ProxiedPlayer player) {
        return this.denyForceTpRequests.contains(player.getName());
    }

    public void setDenyForceTpRequests(ProxiedPlayer player, boolean deny) {
        if(deny) {
            if(!this.denyForceTpRequests.contains(player.getName())) this.denyForceTpRequests.add(player.getName());
        } else this.denyForceTpRequests.remove(player.getName());
    }
}
