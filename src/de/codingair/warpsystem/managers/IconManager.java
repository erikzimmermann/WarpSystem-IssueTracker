package de.codingair.warpsystem.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.tools.ItemBuilder;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.gui.affiliations.*;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.importfilter.CategoryData;
import de.codingair.warpsystem.importfilter.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IconManager {
    public static ItemBuilder STANDARD_ITEM() {return new ItemBuilder(Material.GRASS);}

    private List<Warp> warps = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public void load(boolean sync) throws Exception {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> {
                try {
                    load(true);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            //Load

            ActionIconHelper.load = true;

            this.warps = new ArrayList<>();

            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> warps = config.getStringList("Warps");
            for(String s : warps) {
                Warp warp = ActionIconHelper.fromString(s);

                if(warp != null) {
                    if(warp.getName().contains("@")) warp.setName(warp.getName().replace("@", "(at)"));
                    this.warps.add(warp);
                }
            }

            List<String> categories = config.getStringList("Categories");
            for(String s : categories) {
                Category category = ActionIconHelper.fromString(s);

                if(category != null) {
                    if(category.getName().contains("@")) category.setName(category.getName().replace("@", "(at)"));
                    this.categories.add(category);
                }
            }

            for(Warp warp : this.warps) {
                if(warp.getCategory() == null) continue;
                if(!existsCategory(warp.getCategory().getName())) {
                    this.categories.add(warp.getCategory());
                }
            }

            ActionIconHelper.load = false;

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

                WarpSystem.getInstance().getFileManager().getFile("Categories").delete();
                WarpSystem.getInstance().getFileManager().getFile("Warps").delete();
            }
        }
    }

    public void save(boolean sync) throws Exception {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> {
                try {
                    save(true);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            //Save
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> warps = new ArrayList<>();
            for(Warp warp : this.warps) {
                warps.add(ActionIconHelper.toString(warp));
            }

            List<String> categories = new ArrayList<>();
            for(Category category : this.categories) {
                categories.add(ActionIconHelper.toString(category));
            }

            config.set("Warps", warps);
            config.set("Categories", categories);
            file.saveConfig();
        }
    }

    private int getNextFreeSlot(Category category) {
        int slot = 0;
        List<Integer> unavailable = new ArrayList<>();
        unavailable.add(0);
        unavailable.add(8);
        unavailable.add(45);
        unavailable.add(53);

        boolean available;

        do {
            available = true;

            if(slot > 53) break;

            if(unavailable.contains(slot)) {
                slot++;
                available = false;
            } else {
                if(category == null) {
                    for(Category c : this.categories) {
                        if(c.getSlot() == slot) {
                            slot++;
                            available = false;
                            break;
                        }
                    }

                    for(Warp warp : getWarps(null)) {
                        if(warp.getSlot() == slot) {
                            slot++;
                            available = false;
                            break;
                        }
                    }
                } else {
                    for(Warp warp : getWarps(category)) {
                        if(warp.getSlot() == slot) {
                            slot++;
                            available = false;
                            break;
                        }
                    }
                }
            }
        } while(!available);

        if(available) return slot;
        else return -999;
    }

    public boolean importCategoryData(CategoryData categoryData) {
        int slot = getNextFreeSlot(null);

        if(slot == -999) return false;
        if(this.existsCategory(categoryData.getName())) return false;

        Category c = new Category(categoryData.getName(), STANDARD_ITEM().setName(categoryData.getName()).getItem(), slot, categoryData.getPermission());
        this.categories.add(c);

        boolean result = true;

        for(WarpData warpData : categoryData.getWarps()) {
            if(!importWarpData(warpData)) result = false;
        }

        return result;
    }

    public boolean importWarpData(WarpData warpData) {
        if(warpData.getCategory() != null && !existsCategory(warpData.getCategory())) return false;
        Category category = warpData.getCategory() == null ? null : getCategory(warpData.getCategory());

        int slot = getNextFreeSlot(category);

        if(slot == -999) return false;
        if(this.existsWarp(warpData.getName(), category)) return false;

        Location loc = new Location(new org.bukkit.Location(Bukkit.getWorld(warpData.getWorld()), warpData.getX(), warpData.getY(), warpData.getZ(), warpData.getYaw(), warpData.getPitch()));

        Warp warp = new Warp(warpData.getName(), STANDARD_ITEM().setName(warpData.getName()).getItem(), slot, warpData.getPermission(), category, new ActionObject(Action.TELEPORT_TO_WARP, new SerializableLocation(loc)));
        this.warps.add(warp);
        return true;
    }

    public boolean existsWarp(String name, Category category) {
        if(name == null) return false;
        name = Color.removeColor(name);

        return getWarp(name, category) != null;
    }

    public Warp getWarp(String name, Category category) {
        if(name == null) return null;
        name = Color.removeColor(name);

        for(Warp warp : getWarps(category)) {
            if(warp.getNameWithoutColor().equalsIgnoreCase(name)) return warp;
        }

        return null;
    }

    public boolean existsCategory(String name) {
        if(name == null) return false;
        name = Color.removeColor(name);

        return getCategory(name) != null;
    }

    public Category getCategory(String name) {
        if(name == null) return null;
        name = Color.removeColor(name);

        for(Category c : this.categories) {
            if(c.getNameWithoutColor().equalsIgnoreCase(name)) return c;
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

        for(Warp icon : this.warps) {
            if((icon.getCategory() == null && category == null) || ((icon.getCategory() != null && category != null) && icon.getCategory().getName().equals(category.getName()))) icons.add(icon);
        }

        return icons;
    }

    public void remove(ActionIcon icon) {
        if(icon instanceof Category) {
            Category category = (Category) icon;
            List<Warp> warps = getWarps(category);

            for(Warp warp : warps) {
                remove(warp);
            }

            this.categories.remove(icon);
        } else if(icon instanceof Warp) {
            this.warps.remove(icon);
        }
    }
}
