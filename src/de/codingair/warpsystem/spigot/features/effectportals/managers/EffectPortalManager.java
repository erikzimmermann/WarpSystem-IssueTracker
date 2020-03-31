package de.codingair.warpsystem.spigot.features.effectportals.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.commands.CPortal;
import de.codingair.warpsystem.spigot.features.effectportals.listeners.PortalListener;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectPortalManager implements Manager {
    private static EffectPortalManager instance;
    private List<EffectPortal> effectPortals = new ArrayList<>();
    private double maxParticleDistance = 70D;

    @Override
    public boolean load(boolean loader) {
        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        boolean success = true;

        this.effectPortals.forEach(EffectPortal::destroy);
        this.effectPortals.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        int temp = this.effectPortals.size();

        WarpSystem.log("  > Loading Portals (from Portals)");
        List<?> l = file.getConfig().getList("Portals");
        if(l != null)
            for(Object s : l) {
                EffectPortal effectPortal = new EffectPortal();

                if(s instanceof Map) {
                    try {
                        JSON json = new JSON((Map<?, ?>) s);
                        effectPortal.read(json);
                        this.effectPortals.add(effectPortal);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                } else if(s instanceof String) {
                    try {
                        effectPortal.read((JSON) new JSONParser().parse((String) s));
                        this.effectPortals.add(effectPortal);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            }

        WarpSystem.log("    ...got " + (effectPortals.size() - temp) + " EffectPortal(s)");

        //Check duplicates
        List<EffectPortal> duplicates = new ArrayList<>();
        for(EffectPortal p0 : this.effectPortals) {
            if(duplicates.contains(p0)) continue;

            for(EffectPortal p1 : this.effectPortals) {
                if(duplicates.contains(p1) || p0.equals(p1)) continue;

                if(p0.getLocation().equals(p1.getLocation()) && p0.getDestination().equals(p1.getDestination())) {
                    if(!duplicates.contains(p1)) duplicates.add(p1);
                }
            }
        }

        if(!duplicates.isEmpty()) {
            WarpSystem.log("    > " + duplicates.size() + " duplicated EffectPortal(s) - Removing...");
            this.effectPortals.removeAll(duplicates);
            duplicates.clear();
        }

//        WarpSystem.log("    > Verify that worlds are available");
        for(EffectPortal effectPortal : this.effectPortals) {
            if(effectPortal.getLocation().getWorld() == null) {
                effectPortal.setDisabled(true);
                success = false;
            }
        }

        this.effectPortals.forEach(EffectPortal::initializeLink);
        this.effectPortals.forEach(p -> p.setRunning(true));

        for(EffectPortal effectPortal : this.effectPortals) {
            if(effectPortal.hasDestinationPortal() && effectPortal.getLink() == null) {
                effectPortal.removeAction(Action.WARP);
            }
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

    public EffectPortal getPortal(Location loc) {
        for(EffectPortal p : this.effectPortals) {
            if(p.getLocation().equals(loc)) return p;
        }

        return null;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        if(!saver) WarpSystem.log("  > Saving Portals");
        List<JSON> data = new ArrayList<>();

        for(EffectPortal effectPortal : this.effectPortals) {
            if(effectPortal.getLocation().getWorld() == null) continue;
            JSON json = new JSON();
            effectPortal.write(json);
            data.add(json);
        }

        file.getConfig().set("Portals", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " EffectPortal(s)");
    }

    @Override
    public void destroy() {
        this.effectPortals.clear();
    }

    public List<EffectPortal> getEffectPortals() {
        return effectPortals;
    }

    public double getMaxParticleDistance() {
        return maxParticleDistance;
    }

    public static EffectPortalManager getInstance() {
        if(instance == null) instance = WarpSystem.getInstance().getDataManager().getManager(FeatureType.EFFECT_PORTALS);
        if(instance == null) instance = new EffectPortalManager();
        return instance;
    }
}
