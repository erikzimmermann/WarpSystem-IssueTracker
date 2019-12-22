package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.codingapi.tools.io.yml.ConfigWriter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.commands.CPlayerWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarpConfig;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerWarpManager implements Manager {
    private HashMap<UUID, List<PlayerWarp>> warps = new HashMap<>();

    private int minTime;
    private int maxTime;
    private int minMessageCharLength;
    private int maxMessageCharLength;
    private int maxTeleportCosts;
    private int teleportCostsSteps;
    private int refundByRemovingMessage;
    private int teleportCosts;
    private int nameChangeCosts;
    private int messageChangeCosts;
    private int inactiveTime;
    private boolean protectedRegions;
    private List<Integer> inactiveReminds;
    private boolean refund;
    private boolean keys;
    private boolean firstPublic; //true = the TW will be public when you open up the create gui
    private TempWarpConfig config;

    @Override
    public boolean load() {
        boolean success = true;

        this.warps.clear();

        if(WarpSystem.getInstance().getFileManager().getFile("PlayerWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/Memory/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        WarpSystem.log("  > Loading PlayerWarps");
        int size = 0;

        ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("Config");
        FileConfiguration config = configFile.getConfig();

        this.minTime = config.getInt("WarpSystem.TempWarps.Time.Min_Time", 5);
        this.maxTime = config.getInt("WarpSystem.TempWarps.Time.Max_Time", 20);
        this.minMessageCharLength = config.getInt("WarpSystem.TempWarps.Message.Min_character_length", 5);
        this.maxMessageCharLength = config.getInt("WarpSystem.TempWarps.Message.Max_character_length", 50);
        this.messageChangeCosts = config.getInt("WarpSystem.TempWarps.Message.Edit_Costs", 50);
        this.refundByRemovingMessage = config.getInt("WarpSystem.TempWarps.Message.Refund_by_removing_message", 0);
        this.maxTeleportCosts = config.getInt("WarpSystem.TempWarps.Custom_Teleport_Costs.Max_Costs", 500);
        this.teleportCostsSteps = config.getInt("WarpSystem.TempWarps.Custom_Teleport_Costs.Cost_Steps", 50);
        this.teleportCosts = config.getInt("WarpSystem.TempWarps.Custom_Teleport_Costs.TeleportCosts", 50);
        this.inactiveTime = config.getInt("WarpSystem.TempWarps.Inactive.Time_After_Expiration", 60);
        this.nameChangeCosts = config.getInt("WarpSystem.TempWarps.Name.Edit_Costs", 50);
        this.inactiveReminds = config.getIntegerList("WarpSystem.TempWarps.Inactive.Reminds");
        this.refund = config.getBoolean("WarpSystem.TempWarps.Refund", true);
        this.protectedRegions = config.getBoolean("WarpSystem.TempWarps.Support.ProtectedRegions", false);
        this.keys = config.getBoolean("WarpSystem.TempWarps.Keys", false);
        this.firstPublic = config.getBoolean("WarpSystem.TempWarps.Public_as_create_state", false);

        String timeUnit = config.getString("WarpSystem.TempWarps.Time.Interval", "min");
        TimeUnit unit = getTimeUnitOfString(timeUnit);
        if(unit == null) {
            unit = TimeUnit.MINUTES;
            config.set("WarpSystem.TempWarps.Time.Interval", "min");
            configFile.saveConfig();
        }
        int durationCosts = config.getInt("WarpSystem.TempWarps.Costs.CostsPerInterval", 5);
        int durationSteps = config.getInt("WarpSystem.TempWarps.Time.DurationSteps", 5);
        int publicCosts = config.getInt("WarpSystem.TempWarps.Costs.PublicCosts", 5);
        int messageCosts = config.getInt("WarpSystem.TempWarps.Costs.MessageCosts", 5);

        this.config = new TempWarpConfig(unit, durationCosts, durationSteps, publicCosts, messageCosts);

        for(String key : file.getConfig().getKeys(false)) {
            ConfigWriter w = new ConfigWriter(file, key);

            PlayerWarp p = new PlayerWarp();

            try {
                p.read(w);
                add(p);
                size++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        new CPlayerWarp().register(WarpSystem.getInstance());
        new CPlayerWarps().register(WarpSystem.getInstance());

        WarpSystem.log("    ...got " + size + " PlayerWarp(s)");

        return success;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        int entries = 0;

        for(List<PlayerWarp> data : this.warps.values()) {
            for(PlayerWarp w : data) {
                ConfigWriter writer = new ConfigWriter(file, w.getName(false));
                w.write(writer);
            }

            entries += data.size();
        }

        file.saveConfig();
        if(!saver) WarpSystem.log("    ...saved " + entries + " PlayerWarp(s)");
    }

    @Override
    public void destroy() {

    }

    public static String convertInTimeFormat(long time, TimeUnit unit) {
        long days = TimeUnit.DAYS.convert(time, unit);
        time -= unit.convert(days, TimeUnit.DAYS);
        long hours = TimeUnit.HOURS.convert(time, unit);
        time -= unit.convert(hours, TimeUnit.HOURS);
        long min = TimeUnit.MINUTES.convert(time, unit);
        time -= unit.convert(min, TimeUnit.MINUTES);
        long sec = TimeUnit.SECONDS.convert(time, unit);

        StringBuilder builder = new StringBuilder();

        if(days > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(days).append("d");
        }

        if(hours > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(hours).append("h");
        }

        if(min > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(min).append("m");
        }

        if(sec > 0 || (sec == 0 && builder.toString().isEmpty())) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(sec).append("s");
        }

        return builder.toString();
    }

    private TimeUnit getTimeUnitOfString(String s) {
        for(TimeUnit value : TimeUnit.values()) {
            switch(value) {
                case SECONDS:
                case MILLISECONDS:
                case NANOSECONDS:
                case MICROSECONDS:
                    continue;
            }

            if(value.name().toLowerCase().startsWith(s.toLowerCase())) return value;
        }

        return null;
    }

    public List<PlayerWarp> getWarps(Player player) {
        return getWarps(player.getUniqueId());
    }

    public List<PlayerWarp> getWarps(UUID id) {
        List l = warps.get(id);
        return l == null ? new ArrayList<>() : l;
    }

    public List<PlayerWarp> getWarps(Player player, boolean trusted) {
        if(!trusted) return getWarps(player);
        List<PlayerWarp> warps = new ArrayList<>(getWarps(player));

        for(List<PlayerWarp> ws : this.warps.values()) {
            for(PlayerWarp warp : ws) {
                if(warp.isTrusted(player)) warps.add(warp);
            }
        }

        return warps;
    }

    public PlayerWarp getWarp(String name) {
        return getWarp(name, null);
    }

    public PlayerWarp getWarp(String name, PlayerWarp except) {
        for(List<PlayerWarp> warps : warps.values()) {
            for(PlayerWarp warp : warps) {
                if(warp.equals(except)) continue;
                if(warp.equalsName(name)) return warp;
            }
        }

        return null;
    }

    public boolean exists(String name) {
        return exists(name, null);
    }

    public void add(PlayerWarp warp) {
        List<PlayerWarp> warps = getWarps(warp.getOwner().getId());
        warps.add(warp);
        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
    }

    public boolean exists(String name, PlayerWarp except) {
        return getWarp(name, except) != null;
    }

    public static PlayerWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARS);
    }

    public int getMinTime() {
        return minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getMinMessageCharLength() {
        return minMessageCharLength;
    }

    public int getMaxMessageCharLength() {
        return maxMessageCharLength;
    }

    public int getMaxTeleportCosts() {
        return maxTeleportCosts;
    }

    public int getTeleportCostsSteps() {
        return teleportCostsSteps;
    }

    public int getRefundByRemovingMessage() {
        return refundByRemovingMessage;
    }

    public int getTeleportCosts() {
        return teleportCosts;
    }

    public int getNameChangeCosts() {
        return nameChangeCosts;
    }

    public int getMessageChangeCosts() {
        return messageChangeCosts;
    }

    public int getInactiveTime() {
        return inactiveTime;
    }

    public boolean isProtectedRegions() {
        return protectedRegions;
    }

    public List<Integer> getInactiveReminds() {
        return inactiveReminds;
    }

    public boolean isRefund() {
        return refund;
    }

    public boolean isKeys() {
        return keys;
    }

    public boolean isFirstPublic() {
        return firstPublic;
    }

    public TempWarpConfig getConfig() {
        return config;
    }
}
