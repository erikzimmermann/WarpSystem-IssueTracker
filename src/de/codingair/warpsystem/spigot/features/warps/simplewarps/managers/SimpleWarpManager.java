package de.codingair.warpsystem.spigot.features.warps.simplewarps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.commands.CDeleteWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.commands.CEditWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.commands.CSetWarp;
import de.codingair.warpsystem.utils.Manager;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleWarpManager implements Manager {
    private HashMap<String, SimpleWarp> warps = new HashMap<>();
    private List<String> reservedNames = new ArrayList<>();
    private List<CommandBuilder> commands = new ArrayList<>();
    private ConfigFile file;

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("SimpleWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("SimpleWarps", "/Memory/");
        this.file = WarpSystem.getInstance().getFileManager().getFile("SimpleWarps");
        boolean errors = false;

        WarpSystem.log("  > Loading SimpleWarps");

        List<String> l = file.getConfig().getStringList("Warps");

        if(l != null && !l.isEmpty()) {
            for(String w : l) {
                try {
                    SimpleWarp warp = new SimpleWarp(w);
                    warps.put(warp.getName().toLowerCase(), warp);
                } catch(ParseException e) {
                    e.printStackTrace();
                    errors = true;
                }
            }
        }

        WarpSystem.log("    ...got " + warps.size() + " SimpleWarp(s)");

        this.commands.add(new CSetWarp());
        this.commands.add(new CEditWarp());
        this.commands.add(new CDeleteWarp());
        this.commands.forEach(c -> c.register(WarpSystem.getInstance()));

        return !errors;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving SimpleWarps");
        List<String> finalData = new ArrayList<>();

        for(SimpleWarp warp : this.warps.values()) {
            finalData.add(warp.toString());
        }

        file.getConfig().set("Warps", finalData);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + finalData.size() + " SimpleWarp(s)");
    }

    @Override
    public void destroy() {
        this.commands.forEach(c -> c.unregister(WarpSystem.getInstance()));
        this.commands.clear();
        this.warps.clear();
    }

    public void addWarp(SimpleWarp warp) {
        if(existsWarp(warp.getName())) return;
        this.warps.put(warp.getName().toLowerCase(), warp);
    }

    public void removeWarp(SimpleWarp warp) {
        this.warps.remove(warp.getName().toLowerCase());
    }

    public SimpleWarp getWarp(String warp) {
        return this.warps.get(warp.toLowerCase());
    }

    public HashMap<String, SimpleWarp> getWarps() {
        return warps;
    }

    public boolean existsWarp(String warp) {
        return this.warps.containsKey(warp.toLowerCase());
    }

    public boolean isReserved(String name) {
        for(String n : this.reservedNames) {
            if(n.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    public boolean reserveName(String name) {
        if(isReserved(name)) return false;
        this.reservedNames.add(name);
        return true;
    }

    public boolean commitNewName(SimpleWarp warp, String name) {
        if(existsWarp(name) || warp.getName().equals(name)) return false;
        this.reservedNames.remove(name);
        this.warps.remove(warp.getName().toLowerCase());
        warp.setName(name);
        this.warps.put(warp.getName().toLowerCase(), warp);
        return true;
    }

    public static SimpleWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
    }
}
