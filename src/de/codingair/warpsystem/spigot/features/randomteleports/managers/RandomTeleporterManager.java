package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.ConfigWriter;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.CRandomTp;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.InteractListener;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.RandomTPWorldsPacket;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.WorldOption;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility.RTPTagConverter_v4_2_2;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility.RTPTagConverter_v4_2_6;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

@AvailableForSetupAssistant(type = "Random teleports", config = "RTPConfig")
@Function(name = "Enabled", defaultValue = "true", config = "Config", configPath = "WarpSystem.Functions.RandomTeleports", clazz = Boolean.class)
@Function(name = "Protected regions", defaultValue = "true", configPath = "RandomTeleport.Support.ProtectedRegions", clazz = Boolean.class)
@Function(name = "World border", defaultValue = "true", configPath = "RandomTeleport.Support.WorldBorder", clazz = Boolean.class)
@Function(name = "Block blacklist", defaultValue = "true", configPath = "RandomTeleport.Block_Blacklist.Enabled", description = "§eBlocks §7» §6RTPConfig.yml", clazz = Boolean.class)
@Function(name = "Biome filter", defaultValue = "false", configPath = "RandomTeleport.Support.Biome.Enabled", description = "§eBiomes §7» §6RTPConfig.yml", clazz = Boolean.class)
public class RandomTeleporterManager implements Manager, BungeeFeature {
    private boolean buyable;
    private double costs;
    private boolean protectedRegions;
    private List<Biome> biomeList;
    private final List<Material> materialBlackList = new ArrayList<>();
    private final List<WorldOption> worldOptions = new ArrayList<>();
    private WorldOption defValues;
    private final HashMap<Player, RandomLocationCalculator> searching = new HashMap<>();

    private int netherHeight;
    private int endHeight;

    private final List<Location> interactBlocks = new ArrayList<>();
    private final InteractListener listener = new InteractListener();

    public static RandomTeleporterManager getInstance() {
        return ((RandomTeleporterManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TELEPORTS));
    }

    @Override
    public void preLoad() {
        new RTPTagConverter_v4_2_2();
        new RTPTagConverter_v4_2_6();
    }

