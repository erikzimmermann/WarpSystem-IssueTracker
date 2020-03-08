package de.codingair.warpsystem.bungee.features.playerwarps.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.BungeeJSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.playerwarps.listeners.PlayerWarpListener;
import de.codingair.warpsystem.bungee.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

public class PlayerWarpManager implements Manager {
    private HashMap<UUID, List<PlayerWarp>> warps = new HashMap<>();
    private List<ServerInfo> activeServers = new ArrayList<>();

    public PlayerWarpManager() {
    }

    @Override
    public boolean load() {
        WarpSystem.log("  > Loading PlayerWarps");

        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        for(List<PlayerWarp> value : warps.values()) {
            value.clear();
        }
        warps.clear();

        List l = file.getConfig().getList("PlayerWarps");
        int size = 0;

        for(Object o : l) {
            BungeeJSON json = new BungeeJSON((Map<?, ?>) o);

            PlayerWarp p = new PlayerWarp();
            try {
                p.read(json);
                add(p);
                size++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        WarpSystem.getInstance().getDataHandler().register(new PlayerWarpListener());

        WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");

        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        file.clearConfig();
        JSONArray l = new JSONArray();
        for(List<PlayerWarp> data : this.warps.values()) {
            for(PlayerWarp w : data) {
                BungeeJSON json = new BungeeJSON();
                w.write(json);
                l.add(json);
            }
        }

        file.getConfig().set("PlayerWarps", l);

        file.save();
        if(!saver) WarpSystem.log("    ...saved " + l.size() + " PlayerWarp(s)");
    }

    @Override
    public void destroy() {

    }

    public void add(PlayerWarp warp) {
        List<PlayerWarp> warps = getWarps(warp.getOwner().getId());
        warps.add(warp);

        if(warp.getStarted() == 0) {
            warp.setStarted(System.currentTimeMillis());
            warp.born();
        }

        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
    }

    public boolean delete(PlayerWarp warp) {
        List<PlayerWarp> warps = getWarps(warp.getOwner().getId());
        boolean result = warps.remove(warp);
        if(warps.isEmpty()) this.warps.remove(warp.getOwner().getId());

        return result;
    }

    public List<PlayerWarp> getWarps(UUID id) {
        List<PlayerWarp> l = warps.get(id);
        return l == null ? new ArrayList<>() : l;
    }

    public HashMap<UUID, List<PlayerWarp>> getWarps() {
        return warps;
    }

    public PlayerWarp getWarp(UUID id, String name) {
        name = ChatColor.stripColor(name).toLowerCase();
        List<PlayerWarp> l = getWarps(id);
        for(PlayerWarp w : l) {
            if(ChatColor.stripColor(w.getName()).equalsIgnoreCase(name)) return w;
        }

        return null;
    }

    public void updateWarp(PlayerWarp warp) {
        PlayerWarp w = getWarp(warp.getOwner().getId(), warp.getName());

        if(w == null) add(warp);
        else w.apply(warp);
    }

    public static PlayerWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARPS);
    }

    public List<ServerInfo> getActiveServers() {
        return activeServers;
    }

    public boolean isActive(ServerInfo info) {
        return this.activeServers.contains(info);
    }

    public boolean setActive(ServerInfo info, boolean active) {
        if(active) {
            if(!this.activeServers.contains(info)) {
                this.activeServers.add(info);
                return false;
            } else return true;
        } else return this.activeServers.remove(info);
    }
}
