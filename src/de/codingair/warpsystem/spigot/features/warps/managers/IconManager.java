package de.codingair.warpsystem.spigot.features.warps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.*;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.importfilter.CategoryData;
import de.codingair.warpsystem.spigot.features.warps.importfilter.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class IconManager {
    public static ItemBuilder STANDARD_ITEM() {
        return new ItemBuilder(Material.GRASS);
    }

    private List<Warp> warps = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<GlobalWarp> globalWarps = new ArrayList<>();
    private List<DecoIcon> decoIcons = new ArrayList<>();

    private int size = 54;

    public boolean load(boolean sync) throws Exception {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> {
                try {
                    load(true);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });

            return true;
        } else {
            //Load
            boolean success = true;

            ActionIconHelper.load = true;

            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
            FileConfiguration config = file.getConfig();
            this.size = config.getInt("WarpSystem.GUI_Size", 54);
            switch(this.size) {
                case 9:
                case 18:
                case 27:
                case 36:
                case 45:
                case 54:
                    break;
                default:
                    this.size = 54;
                    config.set("WarpSystem.GUI_Size", 54);
                    file.saveConfig();
                    break;
            }


            this.warps.clear();
            this.categories.clear();
            this.globalWarps.clear();
            this.decoIcons.clear();

            file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            config = file.getConfig();

            List<String> warps = config.getStringList("Warps");
            for(String s : warps) {
                Warp warp = ActionIconHelper.fromString(s);

                if(warp != null) {
                    if(warp.getName().contains("@")) warp.setName(warp.getName().replace("@", "(at)"));
                    this.warps.add(warp);
                } else success = false;
            }

            List<String> categories = config.getStringList("Categories");
            for(String s : categories) {
                Category category = ActionIconHelper.fromString(s);

                if(category != null) {
                    if(category.getName().contains("@")) category.setName(category.getName().replace("@", "(at)"));
                    this.categories.add(category);
                } else success = false;
            }

            List<String> gWarps = config.getStringList("GlobalWarps");
            for(String s : gWarps) {
                GlobalWarp warp = ActionIconHelper.fromString(s);

                if(warp != null) {
                    if(warp.getName().contains("@")) warp.setName(warp.getName().replace("@", "(at)"));
                    this.globalWarps.add(warp);
                } else success = false;
            }

            List<String> decoIcons = config.getStringList("DecoIcons");
            for(String s : decoIcons) {
                DecoIcon deco = ActionIconHelper.fromString(s);

                if(deco != null) this.decoIcons.add(deco);
                else success = false;
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

            return success;
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

            List<String> gWarps = new ArrayList<>();
            for(GlobalWarp warp : this.globalWarps) {
                gWarps.add(ActionIconHelper.toString(warp));
            }

            List<String> decoIcons = new ArrayList<>();
            for(DecoIcon deco : this.decoIcons) {
                decoIcons.add(ActionIconHelper.toString(deco));
            }

            config.set("Warps", warps);
            config.set("Categories", categories);
            config.set("GlobalWarps", gWarps);
            config.set("DecoIcons", decoIcons);
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
                }

                for(Warp warp : getWarps(category)) {
                    if(warp.getSlot() == slot) {
                        slot++;
                        available = false;
                        break;
                    }
                }

                for(GlobalWarp warp : getGlobalWarps(category)) {
                    if(warp.getSlot() == slot) {
                        slot++;
                        available = false;
                        break;
                    }
                }

                for(DecoIcon deco : getDecoIcons(category)) {
                    if(deco.getSlot() == slot) {
                        slot++;
                        available = false;
                        break;
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

    public boolean existsGlobalWarp(String name) {
        if(name == null) return false;

        return getGlobalWarp(name) != null;
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

    public GlobalWarp getGlobalWarp(String name) {
        for(GlobalWarp icon : this.globalWarps) {
            if(icon.getName().equalsIgnoreCase(name)) return icon;
        }

        return null;
    }

    public List<GlobalWarp> getGlobalWarps(Category category) {
        List<GlobalWarp> icons = new ArrayList<>();

        for(GlobalWarp icon : this.globalWarps) {
            if((icon.getCategory() == null && category == null) || ((icon.getCategory() != null && category != null) && icon.getCategory().getName().equals(category.getName()))) icons.add(icon);
        }

        return icons;
    }

    public List<DecoIcon> getDecoIcons(Category category) {
        List<DecoIcon> icons = new ArrayList<>();

        for(DecoIcon icon : this.decoIcons) {
            if((icon.getCategory() == null && category == null) || ((icon.getCategory() != null && category != null) && icon.getCategory().getName().equals(category.getName()))) icons.add(icon);
        }

        return icons;
    }

    public void remove(Icon icon) {
        if(icon instanceof Category) {
            Category category = (Category) icon;
            List<Warp> warps = getWarps(category);

            for(Warp warp : warps) {
                remove(warp);
            }

            this.categories.remove(icon);
        } else if(icon instanceof Warp) {
            this.warps.remove(icon);
        } else if(icon instanceof GlobalWarp) {
            this.globalWarps.remove(icon);
        } else if(icon instanceof DecoIcon) {
            this.decoIcons.remove(icon);
        }
    }

    public List<GlobalWarp> getGlobalWarps() {
        return globalWarps;
    }

    public List<DecoIcon> getDecoIcons() {
        return decoIcons;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
