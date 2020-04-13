package de.codingair.warpsystem.bungee.features.teleport.managers;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.teleport.commands.*;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TabCompleterListener;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TeleportCommandListener;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TeleportPacketListener;
import de.codingair.warpsystem.bungee.features.teleport.utils.TeleportCommandOptions;
import de.codingair.warpsystem.transfer.packets.bungee.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeleportManager implements Manager {
    private HashMap<ServerInfo, TeleportCommandOptions> commandOptions = new HashMap<>();
    private List<String> hasInvites = new ArrayList<>();
    private List<String> denyTpa = new ArrayList<>();
    private List<String> denyForceTps = new ArrayList<>();

    public static TeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT);
    }

    @Override
    public boolean load(boolean loader) {
        if(!loader) WarpSystem.log("  > Initializing TeleportManager");

        CTeleport teleport;
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), teleport = new CTeleport());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpHere(teleport));
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpAll());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpaAll());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpa());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpaHere());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpToggle());
        BungeeCord.getInstance().getPluginManager().registerCommand(WarpSystem.getInstance(), new CTpaToggle());
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
        this.commandOptions.put(info, new TeleportCommandOptions(options));
    }

    public TeleportCommandOptions getOptions(ServerInfo info) {
        return this.commandOptions.get(info);
    }

    public boolean isAccessible(ServerInfo info) {
        TeleportCommandOptions options = getOptions(info);
        return options != null && options.isBungeeCord();
    }

    public boolean deniesTpaRequests(ProxiedPlayer player) {
        return this.denyTpa.contains(player.getName());
    }

    public boolean toggleDenyTpaRequest(ProxiedPlayer player) {
        if(this.denyTpa.contains(player.getName())) {
            this.denyTpa.remove(player.getName());
            return false;
        } else {
            this.denyTpa.add(player.getName());
            return true;
        }
    }

    public boolean deniesForceTps(ProxiedPlayer player) {
        return this.denyForceTps.contains(player.getName());
    }

    public boolean toggleDenyForceTps(ProxiedPlayer player) {
        if(this.denyForceTps.contains(player.getName())) {
            this.denyForceTps.remove(player.getName());
            return false;
        } else {
            this.denyForceTps.add(player.getName());
            return true;
        }
    }

    public boolean hasOpenInvites(ProxiedPlayer player) {
        return this.hasInvites.contains(player.getName());
    }

    public void clear(String name) {
        this.hasInvites.remove(name);
    }

    public void clearAll() {
        this.hasInvites.clear();
    }

    /**
     * @param sender     ProxiedPlayer
     * @param tpToSender boolean
     * @param receiver   ProxiedPlayer
     * @return the amount of players, who received the tp request
     */
    public void sendTeleportRequest(ProxiedPlayer sender, boolean tpToSender, boolean notifySender, ProxiedPlayer... receiver) {
        if(this.hasInvites.contains(sender.getName())) return;
        if(receiver.length == 0) return;
        this.hasInvites.add(sender.getName());

        for(ProxiedPlayer player : receiver) {
            WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportRequestPacket(sender.getName(), sender.getDisplayName(), player.getName(), tpToSender, notifySender), player.getServer().getInfo());
        }
    }
}
