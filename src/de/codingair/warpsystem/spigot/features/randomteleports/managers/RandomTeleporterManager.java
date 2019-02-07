package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.CRandomTP;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.InteractListener;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomTeleporter;
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
    public static boolean hasPermission(Player player) {
        if(player.isOp()) return true;

        int teleports = getInstance().getTeleport(player) + 1;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.toLowerCase().startsWith("warpsystem.randomteleporters.")) {
                int amount = Integer.parseInt(perm.substring(29));
                if(amount >= teleports) return true;
            }

        }

        return false;
    }
    public static String PERMISSION(int amount) { return "WarpSystem.RandomTeleporters." + amount; }
    private int costs;
    private double minRange;
    private double maxRange;
    private boolean worldGuardSupport;
    private List<Biome> biomeList = new ArrayList<>();

    private List<Location> interactBlocks = new ArrayList<>();
    private InteractListener listener = new InteractListener();

    @Override
    public boolean load() {
        if(WarpSystem.getInstance().getFileManager().getFile("Config") == null) WarpSystem.getInstance().getFileManager().loadFile("Config", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        UTFConfig config = file.getConfig();

        WarpSystem.log("  > Loading RandomTeleporters");
        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());
        new CRandomTP().register(WarpSystem.getInstance());

        this.costs = config.getInt("WarpSystem.RandomTeleport.Costs", 500);
        this.minRange = config.getDouble("WarpSystem.RandomTeleport.Range.Min", 1000);
        this.maxRange = config.getDouble("WarpSystem.RandomTeleport.Range.Max", 10000);

        this.worldGuardSupport = config.getBoolean("WarpSystem.RandomTeleport.Support.WorldGuard", true);
        if(config.getBoolean("WarpSystem.RandomTeleport.Support.Biome.Enabled", true)) {
            List<String> configBiomes = config.getStringList("WarpSystem.RandomTeleport.Support.Biome.BiomeList");

            if(configBiomes == null || configBiomes.isEmpty()) {
                for(Biome value : Biome.values()) {
                    if(value.name().equalsIgnoreCase("VOID")) continue;
                    this.biomeList.add(value);
                }
            } else {
                for(String biome : configBiomes) {
                    try {
                        Biome b = Biome.valueOf(biome);
                        if(b != Biome.VOID) this.biomeList.add(b);
                    } catch(Throwable ignored) {
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
        this.biomeList.clear();
        HandlerList.unregisterAll(this.listener);
    }

    public void tryToTeleport(Player player) {
        if(!hasPermission(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
            return;
        }

        RandomTeleporter t = new RandomTeleporter(player);
        Thread thread = new Thread(t);
        thread.start();
    }

    public int getTeleport(Player player) {
        return getTeleports(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public int getTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        UTFConfig config = file.getConfig();

        return config.getInt("RandomTeleporter.Teleports." + uuid.toString(), 0);
    }

    public int getCosts() {
        return costs;
    }

    public double getMinRange() {
        return minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public boolean isWorldGuardSupport() {
        return worldGuardSupport;
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
}
