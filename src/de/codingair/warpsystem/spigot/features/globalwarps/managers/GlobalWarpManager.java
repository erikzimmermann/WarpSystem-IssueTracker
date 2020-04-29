package de.codingair.warpsystem.spigot.features.globalwarps.managers;

import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarps;
import de.codingair.warpsystem.spigot.features.globalwarps.listeners.GlobalWarpListener;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.GlobalWarpTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalWarpManager implements Manager, BungeeFeature {
    //              Name,   Server
    private HashMap<String, String> globalWarps = new HashMap<>();
    private GlobalWarpListener listener;
    private List<CommandBuilder> commandExecutorList = new ArrayList<>();

    public static GlobalWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
    }

    public void create(String warpName, Location loc, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new PublishGlobalWarpPacket(new SGlobalWarp(warpName, new SLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())), callback));
    }

    public void updatePosition(String warpName, Location loc, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new PublishGlobalWarpPacket(new SGlobalWarp(warpName, new SLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())), true, callback));
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

    public void teleport(Player player, String id, Vector randomOffset, String displayName, String message, double costs, Callback<GlobalWarpTeleportPacket.Result> callback) {
        if(id == null) {
            callback.accept(GlobalWarpTeleportPacket.Result.WARP_NOT_EXISTS);
            return;
        }

        id = getCaseCorrectlyName(id);
        if(!this.globalWarps.containsKey(id)) {
            callback.accept(GlobalWarpTeleportPacket.Result.WARP_NOT_EXISTS);
            return;
        }

        double x = 0, y = 0, z = 0;
        if(randomOffset != null) {
            x = randomOffset.getX();
            y = randomOffset.getY();
            z = randomOffset.getZ();
        }

        WarpSystem.getInstance().getDataHandler().send(new GlobalWarpTeleportPacket(player.getName(), id, x, y, z, displayName, message, costs, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
                callback.accept(GlobalWarpTeleportPacket.Result.getById(object));
            }
        }));
    }

    @Override
    public boolean load(boolean loader) {
        this.globalWarps.clear();
        this.commandExecutorList.clear();

        WarpSystem.getInstance().getBungeeFeatureList().add(this);

        WarpSystem.getInstance().getDataHandler().register(listener = new GlobalWarpListener());
        Bukkit.getPluginManager().registerEvents(listener, WarpSystem.getInstance());

        new CGlobalWarp().register(WarpSystem.getInstance());
        new CGlobalWarps().register(WarpSystem.getInstance());
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
        if(getGlobalWarps().isEmpty()) WarpSystem.getInstance().getDataHandler().send(new RequestGlobalWarpNamesPacket());
    }

    @Override
    public void onDisconnect() {
    }
}
