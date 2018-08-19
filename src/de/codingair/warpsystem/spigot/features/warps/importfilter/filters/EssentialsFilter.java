package de.codingair.warpsystem.spigot.features.warps.importfilter.filters;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Filter;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Result;
import de.codingair.warpsystem.spigot.features.warps.importfilter.WarpData;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class EssentialsFilter implements Filter {

    @Override
    public Result importData() {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            File target = new File(WarpSystem.getInstance().getDataFolder().getParent() + "/Essentials/warps/");
            if(!target.exists()) return Result.MISSING_FILE;

            Result result = Result.DONE;

            for(File w : target.listFiles()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(w);

                String name = config.getString("name");
                String world = config.getString("world");
                double x = config.getDouble("x");
                double y = config.getDouble("y");
                double z = config.getDouble("z");
                float yaw = (float) config.getDouble("yaw");
                float pitch = (float) config.getDouble("pitch");

                WarpData warpData = new WarpData(name, null, "essentials.warps." + name, world, x, y, z, yaw, pitch);
                if(manager.existsWarp(warpData.getName(), null) && result != Result.ERROR) result = Result.UNAVAILABLE_NAME;
                else if(!manager.importWarpData(warpData)) result = Result.ERROR;
            }

            return result;
        } catch(Exception ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }
    }
}
