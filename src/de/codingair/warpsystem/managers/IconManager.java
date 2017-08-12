package de.codingair.warpsystem.managers;

import de.CodingAir.v1_6.CodingAPI.Files.ConfigFile;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.ActionIcon;
import de.codingair.warpsystem.gui.affiliations.ActionIconHelper;
import de.codingair.warpsystem.gui.affiliations.Category;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class IconManager {
    private List<Warp> warps = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public void load(boolean sync) {
        if (!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    load(true);
                }
            });
        } else {
            //Load
            this.warps = new ArrayList<>();

            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> warps = config.getStringList("Warps");
            for (String s : warps) {
                Warp warp = ActionIconHelper.fromString(s);

                if (warp != null) this.warps.add(warp);
            }

            List<String> categories = config.getStringList("Categories");
            for (String s : categories) {
                Category category = ActionIconHelper.fromString(s);

                if (category != null) this.categories.add(category);
            }
        }
    }

    public void save(boolean sync) {
        if (!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    save(true);
                }
            });
        } else {
            //Save
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> warps = new ArrayList<>();
            for (Warp warp : this.warps) {
                warps.add(ActionIconHelper.toString(warp));
            }

            List<String> categories = new ArrayList<>();
            for (Category category : this.categories) {
                categories.add(ActionIconHelper.toString(category));
            }

            config.set("Warps", warps);
            config.set("Categories", categories);
            file.saveConfig();
        }
    }

    public boolean existsWarp(String name, Category category) {
        return getWarp(name, category) != null;
    }

    public Warp getWarp(String name, Category category) {
        for (Warp warp : getWarps(category)) {
            if (warp.getName().equalsIgnoreCase(name)) return warp;
        }

        return null;
    }

    public boolean existsCategory(String name) {
        return getCategory(name) != null;
    }

    public Category getCategory(String name) {
        for (Category c : this.categories) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }

        return null;
    }

    public List<Warp> getWarps() {
        return warps;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Warp> getWarps(Category category) {
        List<Warp> icons = new ArrayList<>();

        for (Warp icon : this.warps) {
            if (icon.getCategory() == category) icons.add(icon);
        }

        return icons;
    }

    public void remove(ActionIcon icon) {
        if(icon instanceof Category) this.categories.remove(icon);
        else if(icon instanceof Warp) this.warps.remove(icon);
    }
}
