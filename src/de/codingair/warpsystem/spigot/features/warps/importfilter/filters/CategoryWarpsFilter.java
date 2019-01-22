package de.codingair.warpsystem.spigot.features.warps.importfilter.filters;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.importfilter.CategoryData;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Filter;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Result;
import de.codingair.warpsystem.spigot.features.warps.importfilter.WarpData;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryWarpsFilter implements Filter {
    @Override
    public Result importData() {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            File target = new File(WarpSystem.getInstance().getDataFolder().getParent() + "/CategoryWarps/Data.yml");

            if(!target.exists()) return Result.MISSING_FILE;

            FileConfiguration config = YamlConfiguration.loadConfiguration(target);

            HashMap<String, List<String>> categories = new HashMap<>();

            for(String s : config.getKeys(true)) {
                String[] a = s.split("\\.");
                if(a.length != 2) continue;

                String c = a[1];

                for(String s_ : config.getKeys(true)) {
                    if(!s_.startsWith("Categories." + c + ".")) continue;

                    String[] a_ = s_.split("\\.");
                    if(a_.length != 3) continue;

                    String warp = a_[2];

                    if(!categories.containsKey(c)) categories.put(c, new ArrayList<>());

                    categories.get(c).add(warp);
                }
            }

            Result result = Result.DONE;

            for(String s : categories.keySet()) {
                CategoryData cd = new CategoryData(s, "CategoryWarps." + s);

                for(String s1 : categories.get(s)) {
                    String path = "Categories." + s + "." + s1;

                    String world = config.getString(path + ".world", null);
                    double x = config.getDouble(path + ".X", 0);
                    double y = config.getDouble(path + ".Y", 0);
                    double z = config.getDouble(path + ".Z", 0);
                    float yaw = (float) config.getDouble(path + ".Yaw");
                    float pitch = (float) config.getDouble(path + ".Pitch");

                    if(world == null) continue;

                    WarpData wd = new WarpData(s1, s, "CategoryWarps." + s + "." + s1, world, x, y, z, yaw, pitch);
                    cd.getWarps().add(wd);
                }

                if(manager.existsCategory(cd.getName()) && result != Result.ERROR) result = Result.UNAVAILABLE_NAME;
                else if(!manager.importCategoryData(cd)) result = Result.ERROR;
            }

            return result;
        } catch(Exception ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }
    }

    @Override
    public List<String> loadWarpNames() {
        throw new IllegalStateException("Single-Import-Feature not available for CategoryWarps!");
    }

    @Override
    public SimpleWarp loadWarp(String link) {
        throw new IllegalStateException("Single-Import-Feature not available for CategoryWarps!");
    }
}
