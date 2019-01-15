package de.codingair.warpsystem.bungee.features.globalwarps.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.globalwarps.listeners.GlobalWarpListener;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalWarpManager implements Manager {
    private List<SGlobalWarp> globalWarps = new ArrayList<>();
    private GlobalWarpListener listener;

    public boolean load() {
        WarpSystem.getInstance().getFileManager().loadFile("GlobalWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps");
        Configuration config = file.getConfig();

        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), listener = new GlobalWarpListener());

        WarpSystem.log("  > Loading locations of GlobalWarps");

        this.globalWarps = new ArrayList<>();

        for(String data : config.getKeys()) {
            SGlobalWarp warp = new SGlobalWarp();

            warp.setName(data);
            warp.setServer(config.getString(data + ".Server"));
            warp.setLoc(new SLocation(
                    config.getString(data + ".Location.World"),
                    config.getDouble(data + ".Location.X"),
                    config.getDouble(data + ".Location.Y"),
                    config.getDouble(data + ".Location.Z"),
                    config.getFloat(data + ".Location.Yaw"),
                    config.getFloat(data + ".Location.Pitch")
            ));

            this.globalWarps.add(warp);
        }

        WarpSystem.getInstance().getDataHandler().register(listener);

        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving locations of GlobalWarps");

        for(SGlobalWarp globalWarp : this.globalWarps) {
            save(globalWarp);
        }
    }

    @Override
    public void destroy() {
        this.globalWarps.clear();
    }

    private void save(SGlobalWarp warp) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps");
        Configuration config = file.getConfig();

        config.set(warp.getName() + ".Server", warp.getServer());
        config.set(warp.getName() + ".Location.World", warp.getLoc().getWorld());
        config.set(warp.getName() + ".Location.X", warp.getLoc().getX());
        config.set(warp.getName() + ".Location.Y", warp.getLoc().getY());
        config.set(warp.getName() + ".Location.Z", warp.getLoc().getZ());
        config.set(warp.getName() + ".Location.Yaw", warp.getLoc().getYaw());
        config.set(warp.getName() + ".Location.Pitch", warp.getLoc().getPitch());

        file.save();
    }

    public void synchronize(SGlobalWarp warp) {
        for(ServerInfo server : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
            WarpSystem.getInstance().getDataHandler().send(new UpdateGlobalWarpPacket(get(warp.getName()) == null ? UpdateGlobalWarpPacket.Action.DELETE.getId() : UpdateGlobalWarpPacket.Action.ADD.getId(), warp.getName(), warp.getServer()), server);
        }
    }

    public void synchronize(ServerInfo info) {
        if(this.globalWarps.isEmpty()) {
            return;
        }

        List<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> current = new HashMap<>();
        int currentBytes = 0;

        for(SGlobalWarp warp : this.globalWarps) {
            currentBytes += warp.getName().length() + warp.getServer().length();

            if(currentBytes > 32700) {
                list.add(current);
                current = new HashMap<>();
            }

            currentBytes = warp.getName().length() + warp.getServer().length();
            current.put(warp.getName(), warp.getServer());
        }

        if(current.size() > 0) list.add(current);

        boolean start = true;
        for(HashMap<String, String> l : list) {
            WarpSystem.getInstance().getDataHandler().send(new SendGlobalWarpNamesPacket(l, start), info);
            start = false;
        }

        list.clear();
    }

    private void delete(SGlobalWarp warp) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps");
        Configuration config = file.getConfig();

        config.set(warp.getName(), null);

        file.save();
    }

    public List<SGlobalWarp> getGlobalWarps() {
        return globalWarps;
    }

    public SGlobalWarp get(String name) {
        for(SGlobalWarp warp : this.globalWarps) {
            if(warp.getName().equalsIgnoreCase(name)) return warp;
        }

        return null;
    }

    public boolean add(SGlobalWarp warp) {
        if(get(warp.getName()) != null) return false;
        this.globalWarps.add(warp);
        save(warp);
        return true;
    }

    public SGlobalWarp remove(String name) {
        SGlobalWarp warp = get(name);
        if(warp == null) return null;
        this.globalWarps.remove(warp);
        delete(warp);
        return warp;
    }
}
