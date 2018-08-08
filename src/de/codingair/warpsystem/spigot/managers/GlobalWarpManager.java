package de.codingair.warpsystem.spigot.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalWarpManager {
    private HashMap<String, String> globalWarps = new HashMap<>();

    public void loadAllGlobalWarps() {
        this.getGlobalWarps().clear();
        WarpSystem.getInstance().getDataHandler().send(new RequestGlobalWarpNamesPacket());
    }

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

    public void teleport(Player player, String display, String name, Callback<PrepareTeleportPacket.Result> callback) {
        if(name == null) return;
        name = getCaseCorrectlyName(name);
        if(!this.globalWarps.containsKey(name)) return;

        WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPacket(player.getName(), name, display, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
                callback.accept(PrepareTeleportPacket.Result.getById(object));
            }
        }));
    }
}
