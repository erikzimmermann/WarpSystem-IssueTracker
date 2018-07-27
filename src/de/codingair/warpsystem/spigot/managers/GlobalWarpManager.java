package de.codingair.warpsystem.spigot.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class GlobalWarpManager {
    private List<String> globalWarps = new ArrayList<>();

    public void loadAllGlobalWarps() {
        this.getGlobalWarps().clear();
        WarpSystem.getInstance().getDataHandler().send(new RequestGlobalWarpNamesPacket());
        System.out.println("Sent request packet!");
    }

    public void create(String warpName, Location loc, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new PublishGlobalWarpPacket(new SGlobalWarp(warpName, new SLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())), callback));
    }

    public void delete(String warpName, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new DeleteGlobalWarpPacket(warpName, callback));
    }

    public List<String> getGlobalWarps() {
        return globalWarps;
    }

    public String getCaseCorrectlyName(String name) {
        for(String warp : this.globalWarps) {
            if(warp.equalsIgnoreCase(name)) return warp;
        }

        return name;
    }
}
