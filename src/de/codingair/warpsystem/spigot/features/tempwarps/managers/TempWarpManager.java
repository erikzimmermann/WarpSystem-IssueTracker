package de.codingair.warpsystem.spigot.features.tempwarps.managers;

import de.codingair.codingapi.API;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.utils.Ticker;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.tempwarps.commands.CTempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.commands.CTempWarps;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GCreate;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GDelete;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GEditor;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GTempWarpList;
import de.codingair.warpsystem.spigot.features.tempwarps.listeners.TempWarpListener;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.EmptyTempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarpConfig;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TempWarpManager implements Manager, Ticker {
    public static boolean hasPermission(Player player) {
        if(player.isOp()) return true;

        int warps = TempWarpManager.getManager().getWarps(player).size() + 1;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.toLowerCase().startsWith("warpsystem.tempwarps.")) {
                String s = perm.substring(21);
                if(s.equals("*")) return true;

                try {
                    int amount = Integer.parseInt(s);
                    if(amount >= warps) return true;
                } catch(Throwable ingored) {
                }
            }

        }

        return false;
    }

    public static String PERMISSION(int amount) {
        return "WarpSystem.TempWarps." + amount;
    }

    public static String ERROR_NOT_AVAILABLE(String name) {
        return "§8[§4§lERROR§4 - WarpSystem§8] §cThe TempWarp is not available. Check the info of §n" + name + "§c and take a look to the worlds!";
    }

    private List<EmptyTempWarp> reserved = new ArrayList<>();
    private List<TempWarp> warps = new ArrayList<>();

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

    private TempWarpConfig config;

    private TeleportManager teleportManager = new TeleportManager();

    @Override
    public boolean load() {
        if(!AdapterType.canEnable()) {
            WarpSystem.log("  > No Money-Plugin > Ignoring TempWarps");
            return true;
        }

        API.addTicker(this);

        Bukkit.getPluginManager().registerEvents(new TempWarpListener(), WarpSystem.getInstance());

        new CTempWarp().register(WarpSystem.getInstance());
        new CTempWarps().register(WarpSystem.getInstance());

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        FileConfiguration config = file.getConfig();

        WarpSystem.log("  > Loading TempWarps");

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

        String timeUnit = config.getString("WarpSystem.TempWarps.Time.Interval", "m");
        TimeUnit unit = getTimeUnitOfString(timeUnit);
        int durationCosts = config.getInt("WarpSystem.TempWarps.Costs.CostsPerInterval", 5);
        int durationSteps = config.getInt("WarpSystem.TempWarps.Time.DurationSteps", 5);
        int publicCosts = config.getInt("WarpSystem.TempWarps.Costs.PublicCosts", 5);
        int messageCosts = config.getInt("WarpSystem.TempWarps.Costs.MessageCosts", 5);

        this.config = new TempWarpConfig(unit, durationCosts, durationSteps, publicCosts, messageCosts);

//        WarpSystem.log("    > Loading Warps");
        this.warps.clear();

        if(WarpSystem.getInstance().getFileManager().getFile("TempWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("TempWarps", "/Memory/");
        file = WarpSystem.getInstance().getFileManager().getFile("TempWarps");
        config = file.getConfig();

        for(String s : config.getStringList("Warps")) {
            this.warps.add(TempWarp.getByJSONString(s));
        }

        WarpSystem.log("    ...got " + this.warps.size() + " TempWarp(s)");
        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!AdapterType.canEnable()) return;

        if(!saver) WarpSystem.log("  > Saving TempWarps");
        if(!saver) WarpSystem.log("    > Saving Warps");

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("TempWarps");
        FileConfiguration config = file.getConfig();

        List<String> data = new ArrayList<>();

        for(TempWarp warp : this.warps) {
            data.add(warp.toJSONString());
        }

        config.set("Warps", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("      ...saved " + data.size() + " TempWarp(s)");
        if(!saver) API.removeTicker(this);
    }

    @Override
    public void destroy() {
        this.warps.clear();
        this.reserved.clear();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onSecond() {
        List<TempWarp> warps = new ArrayList<>(this.warps);

        List<GTempWarpList> lists = API.getRemovables(GTempWarpList.class);
        for(GTempWarpList list : lists) {
            for(ItemButton button : list.getButtons().values()) {
                if(button instanceof SyncButton) {
                    ((SyncButton) button).update();
                }
            }

            list.getPlayer().updateInventory();
        }

        for(TempWarp warp : warps) {
            if(warp.isBeingEdited()) continue;
            if(warp.isExpired()) {
                if(-warp.getLeftTime() <= 1000) {
                    for(GTempWarpList list : lists) {
                        list.reinitialize();
                    }

                    Player p = warp.getOnlineOwner();
                    if(p != null)
                        p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_expiring").replace("%TEMP_WARP%", warp.getName()).replace("%TIME_LEFT%", convertInTimeFormat(getInactiveTime(), TimeUnit.SECONDS)));
                }

                for(Integer remind : this.inactiveReminds) {
                    if(remind == getInactiveTime()) continue;

                    long time = -1000L * (getInactiveTime() - remind);
                    if(warp.getLeftTime() >= time - 1050L && warp.getLeftTime() < time) {
                        Player p = warp.getOnlineOwner();
                        if(p != null)
                            p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Deletion_In").replace("%TEMP_WARP%", warp.getName()).replace("%TIME_LEFT%", convertInTimeFormat(remind, TimeUnit.SECONDS)));
                    }
                }

                Date inactive = new Date(warp.getExpireDate().getTime() + TimeUnit.MILLISECONDS.convert(this.inactiveTime, TimeUnit.SECONDS));

                if(inactive.before(new Date())) {
                    //Delete
                    this.warps.remove(warp);
                    Player player = warp.getOnlineOwner();
                    if(player != null) {
                        GTempWarpList list = API.getRemovable(player, GTempWarpList.class);
                        if(list != null) list.reinitialize();

                        GUI gui = API.getRemovable(player, GCreate.class);
                        if(gui != null) gui.close();
                        gui = API.getRemovable(player, GDelete.class);
                        if(gui != null) gui.close();

                        player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_was_deleted").replace("%WARP%", warp.getName()));
                    }
                }
            }
        }

        lists.clear();
        warps.clear();
    }

    public void create(Player player, String... args) {
        EmptyTempWarp warp = new EmptyTempWarp(player);
        warp.setDuration(minTime);
        if(args.length > 0 && !isReserved(player.getName() + "." + args[0])) warp.setName(args[0]);

        new GCreate(player, warp).open();
    }

    public void edit(Player player, String name) {
        TempWarp warp = TempWarpManager.getManager().getWarp(name, player);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return;
        }

        if(!warp.isOwner(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_no_access"));
            return;
        }

        new GEditor(player, warp).open();
    }

    public void reactivate(Player player, String name) {
        TempWarp warp = TempWarpManager.getManager().getWarp(name, player);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return;
        }

        if(!warp.isOwner(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_no_access"));
            return;
        }

        if(!warp.isExpired()) {
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_is_not_expired"));
            return;
        }

        if(warp.getCosts() > AdapterType.getActive().getMoney(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Renew_Not_Enough_Money"));
            return;
        }

        new GCreate(player, warp).open();
    }

    public void delete(Player player, String name) {
        TempWarp warp = TempWarpManager.getManager().getWarp(name, player);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return;
        }

        if(!warp.isOwner(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_no_access"));
            return;
        }

        long rC = warp.getRemainingCosts();

        if(rC > 0) new GDelete(player, warp).open();
        else {
            this.warps.remove(warp);
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Deleted").replace("%TEMP_WARP%", warp.getName()));
        }
    }

    public void updateWarps(Player player) {
        List<TempWarp> correct = getWarps(player);

        int inactiveSales = 0;
        for(TempWarp warp : correct) {
            warp.setLastKnownName(player.getName());
            inactiveSales += warp.getInactiveSales();
            warp.setInactiveSales(0);
        }

        AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + inactiveSales);
        player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Inactive_Win").replace("%AMOUNT%", inactiveSales + ""));

        correct.clear();
    }

    public List<TempWarp> getWarps(Player player) {
        return getWarps(player, false);
    }

    public List<TempWarp> getWarps(Player player, boolean onlyPrivate) {
        UUID uniqueId = WarpSystem.getInstance().getUUIDManager().get(player);
        if(uniqueId == null) return new ArrayList<>();

        return getWarps(uniqueId, onlyPrivate);
    }

    public List<TempWarp> getWarps(UUID uniqueId) {
        return getWarps(uniqueId, false);
    }

    public List<TempWarp> getWarps(UUID uniqueId, boolean onlyPrivate) {
        List<TempWarp> correct = new ArrayList<>();
        if(uniqueId == null) return correct;

        List<TempWarp> warps = new ArrayList<>(this.warps);

        for(TempWarp warp : warps) {
            if(warp.getOwner().equals(uniqueId) && (!onlyPrivate || !warp.isPublic())) correct.add(warp);
        }

        warps.clear();
        return correct;
    }

    public boolean isReserved(String identifier) {
        identifier = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', identifier.replace("_", " ")));
        if(getWarp(identifier) != null) return true;

        for(EmptyTempWarp emptyTempWarp : this.reserved) {
            if(emptyTempWarp.getIdentifier() != null && emptyTempWarp.getIdentifier().equalsIgnoreCase(identifier)) return true;
        }

        return false;
    }

    public TempWarp getWarp(String identifier, Player prefer) {
        TempWarp warp = getWarp(identifier);

        if(warp != null && warp.isOwner(prefer)) return warp;
        else warp = getWarp(prefer.getName() + "." + identifier);

        if(warp == null) return getWarp(identifier);
        else return warp;
    }

    public TempWarp getWarp(String identifier) {
        identifier = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', identifier.replace("_", " ")));
        List<TempWarp> warps = new ArrayList<>(this.warps);

        for(TempWarp warp : warps) {
            if(warp.getIdentifier().replace("_", " ").equalsIgnoreCase(identifier)) {
                warps.clear();
                return warp;
            }
        }

        warps.clear();
        return null;
    }

    public String convertInTimeFormat(long time, TimeUnit unit) {
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

    public void activate(TempWarp warp) {
        this.warps.add(warp);
    }

    public void delete(TempWarp warp) {
        this.warps.remove(warp);
    }

    public boolean isSave(Location location) {
        boolean save = true;

        switch(location.getBlock().getType()) {
            case AIR:
            case GRASS:
            case WATER:
                break;

            default:
                save = false;
                break;
        }

        if(!save) return false;

        Material m = location.clone().subtract(0, 1, 0).getBlock().getType();

        save = m.isBlock() && m.isSolid();

        return save;
    }

    public List<TempWarp> getWarps() {
        return new ArrayList<>(warps);
    }

    public List<TempWarp> getActiveWarps() {
        return getActiveWarps(true);
    }

    public List<TempWarp> getActiveWarps(boolean all) {
        List<TempWarp> f = getWarps();
        List<TempWarp> warps = getWarps();

        for(TempWarp warp : warps) {
            if(warp.isExpired() || (!all && !warp.isPublic())) f.remove(warp);
        }

        warps.clear();
        return f;
    }

    public int getMinTime() {
        return minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    private TimeUnit getTimeUnitOfString(String s) {
        for(TimeUnit value : TimeUnit.values()) {
            switch(value) {
                case MILLISECONDS:
                case NANOSECONDS:
                case MICROSECONDS:
                    continue;
            }

            if(value.name().toLowerCase().startsWith(s.toLowerCase())) return value;
        }

        return null;
    }

    public TempWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TEMP_WARPS);
    }

    public static TempWarpManager getManager() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TEMP_WARPS);
    }

    public int getInactiveTime() {
        return inactiveTime;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public TempWarpConfig getConfig() {
        return config;
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

    public double getTeleportCosts() {
        return teleportCosts;
    }

    public double calculateTeleportCosts(int customCosts) {
        double f = customCosts * teleportCosts;

        if(f - (int) f == 0) return (int) f;

        f = ((double) (int) (f * 100)) / 100;
        return f;
    }

    public List<Integer> getInactiveReminds() {
        return inactiveReminds;
    }

    public int getRefundByRemovingMessage() {
        return refundByRemovingMessage;
    }

    public double getNameChangeCosts() {
        return nameChangeCosts;
    }

    public double getMessageChangeCosts() {
        return messageChangeCosts;
    }

    public List<EmptyTempWarp> getReserved() {
        return reserved;
    }

    public boolean isRefund() {
        return refund;
    }

    public boolean isProtectedRegions() {
        return protectedRegions;
    }
}
