package de.codingair.warpsystem.bungee.features.playerwarps.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.BungeeJSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.playerwarps.listeners.PlayerWarpListener;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerWarpManager implements Manager {
    private HashMap<UUID, List<PlayerWarpData>> warps = new HashMap<>();
    private List<ServerInfo> activeServers = new ArrayList<>();
    private List<String> timeDependent = new ArrayList<>();
    private PlayerWarpListener listener = null;
    private ScheduledTask task = null;

    private long inactiveTime;

    public PlayerWarpManager() {
    }

    public static PlayerWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARPS);
    }

    @Override
    public boolean load(boolean loader) {
        if(!loader) WarpSystem.log("  > Loading PlayerWarps");

        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");
        ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("Config");

        inactiveTime = convertFromTimeFormat(configFile.getConfig().getString("WarpSystem.PlayerWarps.Inactive.Time_After_Expiration"), 86400000L);

        for(List<PlayerWarpData> value : warps.values()) {
            value.clear();
        }
        warps.clear();

        List<Map<?, ?>> l = (List<Map<?, ?>>) file.getConfig().getList("PlayerWarps");
        int size = 0;
        for(Map<?, ?> map : l) {
            BungeeJSON json = new BungeeJSON(map);

            PlayerWarpData p = new PlayerWarpData();
            try {
                p.read(json);
                add(p);
                size++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(listener == null) {
            WarpSystem.getInstance().getDataHandler().register(listener = new PlayerWarpListener());
            BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), listener);
        }

        SendPlayerWarpOptionsPacket packet = new SendPlayerWarpOptionsPacket(inactiveTime);
        interactWithServers(server -> WarpSystem.getInstance().getDataHandler().send(packet, server));

        runScheduler();
        if(!loader) WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");

        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        file.clearConfig();
        JSONArray l = new JSONArray();
        for(List<PlayerWarpData> data : this.warps.values()) {
            for(PlayerWarpData w : data) {
                BungeeJSON json = new BungeeJSON();
                w.write(json);
                l.add(json);
            }
        }

        file.getConfig().set("PlayerWarps", l);

        file.save();
        if(!saver) WarpSystem.log("    ...saved " + l.size() + " PlayerWarp(s)");
    }

    public void runScheduler() {
        if(task != null) return;
        task = BungeeCord.getInstance().getScheduler().schedule(WarpSystem.getInstance(), () -> {
            HashMap<UUID, List<PlayerWarpData>> map = new HashMap<>(warps);

            for(List<PlayerWarpData> value : map.values()) {
                List<PlayerWarpData> warps = new ArrayList<>(value);

                for(PlayerWarpData warp : warps) {
                    if(!timeDependent.contains(warp.getServer())) continue;

                    Date inactive = new Date(warp.getExpireDate() + this.inactiveTime);

                    if(inactive.before(new Date())) {
                        //Delete
                        delete(warp, true);
                    }
                }

                warps.clear();
            }

            map.clear();
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void checkPlayerWarpOwnerNames(ProxiedPlayer player) {
        List<PlayerWarpData> warps = new ArrayList<>(getWarps(player.getUniqueId()));

        for(PlayerWarpData warp : warps) {
            warp.getOwner().setName(player.getName());
        }

        warps.clear();
    }

    private long convertFromTimeFormat(String s, long def) {
        if(s == null || (!s.contains("d") && !s.contains("h") && !s.contains("m") && !s.contains("s"))) return def;

        try {
            return convertFromTimeFormat(s);
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    private long convertFromTimeFormat(String text) throws NumberFormatException {
        long d = 0, h = 0, m = 0, s = 0;

        text = text.trim().toLowerCase();

        if(text.contains("d")) {
            String[] a = text.split("d")[0].split(" ");
            d = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("h")) {
            String[] a = text.split("h")[0].split(" ");
            h = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("m")) {
            String[] a = text.split("m")[0].split(" ");
            m = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("s")) {
            String[] a = text.split("s")[0].split(" ");
            s = Long.parseLong(a[a.length - 1]);
        }

        return TimeUnit.MILLISECONDS.convert(d, TimeUnit.DAYS) + TimeUnit.MILLISECONDS.convert(h, TimeUnit.HOURS) + TimeUnit.MILLISECONDS.convert(m, TimeUnit.MINUTES) + TimeUnit.MILLISECONDS.convert(s, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        if(this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public void add(PlayerWarpData warp) {
        List<PlayerWarpData> warps = getWarps(warp.getOwner().getId());

        warp.setName(getCopiedName(warps, warp.getName()));
        warps.add(warp);

        if(warp.getStarted() == null || warp.getStarted() == 0) {
            warp.setStarted(System.currentTimeMillis());
            warp.born();
        }

        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
    }

    private String getCopiedName(List<PlayerWarpData> list, String name) {
        int num = 0;
        boolean found;

        name = name.replace(" ", "_");

        do {
            found = false;
            if(num == 0) num++;
            else {
                name = name.replaceAll("_\\([0-9]{1,5}?\\)\\z", "");
                name += "_(" + num++ + ")";
            }

            for(PlayerWarpData d : list) {
                String nameWithoutColor = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name));
                String dName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', d.getName())).replace(" ", "_");
                if(dName.equalsIgnoreCase(nameWithoutColor)) {
                    found = true;
                    break;
                }
            }
        } while(found);

        return name;
    }

    public void interactWithServers(ServerInteraction runnable) {
        List<ServerInfo> activeServers = new ArrayList<>(this.activeServers);
        for(ServerInfo activeServer : activeServers) {
            runnable.interact(activeServer);
        }
        activeServers.clear();
    }

    public void interactWithTimeDependentServers(ServerInteraction runnable) {
        List<String> activeServers = new ArrayList<>(this.timeDependent);
        for(String activeServer : activeServers) {
            ServerInfo info = BungeeCord.getInstance().getServerInfo(activeServer);
            if(info != null) runnable.interact(info);
        }
        activeServers.clear();
    }

    public boolean delete(PlayerWarpData warp, boolean informServers) {
        if(warp == null) return false;

        List<PlayerWarpData> warps = getWarps(warp.getOwner().getId());
        boolean result = warps.remove(warp);
        if(warps.isEmpty()) this.warps.remove(warp.getOwner().getId());

        if(informServers) {
            DeletePlayerWarpPacket packet = new DeletePlayerWarpPacket(warp.getName(), warp.getOwner().getId());
            interactWithServers(server -> WarpSystem.getInstance().getDataHandler().send(packet, server));
        }

        return result;
    }

    public List<PlayerWarpData> getWarps(UUID id) {
        List<PlayerWarpData> l = warps.get(id);
        return l == null ? new ArrayList<>() : l;
    }

    public HashMap<UUID, List<PlayerWarpData>> getWarps() {
        return warps;
    }

    public PlayerWarpData getWarp(UUID id, String name) {
        name = ChatColor.stripColor(name).toLowerCase();
        List<PlayerWarpData> l = getWarps(id);
        for(PlayerWarpData w : l) {
            if(ChatColor.stripColor(w.getName()).equalsIgnoreCase(name)) return w;
        }

        return null;
    }

    public void updateWarp(PlayerWarpData warp) {
        PlayerWarpData w = getWarp(warp.getOwner().getId(), warp.getName());

        if(w == null) add(warp);
        else w.apply(warp);
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
        } else {
            setTimeDependent(info, false);
            return this.activeServers.remove(info);
        }
    }

    public boolean setTimeDependent(ServerInfo info, boolean timeDependent) {
        if(timeDependent) {
            if(!this.timeDependent.contains(info.getName())) {
                this.timeDependent.add(info.getName());
                return false;
            } else return true;
        } else return this.timeDependent.remove(info.getName());
    }

    public long getInactiveTime() {
        return inactiveTime;
    }

    public interface ServerInteraction {
        void interact(ServerInfo server);
    }
}
