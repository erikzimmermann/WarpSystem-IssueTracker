package de.codingair.warpsystem.spigot.features.effectportals.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.listeners.PortalListener;
import de.codingair.warpsystem.spigot.features.effectportals.utils.Portal;
import de.codingair.warpsystem.spigot.features.effectportals.commands.CPortal;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PortalManager implements Manager {
    private static PortalManager instance;
    private List<Portal> portals = new ArrayList<>();
    private double maxParticleDistance = 70D;

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        boolean success = true;

        this.portals.forEach(Portal::destroy);
        this.portals.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        if(!file.getConfig().getStringList("Teleporters").isEmpty()) {
            WarpSystem.log("  > Loading Portals (from Teleporters)");
            for(String s : file.getConfig().getStringList("Teleporters")) {
                this.portals.add(Portal.getByJSONString(s));
            }

            WarpSystem.log("     ...got " + this.portals.size() + " Portal(s)");
        }

        int temp = this.portals.size();

        WarpSystem.log("  > Loading Portals (from Portals)");
        for(String s : file.getConfig().getStringList("Portals")) {
            this.portals.add(Portal.getByJSONString(s));
        }

        WarpSystem.log("     ...got " + (portals.size() - temp) + " Portal(s)");

        //Check duplicates
        List<Portal> duplicates = new ArrayList<>();
        for(Portal p0 : this.portals) {
            if(duplicates.contains(p0)) continue;

            for(Portal p1 : this.portals) {
                if(duplicates.contains(p1) || p0.equals(p1)) continue;

                if(p0.getStart().equals(p1.getStart()) && p0.getDestination().equals(p1.getDestination())) {
                    if(!duplicates.contains(p1)) duplicates.add(p1);
                }
            }
        }

        if(!duplicates.isEmpty()) {
            WarpSystem.log("    > " + duplicates.size() + " duplicated Portal(s) - Removing...");
            this.portals.removeAll(duplicates);
            duplicates.clear();
        }

//        WarpSystem.log("    > Verify that worlds are available");
        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null) {
                portal.setDisabled(true);
                success = false;
            }
        }

//        WarpSystem.log("    > Verify that portals are enabled");
        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
            this.portals.forEach(p -> p.setRunning(true));
        }

        //Remove old portals
        file.getConfig().set("Teleporters", null);
        file.saveConfig();

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
            new CPortal().register(WarpSystem.getInstance());
        }

        this.maxParticleDistance = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getDouble("WarpSystem.EffectPortals.ParticleDistance", 100.0);

        Bukkit.getPluginManager().registerEvents(new PortalListener(), WarpSystem.getInstance());

        return success;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        if(!saver) WarpSystem.log("  > Saving Portals");
        List<String> data = new ArrayList<>();

        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null) continue;
            data.add(portal.toJSONString());
        }

        file.getConfig().set("Portals", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " Portal(s)");
    }

    @Override
    public void destroy() {
        this.portals.clear();
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public double getMaxParticleDistance() {
        return maxParticleDistance;
    }

    public static PortalManager getInstance() {
        if(instance == null) instance = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        if(instance == null) instance = new PortalManager();
        return instance;
    }
}
