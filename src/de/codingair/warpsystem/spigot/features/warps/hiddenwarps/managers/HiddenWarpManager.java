package de.codingair.warpsystem.spigot.features.warps.hiddenwarps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.HiddenWarp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands.CDeleteHiddenWarp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands.CEditHiddenWarp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands.CSetWarp;
import de.codingair.warpsystem.utils.Manager;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HiddenWarpManager implements Manager {
    private HashMap<String, HiddenWarp> warps = new HashMap<>();
    private List<String> reservedNames = new ArrayList<>();
    private List<CommandBuilder> commands = new ArrayList<>();
    private ConfigFile file;

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("HiddenWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("HiddenWarps", "/Memory/");
        this.file = WarpSystem.getInstance().getFileManager().getFile("HiddenWarps");
        boolean errors = false;

        WarpSystem.log("  > Loading HiddenWarps");

        List<String> l = file.getConfig().getStringList("Warps");

        if(l != null && !l.isEmpty()) {
            for(String w : l) {
                try {
                    HiddenWarp warp = new HiddenWarp(w);
                    warps.put(warp.getName().toLowerCase(), warp);
                } catch(ParseException e) {
                    e.printStackTrace();
                    errors = true;
                }
            }
        }

        WarpSystem.log("    ...got " + warps.size() + " HiddenWarp(s)");

        this.commands.add(new CSetWarp());
        this.commands.add(new CEditHiddenWarp());
        this.commands.add(new CDeleteHiddenWarp());
        this.commands.forEach(c -> c.register(WarpSystem.getInstance()));

        return !errors;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving HiddenWarps");
        List<String> finalData = new ArrayList<>();

        for(HiddenWarp warp : this.warps.values()) {
            finalData.add(warp.toString());
        }

        file.getConfig().set("Warps", finalData);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + finalData.size() + " HiddenWarp(s)");
    }

    @Override
    public void destroy() {
        this.commands.forEach(c -> c.unregister(WarpSystem.getInstance()));
        this.commands.clear();
        this.warps.clear();
    }

    public void addWarp(HiddenWarp warp) {
        if(existsWarp(warp.getName())) return;
        this.warps.put(warp.getName().toLowerCase(), warp);
    }

    public void removeWarp(HiddenWarp warp) {
        this.warps.remove(warp.getName().toLowerCase());
    }

    public HiddenWarp getWarp(String warp) {
        return this.warps.get(warp.toLowerCase());
    }

    public HashMap<String, HiddenWarp> getWarps() {
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

    public boolean commitNewName(HiddenWarp warp, String name) {
        if(existsWarp(name) || warp.getName().equals(name)) return false;
        this.reservedNames.remove(name);
        this.warps.remove(warp.getName().toLowerCase());
        warp.setName(name);
        this.warps.put(warp.getName().toLowerCase(), warp);
        return true;
    }

    public static HiddenWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
    }
}
