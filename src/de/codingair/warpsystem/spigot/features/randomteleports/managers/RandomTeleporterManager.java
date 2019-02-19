package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.CRandomTP;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.InteractListener;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RandomTeleporterManager implements Manager {
    private boolean buyable;
    private double costs;
    private double minRange;
    private double maxRange;
    private boolean protectedRegions;
    private List<Biome> biomeList;
    private List<Player> searching = new ArrayList<>();

    private List<Location> interactBlocks = new ArrayList<>();
    private InteractListener listener = new InteractListener();

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("PlayData") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayData", "/Memory/");
        if(WarpSystem.getInstance().getFileManager().getFile("Config") == null) WarpSystem.getInstance().getFileManager().loadFile("Config", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        UTFConfig config = file.getConfig();

        WarpSystem.log("  > Loading RandomTeleporters");

        this.buyable = config.getBoolean("WarpSystem.RandomTeleport.Buyable.Enabled", true);
        this.costs = config.getDouble("WarpSystem.RandomTeleport.Buyable.Costs", 500.0);
        this.minRange = config.getDouble("WarpSystem.RandomTeleport.Range.Min", 1000);
        this.maxRange = config.getDouble("WarpSystem.RandomTeleport.Range.Max", 10000);

        this.protectedRegions = config.getBoolean("WarpSystem.RandomTeleport.Support.ProtectedRegions", true);
        if(config.getBoolean("WarpSystem.RandomTeleport.Support.Biome.Enabled", true)) {
            List<String> configBiomes = config.getStringList("WarpSystem.RandomTeleport.Support.Biome.BiomeList");
            biomeList = new ArrayList<>();

            if(configBiomes == null || configBiomes.isEmpty()) {
                for(Biome value : Biome.values()) {
                    if(value.name().equalsIgnoreCase("VOID")) continue;
                    this.biomeList.add(value);
                }
            } else {
                for(String biome : configBiomes) {
                    for(Biome value : Biome.values()) {
                        if(value.name().equalsIgnoreCase(biome) && !biomeList.contains(value)) {
                            biomeList.add(value);
                            break;
                        }
                    }
                }
            }
        }

        if(WarpSystem.getInstance().getFileManager().getFile("Teleporters") == null) WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        config = file.getConfig();

        List<String> interactBlocks = config.getStringList("RandomTeleporter.InteractBlocks");
        for(String s : interactBlocks) {
            this.interactBlocks.add(Location.getByJSONString(s));
        }

        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());
        new CRandomTP().register(WarpSystem.getInstance());

        WarpSystem.log("    ...got " + this.interactBlocks.size() + " InteractBlock(s)");
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        UTFConfig config = file.getConfig();

        if(!saver) WarpSystem.log("  > Saving RandomTeleporters");

        List<String> interactBlocks = new ArrayList<>();
        for(Location l : this.interactBlocks) {
            interactBlocks.add(l.toJSONString(4));
        }

        config.set("RandomTeleporter.InteractBlocks", interactBlocks);
        file.saveConfig();
        if(!saver) WarpSystem.log("    ...saved " + interactBlocks.size() + " InteractBlock(s)");
    }

    @Override
    public void destroy() {
        this.interactBlocks.clear();
        if(this.biomeList != null) this.biomeList.clear();
        HandlerList.unregisterAll(this.listener);
    }

    public boolean canBuy(Player player) {
        if(player.isOp()) return true;

        int bought = getInstance().getBoughtTeleports(player);
        int free = getInstance().getFreeTeleportAmount(player);
        int max = getInstance().getMaxTeleportAmount(player);

        return max == -1 || max - free - bought > 0;
    }

    public boolean canTeleport(Player player) {
        if(player.isOp()) return true;

        UUID u = WarpSystem.getInstance().getUUIDManager().get(player);
        int bought = getInstance().getBoughtTeleports(u);
        int teleports = getInstance().getTeleports(u);
        int free = getInstance().getFreeTeleportAmount(player);

        return free == -1 || free + bought - teleports > 0;
    }

    public int getMaxTeleportAmount(Player player) {
        if(player.isOp()) return -1;

        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.max.")) {
                String s = perm.substring(33);
                if(s.equals("*")) return -1;

                try {
                    return Integer.parseInt(s);
                } catch(Throwable ignored) {
                }
            }

        }

        return 0;
    }

    public int getFreeTeleportAmount(Player player) {
        if(player.isOp()) return -1;

        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.free.")) {
                String s = perm.substring(34);
                if(s.equals("*")) return -1;

                try {
                    return Integer.parseInt(s);
                } catch(Throwable ignored) {
                }
            }
        }

        return 0;
    }

    public void tryToTeleport(Player player) {
        if(!canTeleport(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
            return;
        }

        if(searching.contains(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Already_Searching"));
            return;
        }

        searching.add(player);

        RandomLocationCalculator t = new RandomLocationCalculator(player, new Callback<Location>() {
            @Override
            public void accept(Location loc) {
                if(loc == null) {
                    //no location found, try again
                    player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                } else {
                    //teleported
                    UUID uuid = WarpSystem.getInstance().getUUIDManager().get(player);
                    if(!player.isOp()) setTeleports(uuid, getTeleports(uuid) + 1);
                    player.teleport(loc);
                    Sound.ENDERMAN_TELEPORT.playSound(player);
                    player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported"));
                }

                searching.remove(player);
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), t);
    }

    public void setTeleports(Player player, int teleports) {
        setTeleports(WarpSystem.getInstance().getUUIDManager().get(player), teleports);
    }

    public void setTeleports(UUID uuid, int teleports) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();
        config.set("RandomTeleporter." + uuid.toString() + ".Teleports", teleports);
        file.saveConfig();
    }

    public int getTeleports(Player player) {
        return getTeleports(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public int getTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();

        return config.getInt("RandomTeleporter." + uuid.toString() + ".Teleports", 0);
    }

    public void setBoughtTeleports(Player player, int teleports) {
        setBoughtTeleports(WarpSystem.getInstance().getUUIDManager().get(player), teleports);
    }

    public void setBoughtTeleports(UUID uuid, int teleports) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();
        config.set("RandomTeleporter." + uuid.toString() + ".Bought", teleports);
        file.saveConfig();
    }

    public int getBoughtTeleports(Player player) {
        return getBoughtTeleports(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public int getBoughtTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();

        return config.getInt("RandomTeleporter." + uuid.toString() + ".Bought", 0);
    }

    public double getCosts() {
        return costs;
    }

    public double getMinRange() {
        return minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public boolean isProtectedRegions() {
        return protectedRegions;
    }

    public List<Biome> getBiomeList() {
        return biomeList;
    }

    public List<Location> getInteractBlocks() {
        return interactBlocks;
    }

    public InteractListener getListener() {
        return listener;
    }

    public static RandomTeleporterManager getInstance() {
        return ((RandomTeleporterManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TELEPORTS));
    }

    public boolean isBuyable() {
        return buyable && AdapterType.canEnable();
    }
}
