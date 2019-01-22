package de.codingair.warpsystem.spigot.features.globalwarps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarps;
import de.codingair.warpsystem.spigot.features.globalwarps.listeners.GlobalWarpListener;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalWarpManager implements Manager, BungeeFeature {
    //              Name,   Server
    private HashMap<String, String> globalWarps = new HashMap<>();
    private boolean globalWarpsOfGUI = false;
    private GlobalWarpListener listener;
    private List<CommandBuilder> commandExecutorList = new ArrayList<>();

    public void create(String warpName, Location loc, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new PublishGlobalWarpPacket(new SGlobalWarp(warpName, new SLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())), callback));
    }

    public void delete(String warpName, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new DeleteGlobalWarpPacket(warpName, callback));
    }

    public HashMap<String, String> getGlobalWarps() {
        return globalWarps;
    }

    public String getCaseCorrectlyName(String name) {
        for(String warp : this.globalWarps.keySet()) {
            if(warp.equalsIgnoreCase(name)) return warp;
        }

        return name;
    }

    public boolean exists(String name) {
        for(String warp : this.globalWarps.keySet()) {
            if(warp.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    public void teleport(Player player, String display, String name, Callback<PrepareTeleportPacket.Result> callback) {
        teleport(player, display, name, 0, callback);
    }

    public void teleport(Player player, String display, String name, double costs, Callback<PrepareTeleportPacket.Result> callback) {
        if(name == null) {
            callback.accept(PrepareTeleportPacket.Result.WARP_NOT_EXISTS);
            return;
        }

        name = getCaseCorrectlyName(name);
        if(!this.globalWarps.containsKey(name)) {
            callback.accept(PrepareTeleportPacket.Result.WARP_NOT_EXISTS);
            return;
        }

        WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPacket(player.getName(), name, display, costs, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
                callback.accept(PrepareTeleportPacket.Result.getById(object));
            }
        }));
    }

    @Override
    public boolean load() {
        this.globalWarps.clear();
        this.commandExecutorList.clear();

        WarpSystem.getInstance().getBungeeFeatureList().add(this);

        ConfigFile config = WarpSystem.getInstance().getFileManager().getFile("Config");
        Object test = config.getConfig().get("WarpSystem.GlobalWarps.Use_Warps_Of_WarpsGUI", null);
        if(test == null) {
            config.getConfig().set("WarpSystem.GlobalWarps.Use_Warps_Of_WarpsGUI", false);
        } else if(test instanceof Boolean) {
            globalWarpsOfGUI = (boolean) test;
        }

        WarpSystem.getInstance().getDataHandler().register(listener = new GlobalWarpListener());
        Bukkit.getPluginManager().registerEvents(listener, WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
        this.globalWarps.clear();
        this.commandExecutorList.clear();
    }

    @Override
    public void onConnect() {
        this.commandExecutorList.add(new CGlobalWarp());
        this.commandExecutorList.add(new CGlobalWarps());
        this.commandExecutorList.forEach(c -> c.register(WarpSystem.getInstance()));

        if(getGlobalWarps().isEmpty()) WarpSystem.getInstance().getDataHandler().send(new RequestGlobalWarpNamesPacket());
    }

    @Override
    public void onDisconnect() {
        this.commandExecutorList.forEach(c -> c.unregister(WarpSystem.getInstance()));
        this.commandExecutorList.clear();
    }

    public static GlobalWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
    }

    public boolean isGlobalWarpsOfGUI() {
        return globalWarpsOfGUI;
    }
}
