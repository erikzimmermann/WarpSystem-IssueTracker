package de.codingair.warpsystem.spigot.features.portals.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.commands.CPortal;
import de.codingair.warpsystem.spigot.features.portals.listeners.PortalListener;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PortalManager implements Manager {
    private List<Portal> portals = new ArrayList<>();

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        boolean success = true;

        this.portals.forEach(Portal::destroy);
        this.portals.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        WarpSystem.log("  > Loading Portals (from Teleporters)");
        for(String s : file.getConfig().getStringList("Teleporters")) {
            this.portals.add(Portal.getByJSONString(s));
        }

        WarpSystem.log("  > Loading Portals (from Portals)");
        for(String s : file.getConfig().getStringList("Portals")) {
            this.portals.add(Portal.getByJSONString(s));
        }

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

        WarpSystem.log("    > Verify that worlds are available");
        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null || portal.getDestination().getWorld() == null) {
                portal.setDisabled(true);
                success = false;
            }
        }

        WarpSystem.log("    > Verify that portals are enabled");
        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
            this.portals.forEach(p -> p.setRunning(true));
        }

        //Remove old portals
        file.getConfig().set("Teleporters", null);
        file.saveConfig();

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
            CPortal cPortal = new CPortal();
            WarpSystem.getInstance().getCommands().add(cPortal);
            cPortal.register(WarpSystem.getInstance());
        }


        Bukkit.getPluginManager().registerEvents(new PortalListener(), WarpSystem.getInstance());

        return success;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        if(!saver) WarpSystem.log("  > Saving Portals");
        List<String> data = new ArrayList<>();

        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null || portal.getDestination().getWorld() == null) continue;
            data.add(portal.toJSONString());
        }

        file.getConfig().set("Portals", data);

        file.saveConfig();
    }

    public List<Portal> getPortals() {
        return portals;
    }
}
