package de.codingair.warpsystem.spigot.features.playerwarps.managers;

import de.codingair.codingapi.API;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.ConfigWriter;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Ticker;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.GlobalLocationAdapter;
import de.codingair.warpsystem.spigot.bstats.Collectible;
import de.codingair.warpsystem.spigot.bstats.Metrics;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarpReference;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.listeners.PlayerWarpListener;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.*;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.forwardcompatibility.PlayerWarpTagConverter_v4_2_2;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.tempwarps.TempWarpAdapter;
import de.codingair.warpsystem.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.spigot.MoveLocalPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RegisterServerForPlayerWarpsPacket;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AvailableForSetupAssistant(type = "PlayerWarps", config = "PlayerWarpConfig")
@Function(name = "Enabled", defaultValue = "true", config = "Config", configPath = "WarpSystem.Functions.PlayerWarps", clazz = Boolean.class)
@Function(name = "Teleport message", defaultValue = "true", config = "Config", configPath = "WarpSystem.Send.Teleport_Message.PlayerWarps", clazz = Boolean.class)
@Function(name = "Max warp amount", defaultValue = "5", configPath = "PlayerWarps.General.Max_Warp_Amount", description = "§7If permissions are §cdisabled", clazz = Integer.class)
@Function(name = "Protected regions", defaultValue = "true", configPath = "PlayerWarps.General.Support.ProtectedRegions", clazz = Boolean.class)
@Function(name = "BungeeCord", defaultValue = "true", configPath = "PlayerWarps.General.BungeeCord", clazz = Boolean.class)
@Function(name = "Economy", defaultValue = "false", configPath = "PlayerWarps.General.Economy", clazz = Boolean.class)
@Function(name = "Force player head", defaultValue = "false", configPath = "PlayerWarps.General.Force_Player_Head", clazz = Boolean.class)
@Function(name = "Force create GUI", defaultValue = "false", configPath = "PlayerWarps.General.Force_Create_GUI", clazz = Boolean.class)
@Function(name = "Public as create state", defaultValue = "false", configPath = "PlayerWarps.General.Public_as_create_state", clazz = Boolean.class)
@Function(name = "Allow public warps", defaultValue = "true", configPath = "PlayerWarps.General.Allow_Public_Warps", clazz = Boolean.class)
@Function(name = "Allow trusted members", defaultValue = "true", configPath = "PlayerWarps.General.Allow_Trusted_Members", clazz = Boolean.class)
@Function(name = "Categories", defaultValue = "true", configPath = "PlayerWarps.General.Categories.Enabled", clazz = Boolean.class)
@Function(name = "Standard time value", defaultValue = "1h", configPath = "PlayerWarps.Time.Standard_Value", clazz = String.class)
@Function(name = "Min. time value", defaultValue = "0d, 0h, 5m", configPath = "PlayerWarps.Time.Min_Time", clazz = String.class)
@Function(name = "Max. time value", defaultValue = "30d, 0h, 0m", configPath = "PlayerWarps.Time.Max_Time", clazz = String.class)
public class PlayerWarpManager implements Manager, Ticker, BungeeFeature, Collectible {
    private int lastCountedPlayerWarpSize = 0;

    private ConfigFile playerWarpsData = null;
    private ConfigFile config = null;
    private final HashMap<UUID, List<PlayerWarp>> warps = new HashMap<>();
    private final HashMap<String, UUID> names = new HashMap<>();
    private final List<Category> warpCategories = new ArrayList<>();
    private final List<String> nameBlacklist = new ArrayList<>();
    private final List<String> worldBlacklist = new ArrayList<>();
    private boolean bungeeCord;
    private final PlayerWarpListener listener = new PlayerWarpListener();
    private int maxAmount = 0;
    private long minTime;
    private long maxTime;
    private double maxTeleportCosts;
    private double teleportCosts;
    private double nameChangeCosts;
    private List<Long> inactiveReminds;
    private boolean firstPublic; //true = the PW will be public when you open up the create gui
    private double publicCosts;
    private double messageCosts;
    private long inactiveTime;
    private double personalItemCosts;
    private double descriptionCosts;
    private double positionChangeCosts;
    private double activeTimeCosts;
    private double itemChangeCosts;
    private int messageMinLength;
    private int messageMaxLength;
    private int descriptionLineMinLength;
    private int descriptionLineMaxLength;
    private int nameMinLength;
    private int nameMaxLength;
    private int descriptionMaxLines;
    private double trustedMemberCosts;
    private double personalItemRefund;
    private double descriptionRefund;
    private double messageRefund;
    private double publicRefund;
    private double teleportCostsRefund;
    private double activeTimeRefund;
    private double trustedMemberRefund;
    private double createCosts;
    private double editCosts;
    private boolean naturalNumbers;
    private boolean internalRefundFactor;
    private boolean economy;
    private boolean forcePlayerHead;
    private boolean customTeleportCosts;
    private boolean protectedRegions;
    private int classesMin;
    private int classesMax;
    private boolean classes;
    private long timeStandardValue;
    private boolean forceCreateGUI;
    private boolean allowPublicWarps;
    private boolean allowTrustedMembers;

