package de.codingair.warpsystem.spigot.features.signs.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.listeners.SignListener;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignManager implements Manager {
    private List<WarpSign> warpSigns = new ArrayList<>();

    @Override
    public boolean load(boolean loader) {
        boolean success = true;
        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        this.warpSigns.clear();

        WarpSystem.log("  > Loading WarpSigns");
        List<?> data = file.getConfig().getList("WarpSigns");
        if(data != null) {
            for(Object s : data) {
                WarpSign warpSign = new WarpSign();

                if(s instanceof Map) {
                    try {
                        JSON json = new JSON((Map<?, ?>) s);
                        warpSign.read(json);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                } else if(s instanceof String) {
                    try {
                        warpSign.read((JSON) new JSONParser().parse((String) s));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                if(warpSign.getLocation() != null && warpSign.getLocation().getWorld() != null && warpSign.getLocation().getBlock() != null) {
                    if(warpSign.getLocation().getBlock().getState() instanceof Sign) {
                        this.warpSigns.add(warpSign);
                    } else {
                        WarpSystem.log("    > Loaded WarpSign at location without sign! (Skip)");
                        success = false;
                    }
                } else {
                    WarpSystem.log("    > Loaded WarpSign with missing world! (Skip)");
                    success = false;
                }
            }
        }

        WarpSystem.log("    ...got " + this.warpSigns.size() + " WarpSign(s)");


        Bukkit.getPluginManager().registerEvents(new SignListener(), WarpSystem.getInstance());

        return success;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        if(!saver) WarpSystem.log("  > Saving WarpSigns");

        List<JSON> data = new ArrayList<>();
        for(WarpSign s : this.warpSigns) {
            JSON json = new JSON();
            s.write(json);
            data.add(json);
        }

        file.getConfig().set("WarpSigns", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " WarpSign(s)");
    }

    @Override
    public void destroy() {
        this.warpSigns.clear();
    }

    public WarpSign getByLocation(Location location) {
        for(WarpSign warpSign : this.warpSigns) {
            if(warpSign.getLocation().getBlock().getLocation().equals(location.getBlock().getLocation())) return warpSign;
        }

        return null;
    }

    public List<WarpSign> getWarpSigns() {
        return warpSigns;
    }

    public static SignManager getInstance() {
        return ((SignManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS));
    }
}
