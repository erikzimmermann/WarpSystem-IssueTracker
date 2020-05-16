package de.codingair.warpsystem.spigot.features.spawn.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.ConfigWriter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.spawn.commands.CSetSpawn;
import de.codingair.warpsystem.spigot.features.spawn.commands.CSpawn;
import de.codingair.warpsystem.spigot.features.spawn.listeners.SpawnListener;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import de.codingair.warpsystem.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class SpawnManager implements Manager {
    private String spawnServer = null, respawnServer = null;
    private Spawn spawn;

    public static SpawnManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SPAWN);
    }

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");

        spawn = new Spawn();
        if(file.getConfig().contains("Spawn")) {
            ConfigWriter reader = new ConfigWriter(file);
            reader.getSerializable("Spawn", this.spawn);
        }

        SpawnListener listener = new SpawnListener();
        Bukkit.getPluginManager().registerEvents(listener, WarpSystem.getInstance());
        WarpSystem.getInstance().getDataHandler().register(listener);

        new CSetSpawn().register(WarpSystem.getInstance());
        new CSpawn().register(WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        ConfigWriter writer = new ConfigWriter(file);
        writer.put("Spawn", this.spawn);
        file.saveConfig();
    }

    @Override
    public void destroy() {
        this.spawn.destroy();
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void updateSpawn(Location location) {
        if(this.spawn == null) this.spawn = new Spawn();
        this.spawn.addAction(new WarpAction(new Destination(new LocationAdapter(location))));
    }

    public String getSpawnServer() {
        return spawnServer;
    }

    public void updateGlobalOptions(String spawn, String respawn) {
        if(!Objects.equals(this.spawnServer, spawn) || !Objects.equals(this.respawnServer, respawn)) {
            this.spawnServer = spawn;
            this.respawnServer = respawn;

            WarpSystem.getInstance().getDataHandler().send(new SendGlobalSpawnOptionsPacket(spawn, respawn));
        }
    }

    public void applyGlobalOptions(String spawn, String respawn) {
        String s = WarpSystem.getInstance().getCurrentServer();

        if(this.spawn != null && this.spawn.getUsage().isBungee() && !Objects.equals(s, spawn)) this.spawn.setUsage(this.spawn.getUsage().getLocal());
        if(this.spawn != null && this.spawn.getRespawnUsage().isBungee() && !Objects.equals(s, respawn)) this.spawn.setRespawnUsage(this.spawn.getRespawnUsage().getLocal());

        this.spawnServer = spawn;
        this.respawnServer = respawn;
    }

    public String getRespawnServer() {
        return respawnServer;
    }
}
