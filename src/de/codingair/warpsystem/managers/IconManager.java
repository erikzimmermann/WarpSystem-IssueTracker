package de.codingair.warpsystem.managers;

import de.CodingAir.v1_6.CodingAPI.Files.ConfigFile;
import de.CodingAir.v1_6.CodingAPI.Serializable.SerializableLocation;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.gui.affiliations.*;
import de.codingair.warpsystem.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IconManager {
    private List<Warp> warps = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public void load(boolean sync) {
        if (!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> load(true));
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

            //Import old
            if(WarpSystem.getInstance().isOld()) {
                WarpSystem.log("Import old icons.");

                WarpSystem.getInstance().getFileManager().loadFile("Categories", "Memory/");
                WarpSystem.getInstance().getFileManager().loadFile("Warps", "Memory/");

                ConfigFile oldFile = WarpSystem.getInstance().getFileManager().getFile("Categories");
                FileConfiguration oldConfig = oldFile.getConfig();

                for(String key : oldConfig.getKeys(false)) {
                    Category category = new Category(key, ImportHelper.getItem(oldConfig.getString(key + ".Item")), oldConfig.getInt(key + ".Slot"), oldConfig.getString(key + ".Permission", null));

                    category.setItem(new ItemBuilder(category.getItem()).setHideStandardLore(true).setAmount(1).setName("§b§n" + category.getName()).setLore(oldConfig.getStringList(key + ".Lore")).getItem());

                    this.categories.add(category);
                }

                oldFile = WarpSystem.getInstance().getFileManager().getFile("Warps");
                oldConfig = oldFile.getConfig();

                for(String key : oldConfig.getKeys(false)) {
                    Warp warp = new Warp(key, ImportHelper.getItem(oldConfig.getString(key + ".Item")), oldConfig.getInt(key + ".Slot"), oldConfig.getString(key + ".Permission", null), getCategory(oldConfig.getString(key + ".Category", null))
                            , new ActionObject(Action.TELEPORT_TO_WARP, new SerializableLocation(ImportHelper.stringToLoc(oldConfig.getString(key + ".Location")))));

                    warp.setItem(new ItemBuilder(warp.getItem()).setHideStandardLore(true).setAmount(1).setName("§b" + warp.getName()).setLore(oldConfig.getStringList(key + ".Lore")).getItem());

                    this.warps.add(warp);
                }

                oldFile = WarpSystem.getInstance().getFileManager().getFile("Categories");
                oldConfig = oldFile.getConfig();

                for(String key : oldConfig.getKeys(false)) {
                    Category category = getCategory(key);

                    for(String name : oldConfig.getStringList(key + ".Warps")) {
                        if(this.existsWarp(name, category)) category.addWarp(getWarp(name, category));
                    }
                }

                WarpSystem.getInstance().getFileManager().getFile("Categories").delete();
                WarpSystem.getInstance().getFileManager().getFile("Warps").delete();
            }
        }
    }

    public void save(boolean sync) {
        if (!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> save(true));
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
        if(name == null) return null;

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
        if(icon instanceof Category) {
            Category category = (Category) icon;
            List<Warp> warps = new ArrayList<>();
            warps.addAll(category.getWarps());

            for(Warp warp : warps) {
                remove(warp);
            }

            this.categories.remove(icon);
        } else if(icon instanceof Warp) {
            Category category = ((Warp) icon).getCategory();
            if(category != null) category.removeWarp((Warp) icon);

            this.warps.remove(icon);
        }
    }
}
