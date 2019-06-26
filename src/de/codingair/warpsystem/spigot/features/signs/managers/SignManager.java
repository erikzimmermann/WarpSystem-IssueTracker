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
import de.codingair.codingapi.tools.JSON.JSONObject;
import de.codingair.codingapi.tools.JSON.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class SignManager implements Manager {
    private List<WarpSign> warpSigns = new ArrayList<>();

    @Override
    public boolean load() {
        boolean success = true;
        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        this.warpSigns.clear();

        WarpSystem.log("  > Loading WarpSigns");
        List<String> data = file.getConfig().getStringList("WarpSigns");
        if(data != null) {
            for(String s : data) {
                WarpSign warpSign = new WarpSign();
                try {
                    warpSign.read((JSONObject) new JSONParser().parse(s));
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(warpSign != null) {
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
                } else {
                    WarpSystem.log("    > Could not load WarpSign! (Skip)");
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

        List<String> data = new ArrayList<>();
        for(WarpSign s : this.warpSigns) {
            JSONObject json = new JSONObject();
            s.write(json);
            data.add(json.toJSONString());
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
