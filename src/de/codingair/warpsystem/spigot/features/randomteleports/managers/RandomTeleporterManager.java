package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.CRandomTP;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.InteractListener;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility.RTPTagConverter_v4_2_2;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class RandomTeleporterManager implements Manager {
    private boolean buyable;
    private double costs;
    private double minRange;
    private double maxRange;
    private boolean protectedRegions;
    private boolean worldBorder;
    private List<Biome> biomeList;
    private List<World> worldList = new ArrayList<>();
    private HashMap<Player, RandomLocationCalculator> searching = new HashMap<>();

    private int netherHeight;
    private int endHeight;

    private List<Location> interactBlocks = new ArrayList<>();
    private InteractListener listener = new InteractListener();

    @Override
    public void preLoad() {
        new RTPTagConverter_v4_2_2();
    }

    @Override
    public boolean load(boolean loader) {
        if(WarpSystem.getInstance().getFileManager().getFile("PlayData") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayData", "/Memory/");
        UTFConfig config = WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/").getConfig();

        WarpSystem.log("  > Loading RandomTeleporters");

        this.buyable = config.getBoolean("RandomTeleport.Buyable.Enabled", true);
        this.costs = config.getDouble("RandomTeleport.Buyable.Costs", 500.0);
        this.minRange = config.getDouble("RandomTeleport.Range.Min", 1000);
        this.maxRange = config.getDouble("RandomTeleport.Range.Max", 10000);

        this.netherHeight = config.getInt("RandomTeleport.Range.Highest_Y.Nether", 126);
        this.endHeight = config.getInt("RandomTeleport.Range.Highest_Y.End", 72);

        if(config.getBoolean("RandomTeleport.Worlds.Enabled", false)) {
            for(String name : config.getStringList("RandomTeleport.Worlds.List")) {
                World w = Bukkit.getWorld(name);
                if(w != null) this.worldList.add(w);
            }
        }

        this.protectedRegions = config.getBoolean("RandomTeleport.Support.ProtectedRegions", true);
        this.worldBorder = config.getBoolean("RandomTeleport.Support.WorldBorder", true);
        if(config.getBoolean("RandomTeleport.Support.Biome.Enabled", true)) {
            List<String> configBiomes = config.getStringList("RandomTeleport.Support.Biome.BiomeList");
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
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        config = file.getConfig();

        List<?> l = config.getList("RandomTeleporter.InteractBlocks");
        if(l != null)
            for(Object s : l) {
                if(s instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) s);
                    Location loc = new Location();
                    try {
                        loc.read(json);
                    } catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    this.interactBlocks.add(loc);
                } else if(s instanceof String) {
                    this.interactBlocks.add(Location.getByJSONString((String) s));
                }
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

        List<JSON> interactBlocks = new ArrayList<>();
        for(Location l : this.interactBlocks) {
            JSON json = new JSON();
            l.trim(0);
            l.write(json);
            interactBlocks.add(json);
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

            if(perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.max.")) {
                String s = perm.substring(33);
                if(s.equals("*") || s.equalsIgnoreCase("n")) return -1;

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

            if(perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.free.")) {
                String s = perm.substring(34);
                if(s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                try {
                    return Integer.parseInt(s);
                } catch(Throwable ignored) {
                }
            }
        }

        return 0;
    }

    public void tryToTeleport(Player player) {
        RandomLocationCalculator c;
        if((c = searching.get(player)) != null) {
            if(System.currentTimeMillis() - c.getLastReaction() > 5000) {
                searching.remove(player);
                player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
            } else player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Already_Searching"));

            return;
        }

        if(!canTeleport(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
            return;
        }

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

                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                        player.teleport(loc);
                        Sound.ENDERMAN_TELEPORT.playSound(player);
                        player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported"));
                    });
                }

                searching.remove(player);
            }
        });

        player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Searching"));
        searching.put(player, t);
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
        return buyable && MoneyAdapterType.canEnable();
    }

    public boolean isWorldBorder() {
        return worldBorder;
    }

    public List<World> getWorldList() {
        return worldList;
    }

    public int getNetherHeight() {
        return netherHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }
}
