package de.codingair.warpsystem.bungee.features.spawn.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.spawn.listeners.ServerListener;
import de.codingair.warpsystem.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Objects;

public class SpawnManager implements Manager {
    private String spawn, respawn;

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        this.spawn = file.getConfig().getString("WarpSystem.GlobalSpawnOptions.Spawn", null);
        this.respawn = file.getConfig().getString("WarpSystem.GlobalSpawnOptions.Respawn", null);

        ServerListener listener = new ServerListener();
        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), listener);
        WarpSystem.getInstance().getDataHandler().register(listener);
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        file.getConfig().set("WarpSystem.GlobalSpawnOptions.Spawn", this.spawn);
        file.getConfig().set("WarpSystem.GlobalSpawnOptions.Respawn", this.respawn);

        file.save();
    }

    @Override
    public void destroy() {
    }

    public void update(ServerInfo sender, String spawn, String respawn) {
        if(!Objects.equals(this.spawn, spawn) || !Objects.equals(this.respawn, respawn)) {
            this.spawn = spawn;
            this.respawn = respawn;
            synchronize(sender);
        }
    }

    public void synchronize(ServerInfo except) {
        for(ServerInfo serverInfo : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
            if(serverInfo.equals(except)) continue;
            WarpSystem.getInstance().getDataHandler().send(getInfoPacket(), serverInfo);
        }
    }

    public static SpawnManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SPAWN);
    }

    public SendGlobalSpawnOptionsPacket getInfoPacket() {
        return new SendGlobalSpawnOptionsPacket(this.spawn, this.respawn);
    }

    public String getSpawn() {
        return spawn;
    }

    public String getRespawn() {
        return respawn;
    }
}