    public static boolean hasPermission(Player player) {
        if(player.isOp()) return true;

        int warps = PlayerWarpManager.getManager().getOwnWarps(player).size();
        int maxAmount = getMaxAmount(player);

        return maxAmount == -1 || warps < maxAmount;
    }

    /**
     * @param player Player
     * @return Max amount of PlayerWarps the player can have.
     * Returns -1 if player can have unlimited warps.
     */
    public static int getMaxAmount(Player player) {
        if(player.isOp()) return -1;

        if(WarpSystem.PERMISSION_USE_PLAYER_WARPS != null) {
            int amount = 0;
            for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
                if(!effectivePermission.getValue()) continue;
                String perm = effectivePermission.getPermission();

                if(perm.equals("*") || perm.equalsIgnoreCase("warpsystem.*")) return -1;
                if(perm.toLowerCase().startsWith("warpsystem.playerwarps.")) {
                    String s = perm.substring(23);
                    if(s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                    try {
                        int i = Integer.parseInt(s);
                        if(i > amount) amount = i;
                    } catch(Throwable ignored) {
                    }
                }
            }
            return amount;
        } else return getManager().maxAmount;
    }

    public static PlayerWarpManager getManager() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARS);
    }

    public static boolean isProtected(Player player) {
        String w = player.getLocation().getWorld().getName().toLowerCase();
        for(String s : getManager().worldBlacklist) {
            if(w.equals(s.toLowerCase())) return true;
        }

        if(!getManager().isProtectedRegions()) return false;

        PermissionPlayer check = null;
        try {
            check = PermissionPlayer.class.getConstructor(Player.class).newInstance(player);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return true;
        }
        BlockBreakEvent event = new BlockBreakEvent(player.getLocation().getBlock(), check);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    @Override
    public void collectOptionStatistics(Map<String, Integer> entry) {
        if(classes) entry.put("Classes", 1);
        if(economy) entry.put("Economy", 1);

        if(bungeeCord) {
            if(WarpSystem.getInstance().isOnBungeeCord()) entry.put("BungeeCord", 1);
            else if(Bukkit.getOnlinePlayers().isEmpty()) entry.put("BungeeCord (empty server)", 1);
        }

        entry.put("Warps", 1);
    }

    @Override
    public void addCustomCarts(Metrics metrics) {
        metrics.addCustomChart(new Metrics.SingleLineChart("playerwarp_usage", () -> {
            if(!bungeeCord || WarpSystem.getInstance().isOnBungeeCord()) {
                lastCountedPlayerWarpSize = 0;

                interactWithWarps(new Callback<PlayerWarp>() {
                    @Override
                    public void accept(PlayerWarp warp) {
                        WarpAction action = warp.getAction(Action.WARP);
                        if(action != null) {
                            String s = ((GlobalLocationAdapter) action.getValue().getAdapter()).getServer();
                            if(s == null || s.equals(WarpSystem.getInstance().getCurrentServer())) {
                                lastCountedPlayerWarpSize++;
                            }
                        }
                    }
                });
            }

            return lastCountedPlayerWarpSize;
        }));
    }

    @Override
    public void preLoad() {
        new PlayerWarpTagConverter_v4_2_2();
    }

    @Override
    public boolean load(boolean loader) {
        boolean success = true;

        this.warps.clear();
        this.warpCategories.clear();
        this.nameBlacklist.clear();
        this.worldBlacklist.clear();

        this.playerWarpsData = WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/Memory/");
        this.config = WarpSystem.getInstance().getFileManager().loadFile("PlayerWarpConfig", "/");
        FileConfiguration config = this.config.getConfig();

        int size = 0;

        this.bungeeCord = config.getBoolean("PlayerWarps.General.BungeeCord", true);
        this.economy = config.getBoolean("PlayerWarps.General.Economy", true);
        WarpSystem.log("  > Loading PlayerWarps [Bungee: " + bungeeCord + "; TimeDependent: " + economy + "]");

        // Timings
        this.minTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Min_Time", null), 300000);
        this.maxTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Max_Time", null), 2592000000L);

        List<String> reminds = config.getStringList("Inactive.Reminds");
        this.inactiveReminds = new ArrayList<>();

        for(int i = 0; i < reminds.size(); i++) {
            String data = i < reminds.size() ? reminds.get(i) : null;

            long time = StringFormatter.convertFromTimeFormat(data);
            if(time > 0) inactiveReminds.add(time);
        }

        this.inactiveTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Inactive.Time_After_Expiration", null), 2592000000L);

        //Costs - Generally
        this.maxAmount = config.getInt("PlayerWarps.General.Max_Warp_Amount", 5);
        this.protectedRegions = config.getBoolean("PlayerWarps.General.Support.ProtectedRegions", true);
        this.nameBlacklist.addAll(config.getStringList("PlayerWarps.General.Name_Blacklist"));
        this.worldBlacklist.addAll(config.getStringList("PlayerWarps.General.World_Blacklist"));
        this.createCosts = config.getDouble("PlayerWarps.Costs.Create", 200);
        this.editCosts = config.getDouble("PlayerWarps.Costs.Edit", 200);
        this.naturalNumbers = config.getBoolean("PlayerWarps.Costs.Round_costs_to_natural_numbers", false);
        this.internalRefundFactor = config.getBoolean("PlayerWarps.Costs.Internal_Refund_Factor", false);
        this.forcePlayerHead = config.getBoolean("PlayerWarps.General.Force_Player_Head", false);
        this.customTeleportCosts = config.getBoolean("PlayerWarps.General.Custom_teleport_costs", true);
        this.timeStandardValue = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Standard_Value", "1h"));
        this.forceCreateGUI = config.getBoolean("PlayerWarps.General.Force_Create_GUI", false);
        this.allowPublicWarps = config.getBoolean("PlayerWarps.General.Allow_Public_Warps", true);
        this.allowTrustedMembers = config.getBoolean("PlayerWarps.General.Allow_Trusted_Members", true);

        //Costs - Editing
        this.nameChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Name", 400);
        this.positionChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Target_Position", 200);
        this.itemChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Personal_Item", 100);

        //Costs - Fields
        this.personalItemCosts = config.getDouble("PlayerWarps.Costs.Personal_Item", 200);
        this.messageCosts = config.getDouble("PlayerWarps.Costs.Text.Teleport_Message", 2);
        this.descriptionCosts = config.getDouble("PlayerWarps.Costs.Text.Warp_Description", 2);

        this.publicCosts = config.getDouble("PlayerWarps.Costs.PublicWarp", 100);
        this.activeTimeCosts = config.getDouble("PlayerWarps.Costs.Active_Time", 0.5);

        //Teleport costs
        this.teleportCosts = config.getDouble("PlayerWarps.Costs.Teleport_Fee", 25);
        this.maxTeleportCosts = config.getDouble("PlayerWarps.Teleport_Fee.Max", 500);

        //Teleport message
        this.messageMinLength = config.getInt("PlayerWarps.Teleport_Message.Length.Min", 5);
        this.messageMaxLength = config.getInt("PlayerWarps.Teleport_Message.Length.Max", 50);

        //Description
        this.descriptionLineMinLength = config.getInt("PlayerWarps.Warp_Description.Line_Length.Min", 5);
        this.descriptionLineMaxLength = config.getInt("PlayerWarps.Warp_Description.Line_Length.Max", 25);
        this.descriptionMaxLines = config.getInt("PlayerWarps.Warp_Description.Max_Lines", 3);

        //Name
        this.nameMinLength = config.getInt("PlayerWarps.Name_Length.Min", 3);
        this.nameMaxLength = config.getInt("PlayerWarps.Name_Length.Max", 20);

        //generally
        this.firstPublic = config.getBoolean("PlayerWarps.General.Public_as_create_state", false);
        this.trustedMemberCosts = config.getDouble("PlayerWarps.Costs.Trusted_Member", 50);

        //refund
        this.personalItemRefund = config.getDouble("PlayerWarps.Refunds.Personal_Item", 0.5);
        this.descriptionRefund = config.getDouble("PlayerWarps.Refunds.Warp_Description", 0.5);
        this.messageRefund = config.getDouble("PlayerWarps.Refunds.Teleport_Message", 0.5);
        this.publicRefund = config.getDouble("PlayerWarps.Refunds.PublicWarp", 0.5);
        this.teleportCostsRefund = config.getDouble("PlayerWarps.Refunds.Teleport_Fee", 0.5);
        this.activeTimeRefund = config.getDouble("PlayerWarps.Refunds.Active_Time", 1);
        this.trustedMemberRefund = config.getDouble("PlayerWarps.Refunds.Trusted_Member", 0.5);

        //Classes
        this.classes = config.getBoolean("PlayerWarps.General.Categories.Enabled", true);
        this.classesMin = config.getInt("PlayerWarps.General.Categories.Min", 1);
        this.classesMax = config.getInt("PlayerWarps.General.Categories.Max", 2);

        List<?> l = config.getList("PlayerWarps.General.Categories.Classes");
        if(l != null)
            for(Object o : l) {
                JSON json = new JSON((Map<Object, Object>) o);
                Category c = new Category();
                try {
                    c.read(json);
                    this.warpCategories.add(c);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        //loading PlayerWarps
        List<?> data = playerWarpsData.getConfig().getList("PlayerWarps");
        if(data != null)
            for(Object o : data) {
                JSON json = new JSON((Map<?, ?>) o);
                PlayerWarp p = new PlayerWarp();

                try {
                    p.read(json);
                    add(p);
                    size++;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        List<PlayerWarp> imported = TempWarpAdapter.convertTempWarps(true);
        for(PlayerWarp playerWarp : imported) {
            add(playerWarp);
        }

        new CPlayerWarp(config.getStringList("PlayerWarps.General.PlayerWarp_Command_Aliases")).register();
        new CPlayerWarps(config.getStringList("PlayerWarps.General.PlayerWarps_Command_Aliases")).register();

        List<String> aliases = config.getStringList("PlayerWarps.General.Command_References");
        if(!aliases.isEmpty()) new CPlayerWarpReference(aliases.remove(0), aliases.toArray(new String[0])).register();

        WarpSystem.log("    ...got " + warpCategories.size() + " Class(es)");
        if(!imported.isEmpty()) WarpSystem.log("    ...got " + imported.size() + " imported TempWarp(s)");
        imported.clear();

        if(!bungeeCord) WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        if(economy) API.addTicker(this);

        WarpSystem.getInstance().getBungeeFeatureList().add(this);
        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayerWarpPlaceholderExpansion().register();
        }

        return success;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");
        playerWarpsData.clearConfig();

        JSONArray a = null;
        if(!bungeeCord || !WarpSystem.getInstance().isOnBungeeCord()) {
            a = new JSONArray();

            for(List<PlayerWarp> data : this.warps.values()) {
                for(PlayerWarp w : data) {
                    JSON json = new JSON();
                    w.write(json);
                    a.add(json);
                }
            }
            playerWarpsData.getConfig().set("PlayerWarps", a);
        } else if(!saver) WarpSystem.log("    ...skipping PlayerWarp(s) > Saved on BungeeCord");

        if(warpCategories.isEmpty()) {
            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.EMERALD), "&a&lShop", 1, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &aShop&7!");
            }}));

            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.OAK_DOOR), "&c&lHome", 2, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &cHome&7!");
            }}));

            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.FARMLAND), "&9&lFarm", 3, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &9Farm&7!");
            }}));

            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.IRON_SWORD), "&e&lPvP-Zone", 4, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &ePvP-Zone&7!");
            }}));

            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.BOW), "&b&lHunting-Area", 5, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &bHunting-Area&7!");
            }}));

            this.warpCategories.add(new Category(new ItemBuilder(XMaterial.ENDER_EYE), "&3&lMiscellaneous", 6, new ArrayList<String>() {{
                add("&7This class marks a warp");
                add("&7as a &3miscellaneous &7warp!");
            }}));
        }

        JSONArray array = new JSONArray();
        for(Category c : this.warpCategories) {
            JSON json = new JSON();
            c.write(json);
            array.add(json);
        }

        config.loadConfig();
        ConfigWriter writer = new ConfigWriter(config);
        writer.put("PlayerWarps.General.Categories.Classes", array);
        config.saveConfig();

        playerWarpsData.saveConfig();
        if(!saver && a != null) WarpSystem.log("    ...saved " + a.size() + " PlayerWarp(s)");
    }

    @Override
    public void onConnect() {
        WarpSystem.getInstance().getDataHandler().register(listener);

        if(bungeeCord) {
            if(!getWarps().isEmpty()) {
                List<List<PlayerWarpData>> uploads = new ArrayList<>();

                List<PlayerWarpData> l = new ArrayList<>();
                for(List<PlayerWarp> value : getWarps().values()) {
                    for(PlayerWarp w : value) {
                        l.add(w.getData());

                        if(l.size() == 100) {
                            uploads.add(new ArrayList<>(l));
                            l.clear();
                        }
                    }
                }

                if(!l.isEmpty()) uploads.add(l);

                for(List<PlayerWarpData> upload : uploads) {
                    SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
                    p.setClearable(true);
                    WarpSystem.getInstance().getDataHandler().send(p);
                }

                uploads.clear();
            }

            WarpSystem.getInstance().getDataHandler().send(new RegisterServerForPlayerWarpsPacket(isEconomy()));
        } else WarpSystem.getInstance().getDataHandler().send(new MoveLocalPlayerWarpsPacket());
    }

    @Override
    public void onDisconnect() {
        if(bungeeCord) {
            for(List<PlayerWarp> value : this.warps.values()) {
                value.clear();
            }
            this.warps.clear();
        }

        WarpSystem.getInstance().getDataHandler().unregister(listener);
    }

    public boolean sync(PlayerWarp old, PlayerWarp warp) {
        if(!bungeeCord || !WarpSystem.getInstance().isOnBungeeCord()) return false;

        if(warp.isSource()) {
            warp.setSource(false);
            SendPlayerWarpsPacket packet = new SendPlayerWarpsPacket(new ArrayList<PlayerWarpData>() {{
                add(warp.getData());
            }});
            packet.setClearable(true);
            WarpSystem.getInstance().getDataHandler().send(packet);
            return true;
        } else return sync(old.getData(), warp.getData());
    }

    public boolean sync(PlayerWarpData old, PlayerWarpData warp) {
        if(!bungeeCord || !WarpSystem.getInstance().isOnBungeeCord()) return false;
        PlayerWarpUpdate update = warp.diff(old);

        if(update.isEmpty()) return false;

        WarpSystem.getInstance().getDataHandler().send(new SendPlayerWarpUpdatePacket(update));
        old.destroy();
        warp.destroy();
        return true;
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onSecond() {
        List<List<PlayerWarp>> mapCopy = new ArrayList<>(warps.values());
        for(List<PlayerWarp> value : mapCopy) {
            List<PlayerWarp> copy = new ArrayList<>(value);

            for(PlayerWarp warp : copy) {
                if(!warp.isTimeDependent() || warp.isBeingEdited()) continue;
                if(warp.isExpired()) {
                    if(-(warp.getExpireDate() - System.currentTimeMillis()) <= 1000) {
                        Player p = warp.getOwner().getPlayer();
                        if(p != null)
                            p.sendMessage(Lang.getPrefix() + Lang.get("Warp_expiring").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", StringFormatter.convertInTimeFormat(inactiveTime, 0, "", "")));
                        else
                            warp.setNotify(true);
                    }

                    for(Long remind : this.inactiveReminds) {
                        if(remind == inactiveTime) continue;

                        long time = -1000L * (inactiveTime - remind);
                        if(warp.getLeftTime() >= time - 1050L && warp.getLeftTime() < time) {
                            Player p = warp.getOwner().getPlayer();
                            if(p != null)
                                p.sendMessage(Lang.getPrefix() + Lang.get("Warp_Deletion_In").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", StringFormatter.convertInTimeFormat(remind, 0, "", "")));
                        }
                    }

                    Date inactive = new Date(warp.getExpireDate() + this.inactiveTime);

                    if(inactive.before(new Date())) {
                        //Delete
                        delete(warp, false);
                        warp.destroy();
                        Player player = warp.getOwner().getPlayer();
                        if(player != null) player.sendMessage(Lang.getPrefix() + Lang.get("Warp_was_deleted").replace("%NAME%", warp.getName()));
                    }
                }
            }

            copy.clear();
        }
        mapCopy.clear();
    }

    @Override
    public void destroy() {
        this.warps.clear();
        this.names.clear();
        this.warpCategories.clear();
    }

    public void checkPlayerWarpOwnerNames(Player player) {
        List<PlayerWarp> warps = new ArrayList<>(getOwnWarps(player));

        for(PlayerWarp warp : warps) {
            warp.getOwner().setName(player.getName());
        }

        warps.clear();
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

    public List<PlayerWarp> filter(List<Category> classes, Player toTeleport) {
        List<PlayerWarp> warps = getWarps(toTeleport, true);

        for(int i = 0; i < warps.size(); i++) {
            PlayerWarp pw = warps.get(i);
            if(pw.isExpired()) continue;

            List<Category> categories = pw.getClasses();
            boolean match = false;

            for(Category c : classes) {
                for(Category cat : categories) {
                    if(c.equals(cat)) {
                        match = true;
                        break;
                    }
                }
            }

            if(!match) {
                warps.remove(i);
                i--;
            }
        }

        return warps;
    }

    public void updateWarp(PlayerWarp warp) {
        PlayerWarp w = getWarp(warp.getOwner().getId(), warp.getName());

        if(w == null) add(warp);
        else w.apply(warp);
    }

    public List<PlayerWarp> getPublicWarps() {
        List<PlayerWarp> warps = new ArrayList<>();
        for(List<PlayerWarp> value : this.warps.values()) {
            for(PlayerWarp warp : value) {
                if(warp.isPublic()) warps.add(warp);
            }
        }

        return warps;
    }

    public HashMap<UUID, List<PlayerWarp>> getWarps() {
        return warps;
    }

    public void interactWithWarps(Callback<PlayerWarp> interact) {
        List<List<PlayerWarp>> values = new ArrayList<>(warps.values());
        for(List<PlayerWarp> value : values) {
            List<PlayerWarp> warps = new ArrayList<>(value);

            for(PlayerWarp warp : warps) {
                interact.accept(warp);
            }

            warps.clear();
        }
        values.clear();
    }

    public Set<UUID> getUUIDs() {
        return this.warps.keySet();
    }

    public List<PlayerWarp> getOwnWarps(Player player) {
        return getOwnWarps(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public List<PlayerWarp> getOwnWarps(UUID id) {
        List<PlayerWarp> l = warps.get(id);
        return l == null ? new ArrayList<>() : l;
    }

    public String checkSymbols(String name, String highlighter, String reset) {
        StringBuilder finalName = new StringBuilder();
        String modifiedName = name;
        String lowerName = modifiedName.toLowerCase();

        for(String s : this.nameBlacklist) {
            s = s.toLowerCase();

            int first, last = 0, matches = 0;
            while((first = lowerName.indexOf(s, last)) > -1) {
                last = first + 1;

                StringBuilder builder = new StringBuilder();
                int modFirst = first + matches * (highlighter.length() + reset.length());
                for(int i = 0; i < modifiedName.toCharArray().length; i++) {
                    if(i == modFirst) builder.append(highlighter);
                    builder.append(modifiedName.charAt(i));
                    if(i == modFirst + s.length() - 1) builder.append(reset);
                }

                modifiedName = builder.toString();
                matches++;
            }
        }

        Pattern p = Pattern.compile("[A-Za-z0-9\\p{Blank}_\\-'§]*");

        for(char c : modifiedName.toCharArray()) {
            Matcher m = p.matcher(c + "");
            if(!m.matches()) {
                finalName.append(highlighter).append(c).append(reset);
            } else finalName.append(c);
        }

        return finalName.toString().equals(name) ? null : finalName.toString();
    }

    /**
     * @param id         Owner of warps
     * @param toTeleport Player, who wants to see the warps
     * @return A list with usable PlayerWarps of id
     */
    public List<PlayerWarp> getUsableWarpsOf(UUID id, Player toTeleport) {
        List<PlayerWarp> warps = new ArrayList<>();

        for(PlayerWarp warp : getOwnWarps(id)) {
            if(warp.isExpired()) continue;
            if(warp.isOwner(toTeleport) || (allowPublicWarps && warp.isPublic()) || (allowTrustedMembers && warp.isTrusted(toTeleport))) warps.add(warp);
        }

        return warps;
    }

    public int getTrustedWarpAmountOf(UUID id, Player trustedPlayer) {
        int i = 0;

        for(PlayerWarp warp : getOwnWarps(id)) {
            if(warp.isOwner(trustedPlayer) || (allowPublicWarps && warp.isPublic()) || (allowTrustedMembers && warp.isTrusted(trustedPlayer))) i++;
        }

        return i;
    }

    public List<PlayerWarp> getWarps(Player player, boolean trusted) {
        if(!trusted) return getOwnWarps(player);
        List<PlayerWarp> warps = new ArrayList<>();

        for(List<PlayerWarp> ws : this.warps.values()) {
            for(PlayerWarp warp : ws) {
                if(warp.isOwner(player) || (allowPublicWarps && warp.isPublic()) || (allowTrustedMembers && warp.isTrusted(player))) warps.add(warp);
            }
        }

        return warps;
    }

    public List<PlayerWarp> getForeignAvailableWarps(Player player) {
        List<PlayerWarp> warps = new ArrayList<>();

        for(List<PlayerWarp> ws : this.warps.values()) {
            for(PlayerWarp warp : ws) {
                if(warp.isExpired()) continue;
                if(warp.isOwner(player) || (allowPublicWarps && warp.isPublic()) || (allowTrustedMembers && warp.isTrusted(player))) warps.add(warp);
            }
        }

        return warps;
    }

    public PlayerWarp getWarp(Player player, String name) {
        return getWarp(player, name, null);
    }

    //<player>.<name>
    //<name> « private warps haben vorrang
    public PlayerWarp getWarp(Player player, String name, PlayerWarp except) {
        name = name.replace(" ", "_");

        String[] a = name.split("\\.", -1);
        if(a.length > 2) {
            return null;
        }

        String prefer = a.length == 2 ? a[0] : null;
        name = a[a.length - 1];

        PlayerWarp searched = null;
        PlayerWarp pWarp = null; //private warp

        if(prefer == null) {
            List<PlayerWarp> warps = this.warps.get(WarpSystem.getInstance().getUUIDManager().get(player));

            if(warps != null) {
                warps = new ArrayList<>(warps);

                for(PlayerWarp warp : warps) {
                    if(warp.equals(except)) continue;
                    if(warp.equalsName(name)) {
                        searched = warp;
                        break;
                    }
                }

                warps.clear();
                if(searched != null) return searched;
            }

            List<List<PlayerWarp>> lists = new ArrayList<>(this.warps.values());
            for(List<PlayerWarp> list : lists) {
                warps = new ArrayList<>(list);

                for(PlayerWarp warp : warps) {
                    if(warp.equals(except)) continue;
                    if(warp.equalsName(name)) {
                        if(warp.canTeleport(player)) {
                            searched = warp;
                            break;
                        } else pWarp = warp;
                    }
                }

                warps.clear();
            }
            lists.clear();
        } else {
            UUID id = names.get(prefer);
            if(id != null) {
                List<PlayerWarp> warps = this.warps.get(id);

                if(warps != null) {
                    warps = new ArrayList<>(warps);

                    for(PlayerWarp warp : warps) {
                        if(warp.equals(except)) continue;
                        if(warp.equalsName(name)) {
                            searched = warp;
                            break;
                        }
                    }

                    warps.clear();
                }
            }
        }

        return searched == null ? pWarp : searched;
    }

    public PlayerWarp getWarp(UUID id, String name) {
        for(PlayerWarp w : getOwnWarps(id)) {
            if(w.equalsName(name)) return w;
        }

        return null;
    }

    public boolean exists(Player player, String name) {
        return exists(player, name, null);
    }

    public boolean existsOwn(Player player, String name) {
        return existsOwn(player, name, null);
    }

    public void add(PlayerWarp warp) {
        List<PlayerWarp> warps = getOwnWarps(warp.getOwner().getId());
        if(getWarp(warp.getOwner().getId(), warp.getName()) != null) return;

        warp.setName(getCopiedName(warps, warp.getName()));
        warps.add(warp);

        if(warp.getStarted() == 0) {
            warp.setStarted(System.currentTimeMillis());
            warp.born();
        }

        names.putIfAbsent(warp.getOwner().getName(), warp.getOwner().getId());
        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
    }

    private String getCopiedName(List<PlayerWarp> list, String name) {
        int num = 0;
        boolean found;

        name = name.replace(" ", "_");

        do {
            found = false;
            if(num == 0) num++;
            else {
                name = name.replaceAll("_\\([0-9]{1,5}?\\)\\z", "");
                name += "_(" + num++ + ")";
            }

            for(PlayerWarp d : list) {
                String nameWithoutColor = net.md_5.bungee.api.ChatColor.stripColor(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', name));
                String dName = net.md_5.bungee.api.ChatColor.stripColor(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', d.getName())).replace(" ", "_");
                if(dName.equalsIgnoreCase(nameWithoutColor)) {
                    found = true;
                    break;
                }
            }
        } while(found);

        return name;
    }

    public double delete(PlayerWarp warp, boolean informBungee) {
        if(warp == null) return 0;
        List<PlayerWarp> warps = getOwnWarps(warp.getOwner().getId());
        double refund = warps.remove(warp) ? calculateRefund(warp) : -1;

        if(warps.isEmpty()) {
            this.warps.remove(warp.getOwner().getId());
            this.names.remove(warp.getOwner().getName());
        }

        if(informBungee && checkBungeeCord()) {
            DeletePlayerWarpPacket packet = new DeletePlayerWarpPacket(warp.getName(), warp.getOwner().getId());
            WarpSystem.getInstance().getDataHandler().send(packet);
        }

        return refund;
    }

    public void updateGUIs() {
        for(PWList gui : API.getRemovables(PWList.class)) {
            gui.updateList();
        }
    }

    public double calculateRefund(PlayerWarp warp) {
        double refund = 0;
        if(warp == null) return -1;
        if(!isEconomy()) return 0;

        //personal item
        if(!warp.isStandardItem()) refund += PlayerWarpManager.getManager().getItemCosts() * PlayerWarpManager.getManager().getPersonalItemRefund() * warp.getRefundFactor();

        //description
        int length = 0;

        if(warp.getItem().getLore() != null)
            for(String s : warp.getItem().getLore()) {
                length += s.replaceFirst("§f", "").length();
            }

        if(length > 0) refund += length * PlayerWarpManager.getManager().getDescriptionCosts() * PlayerWarpManager.getManager().getDescriptionRefund() * warp.getRefundFactor();

        //teleport message
        length = warp.getTeleportMessage() == null ? 0 : warp.getTeleportMessage().length();

        if(length > 0) refund += length * PlayerWarpManager.getManager().getMessageCosts() * PlayerWarpManager.getManager().getMessageRefund() * warp.getRefundFactor();

        //public state
        if(warp.isPublic()) refund += PlayerWarpManager.getManager().getPublicCosts() * PlayerWarpManager.getManager().getPublicRefund() * warp.getRefundFactor();

        //teleport costs
        double tpCosts = warp.getTeleportCosts();
        if(tpCosts > 0) refund += tpCosts * PlayerWarpManager.getManager().getTeleportCosts() * PlayerWarpManager.getManager().getTeleportCostsRefund() * warp.getRefundFactor();

        //active time
        refund += (warp.getLeftTime() / 60000D) * PlayerWarpManager.getManager().getActiveTimeCosts() * PlayerWarpManager.getManager().getActiveTimeRefund() * warp.getRefundFactor();

        //trusted members
        length = warp.getTrusted().size();
        if(length > 0) refund += length * PlayerWarpManager.getManager().getTrustedMemberCosts() * PlayerWarpManager.getManager().getTrustedMemberRefund() * warp.getRefundFactor();

        if(isNaturalNumbers()) return Math.ceil(refund);
        return refund;
    }

    public boolean exists(Player player, String name, PlayerWarp except) {
        return getWarp(player, name, except) != null;
    }

    public boolean existsOwn(Player player, String name, PlayerWarp except) {
        String[] a = name.split("\\.", -1);
        return exists(player, player.getName() + "." + a[a.length - 1], except);
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public double getMaxTeleportCosts() {
        return maxTeleportCosts;
    }

    public double getTeleportCosts() {
        return teleportCosts;
    }

    public double getNameChangeCosts() {
        return nameChangeCosts;
    }

    public boolean isFirstPublic() {
        return firstPublic;
    }

    public double getPublicCosts() {
        return publicCosts;
    }

    public double getMessageCosts() {
        return messageCosts;
    }

    public double getItemCosts() {
        return personalItemCosts;
    }

    public double getDescriptionCosts() {
        return descriptionCosts;
    }

    public double getPositionChangeCosts() {
        return positionChangeCosts;
    }

    public double getActiveTimeCosts() {
        return activeTimeCosts;
    }

    public double getItemChangeCosts() {
        return itemChangeCosts;
    }

    public int getMessageMinLength() {
        return messageMinLength;
    }

    public int getMessageMaxLength() {
        return messageMaxLength;
    }

    public int getDescriptionLineMinLength() {
        return descriptionLineMinLength;
    }

    public int getDescriptionLineMaxLength() {
        return descriptionLineMaxLength;
    }

    public int getDescriptionMaxLines() {
        return descriptionMaxLines;
    }

    public double getTrustedMemberCosts() {
        return trustedMemberCosts;
    }

    public double getPersonalItemRefund() {
        return personalItemRefund;
    }

    public double getDescriptionRefund() {
        return descriptionRefund;
    }

    public double getMessageRefund() {
        return messageRefund;
    }

    public double getPublicRefund() {
        return publicRefund;
    }

    public double getTeleportCostsRefund() {
        return teleportCostsRefund;
    }

    public double getActiveTimeRefund() {
        return activeTimeRefund;
    }

    public double getTrustedMemberRefund() {
        return trustedMemberRefund;
    }

    public double getCreateCosts() {
        return createCosts;
    }

    public double getEditCosts() {
        return editCosts;
    }

    public boolean isNaturalNumbers() {
        return naturalNumbers;
    }

    public boolean isInternalRefundFactor() {
        return internalRefundFactor;
    }

    public boolean isEconomy() {
        return economy;
    }

    public boolean isForcePlayerHead() {
        return forcePlayerHead;
    }

    public List<Category> getWarpClasses() {
        return warpCategories;
    }

    public Category getWarpClass(String name) {
        for(Category c : this.warpCategories) {
            if(ChatColor.stripColor(c.getName()).equals(ChatColor.stripColor(name))) return c;
        }

        return null;
    }

    public Category getWarpClass(int id) {
        for(Category c : this.warpCategories) {
            if(c.getId() == id) return c;
        }

        return null;
    }

    public boolean isCustomTeleportCosts() {
        return customTeleportCosts;
    }

    public int getClassesMin() {
        return classesMin;
    }

    public int getClassesMax() {
        return classesMax;
    }

    public boolean isClasses() {
        return classes;
    }

    public boolean isBungeeCord() {
        return bungeeCord;
    }

    public boolean checkBungeeCord() {
        return isBungeeCord() && WarpSystem.getInstance().isOnBungeeCord();
    }

    public long getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(long inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public boolean isProtectedRegions() {
        return protectedRegions;
    }

    public List<String> getNameBlacklist() {
        return nameBlacklist;
    }

    public int getNameMinLength() {
        return nameMinLength;
    }

    public int getNameMaxLength() {
        return nameMaxLength;
    }

    public long getTimeStandardValue() {
        return isEconomy() ? timeStandardValue : 0;
    }

    public boolean isForceCreateGUI() {
        return forceCreateGUI;
    }

    public boolean isAllowPublicWarps() {
        return allowPublicWarps;
    }

    public boolean isAllowTrustedMembers() {
        return allowTrustedMembers;
    }

    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }
}
