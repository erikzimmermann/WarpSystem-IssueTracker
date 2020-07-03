package de.codingair.warpsystem.spigot.features.signs.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.listeners.SignListener;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AvailableForSetupAssistant(type = "WarpSigns", config = "Config")
@Function(name = "Enabled", defaultValue = "true", config = "Config", configPath = "WarpSystem.Functions.WarpSigns", clazz = Boolean.class)
@Function(name = "Teleport message", defaultValue = "true", config = "Config", configPath = "WarpSystem.Send.Teleport_Message.WarpSigns", clazz = Boolean.class)
public class SignManager implements Manager {
    private final List<WarpSign> warpSigns = new ArrayList<>();

    public static SignManager getInstance() {
        return ((SignManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS));
    }

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

                this.warpSigns.add(warpSign);
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
            if(warpSign.getLocation().getWorld() == null) continue;
            if(warpSign.getLocation().getBlock().getLocation().equals(location.getBlock().getLocation())) return warpSign;
        }

        return null;
    }

    public List<WarpSign> getWarpSigns() {
        return warpSigns;
    }
}
