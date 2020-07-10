package de.codingair.warpsystem.bungee.features.randomtp;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RandomTPManager implements Manager {
    private HashMap<String, List<String>> worlds = new HashMap<>();
    private ConfigFile file;

    public static RandomTPManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TP);
    }

    @Override
    public boolean load(boolean loader) {
        if(!loader) WarpSystem.log("  > Loading RandomTPManager");
        destroy();
        WarpSystem.getInstance().getFileManager().loadFile("RTP_Queue", "/");

        WarpSystem.getInstance().getFileManager().loadFile("Worlds", "/");
        file = WarpSystem.getInstance().getFileManager().getFile("Worlds");

        int size = 0;
        for(String key : file.getConfig().getKeys()) {
            List<String> data = file.getConfig().getStringList(key);
            size += data.size();
            addWorldData(key, data);
        }
        if(!loader) WarpSystem.log("    ...got " + size + " registered random tp world(s)");

        RandomTPListener l = new RandomTPListener();
        WarpSystem.getInstance().getDataHandler().register(l);
        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), l);
        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving RandomTPManager");
        file.clearConfig();
        int size = 0;
        for(String s : worlds.keySet()) {
            List<String> worlds = this.worlds.get(s);
            file.getConfig().set(s, worlds);
            size += worlds.size();
        }
        file.save();
        if(!saver) WarpSystem.log("    ...saved " + size + " registered random tp world(s)");
    }

    @Override
    public void destroy() {
        this.worlds.values().forEach(List::clear);
        this.worlds.clear();
    }

    public void addWorldData(String server, List<String> worlds) {
        if(worlds == null || worlds.isEmpty()) this.worlds.remove(server);
        else this.worlds.put(server, worlds);
    }

    public List<String> getWorlds(String server) {
        return this.worlds.getOrDefault(server, new ArrayList<>());
    }

    public boolean hasRegisteredServers() {
        return !this.worlds.isEmpty();
    }

    public List<String> getServer() {
        List<String> servers = new ArrayList<>();
        for(String s : this.worlds.keySet()) {
            ServerInfo info = BungeeCord.getInstance().getServerInfo(s);
            if(info != null && WarpSystem.getInstance().getServerManager().isOnline(info)) servers.add(s);
        }
        return servers;
    }
}