    @Override
    public boolean load(boolean loader) {
        if(WarpSystem.getInstance().getFileManager().getFile("PlayData") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayData", "/Memory/");
        ConfigFile rtpFile = WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/");
        UTFConfig config = rtpFile.getConfig();

        WarpSystem.log("  > Loading RandomTeleporters");

        this.buyable = config.getBoolean("RandomTeleport.Buyable.Enabled", true);
        this.costs = config.getDouble("RandomTeleport.Buyable.Costs", 500.0);

        if(this.defValues != null) this.defValues.destroy();
        this.defValues = new WorldOption("§DEF§");
        ConfigWriter w = new ConfigWriter(rtpFile, "RandomTeleport.Worlds.Default");
        this.defValues.read(w);

        this.netherHeight = config.getInt("RandomTeleport.Range.Highest_Y.Nether", 126);
        this.endHeight = config.getInt("RandomTeleport.Range.Highest_Y.End", 72);

        this.materialBlackList.clear();
        if(config.getBoolean("RandomTeleport.Block_Blacklist.Enabled", false)) {
            for(String material : config.getStringList("RandomTeleport.Block_Blacklist.List")) {
                Optional<XMaterial> parsed = XMaterial.matchXMaterial(material.toUpperCase().replace(" ", "_"));
                parsed.ifPresent(xMaterial -> {
                    Material m = xMaterial.parseMaterial();

                    if(!materialBlackList.contains(m)) materialBlackList.add(m);
                });
            }
        }

        this.protectedRegions = config.getBoolean("RandomTeleport.Support.ProtectedRegions", true);
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

        boolean success = true;
        worldOptions.clear();
        List<?> l = config.getList("RandomTeleport.Worlds.Options");
        if(l != null)
            for(Object data : l) {
                try {
                    JSON json = new JSON((Map<?, ?>) data);
                    for(Object o : json.keySet(false)) {
                        String key = o + "";
                        WorldOption option = new WorldOption(key);
                        json.getSerializable(key, option);
                        worldOptions.add(option);
                    }
                } catch(Exception e) {
                    success = false;
                    e.printStackTrace();
                }
            }

        WarpSystem.log("    ...got " + this.worldOptions.size() + " WorldOption(s)");
        ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        config = file.getConfig();

        l = config.getList("RandomTeleporter.InteractBlocks");
        if(l != null)
            for(Object s : l) {
                if(s instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) s);
                    Location loc = new Location();
                    try {
                        loc.read(json);
                    } catch(Exception e) {
                        success = false;
                        e.printStackTrace();
                        continue;
                    }

                    this.interactBlocks.add(loc);
                } else if(s instanceof String) {
                    this.interactBlocks.add(Location.getByJSONString((String) s));
                }
            }

        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());
        new CRandomTp().register();

        WarpSystem.log("    ...got " + this.interactBlocks.size() + " InteractBlock(s)");
        WarpSystem.getInstance().getBungeeFeatureList().add(this);

        return success;
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
    public void onConnect() {
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            if(!worlds.contains(world.getName())) worlds.add(world.getName());
        }

        WarpSystem.getInstance().getDataHandler().send(new RandomTPWorldsPacket(worlds));
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void destroy() {
        this.interactBlocks.clear();
        if(this.biomeList != null) this.biomeList.clear();
        this.materialBlackList.clear();
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

        int amount = 0;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            if(!effectivePermission.getValue()) continue;
            String perm = effectivePermission.getPermission();

            if(perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.max.")) {
                String s = perm.substring(33);
                if(s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                try {
                    int i = Integer.parseInt(s);
                    if(i > amount) amount = i;
                } catch(Throwable ignored) {
                }
            }

        }

        return amount;
    }

    public int getFreeTeleportAmount(Player player) {
        if(player.isOp()) return -1;

        int amount = 0;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            if(!effectivePermission.getValue()) continue;
            String perm = effectivePermission.getPermission();

            if(perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                    || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.free.")) {
                String s = perm.substring(34);
                if(s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                try {
                    int i = Integer.parseInt(s);
                    if(i > amount) amount = i;
                } catch(Throwable ignored) {
                }
            }
        }

        return amount;
    }

    private WorldOption getOption(World world, WorldOption def) {
        for(WorldOption worldOption : this.worldOptions) {
            if(worldOption.getWorldName().equalsIgnoreCase(world.getName())) return worldOption;
        }

        return def;
    }

    public void tryToTeleport(Player player) {
        tryToTeleport(player.getName(), player.getWorld(), false, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
            }
        });
    }

    public void search(Player player, World target, Callback<Location> callback) {
        Preconditions.checkNotNull(target);
        WorldOption option = getOption(target, defValues);
        org.bukkit.Location start = new Location();
        option.prepareStart(start, target);

        RandomLocationCalculator t = new RandomLocationCalculator(player, start, option.getMin(), option.getMax(), new Callback<Location>() {
            @Override
            public void accept(Location loc) {
                searching.remove(player);

                if(loc != null) {
                    loc.setYaw(player.getLocation().getYaw());
                    loc.setPitch(player.getLocation().getPitch());
                }

                callback.accept(loc);
            }
        });
        searching.put(player, t);
        Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), t);
    }

    public void tryToTeleport(String targetPlayer, World target, boolean force, Callback<Integer> callback) {
        Player player = Bukkit.getPlayerExact(targetPlayer);

        if(player == null) {
            callback.accept(1);
            return;
        }

        RandomLocationCalculator c;
        if((c = searching.get(player)) != null) {
            if(System.currentTimeMillis() - c.getLastReaction() > 5000) {
                searching.remove(player);
                player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
            } else player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Already_Searching"));

            callback.accept(-1);
            return;
        }

        if(!canTeleport(player) && !force) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
            callback.accept(4);
            return;
        }

        search(player, target, new Callback<Location>() {
            @Override
            public void accept(Location loc) {
                if(loc == null) {
                    //no location found, try again
                    player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                    callback.accept(2);
                } else {
                    //teleported
                    UUID uuid = WarpSystem.getInstance().getUUIDManager().get(player);
                    if(!player.isOp()) increaseTeleports(uuid);

                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(loc)), "");
                        options.setOrigin(Origin.RandomTP);
                        options.setMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported"));
                        options.setSkip(true);
                        options.addCallback(new Callback<Result>() {
                            @Override
                            public void accept(Result object) {
                                callback.accept(0);
                            }
                        });

                        WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                    });
                }
            }
        });

        player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Searching"));
    }

    public void increaseTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();
        int i = config.getInt("RandomTeleporter." + uuid.toString() + ".Teleports", 0) + 1;
        config.set("RandomTeleporter." + uuid.toString() + ".Teleports", i);
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

    public WorldOption getDefValues() {
        return defValues;
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

    public boolean isBuyable() {
        return buyable && Bank.isReady();
    }

    public int getNetherHeight() {
        return netherHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public List<Material> getMaterialBlackList() {
        return materialBlackList;
    }
}
