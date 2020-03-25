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
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarpReference;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.listeners.PlayerWarpListener;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.*;
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerWarpManager implements Manager, Ticker, BungeeFeature {
    public static boolean hasPermission(Player player) {
        if(player.isOp()) return true;

        int warps = PlayerWarpManager.getManager().getOwnWarps(player).size() + 1;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.equals("*") || perm.equalsIgnoreCase("warpsystem.*")) return true;
            if(perm.toLowerCase().startsWith("warpsystem.playerwarps.")) {
                String s = perm.substring(23);
                if(s.equals("*") || s.equalsIgnoreCase("n")) return true;

                try {
                    int amount = Integer.parseInt(s);
                    if(amount >= warps) return true;
                } catch(Throwable ignored) {
                }
            }
        }

        return false;
    }

    private HashMap<UUID, List<PlayerWarp>> warps = new HashMap<>();
    private HashMap<String, UUID> names = new HashMap<>();
    private List<Category> warpCategories = new ArrayList<>();
    private List<String> nameBlacklist = new ArrayList<>();

    private boolean bungeeCord;
    private PlayerWarpListener listener = new PlayerWarpListener();

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

    @Override
    public boolean load(boolean loader) {
        boolean success = true;

        this.warps.clear();
        this.warpCategories.clear();
        this.nameBlacklist.clear();

        if(WarpSystem.getInstance().getFileManager().getFile("PlayerWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/Memory/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        int size = 0;

        ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("Config");
        FileConfiguration config = configFile.getConfig();

        this.bungeeCord = config.getBoolean("WarpSystem.PlayerWarps.General.BungeeCord", true);
        this.economy = config.getBoolean("WarpSystem.PlayerWarps.General.Economy", true);
        WarpSystem.log("  > Loading PlayerWarps [Bungee: " + bungeeCord + "; TimeDependent: " + economy + "]");

        // Timings
        this.minTime = convertFromTimeFormat(config.getString("WarpSystem.PlayerWarps.Time.Min_Time", null), 300000);
        this.maxTime = convertFromTimeFormat(config.getString("WarpSystem.PlayerWarps.Time.Max_Time", null), 2592000000L);

        List<String> reminds = config.getStringList("WarpSystem.PlayerWarps.Inactive.Reminds");
        this.inactiveReminds = new ArrayList<>();

        for(int i = 0; i < reminds.size(); i++) {
            String data = i < reminds.size() ? reminds.get(i) : null;

            long time = convertFromTimeFormat(data);
            if(time > 0) inactiveReminds.add(time);
        }

        this.inactiveTime = convertFromTimeFormat(config.getString("WarpSystem.PlayerWarps.Inactive.Time_After_Expiration", null), 2592000000L);

        //Costs - Generally
        this.protectedRegions = config.getBoolean("WarpSystem.PlayerWarps.General.Support.ProtectedRegions", true);
        this.nameBlacklist.addAll(config.getStringList("WarpSystem.PlayerWarps.General.Name_Blacklist"));
        this.createCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Create", 200);
        this.editCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Edit", 200);
        this.naturalNumbers = config.getBoolean("WarpSystem.PlayerWarps.Costs.Round_costs_to_natural_numbers", false);
        this.internalRefundFactor = config.getBoolean("WarpSystem.PlayerWarps.Costs.Internal_Refund_Factor", false);
        this.forcePlayerHead = config.getBoolean("WarpSystem.PlayerWarps.General.Force_Player_Head", false);
        this.customTeleportCosts = config.getBoolean("WarpSystem.PlayerWarps.General.Custom_teleport_costs", true);

        //Costs - Editing
        this.nameChangeCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Editing.Name", 400);
        this.positionChangeCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Editing.Target_Position", 200);
        this.itemChangeCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Editing.Personal_Item", 100);

        //Costs - Fields
        this.personalItemCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Personal_Item", 200);
        this.messageCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Text.Teleport_Message", 2);
        this.descriptionCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Text.Warp_Description", 2);

        this.publicCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.PublicWarp", 100);
        this.activeTimeCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Active_Time", 0.5);

        //Teleport costs
        this.teleportCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Teleport_Fee", 25);
        this.maxTeleportCosts = config.getDouble("WarpSystem.PlayerWarps.Teleport_Fee.Max", 500);

        //Teleport message
        this.messageMinLength = config.getInt("WarpSystem.PlayerWarps.Teleport_Message.Length.Min", 5);
        this.messageMaxLength = config.getInt("WarpSystem.PlayerWarps.Teleport_Message.Length.Max", 50);

        //Description
        this.descriptionLineMinLength = config.getInt("WarpSystem.PlayerWarps.Warp_Description.Line_Length.Min", 5);
        this.descriptionLineMaxLength = config.getInt("WarpSystem.PlayerWarps.Warp_Description.Line_Length.Max", 25);
        this.descriptionMaxLines = config.getInt("WarpSystem.PlayerWarps.Warp_Description.Max_Lines", 3);

        //Name
        this.nameMinLength = config.getInt("WarpSystem.PlayerWarps.Name_Length.Min", 3);
        this.nameMaxLength = config.getInt("WarpSystem.PlayerWarps.Name_Length.Max", 20);

        //generally
        this.firstPublic = config.getBoolean("WarpSystem.PlayerWarps.General.Public_as_create_state", false);
        this.trustedMemberCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Trusted_Member", 50);

        //refund
        this.personalItemRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Personal_Item", 0.5);
        this.descriptionRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Warp_Description", 0.5);
        this.messageRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Teleport_Message", 0.5);
        this.publicRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.PublicWarp", 0.5);
        this.teleportCostsRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Teleport_Fee", 0.5);
        this.activeTimeRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Active_Time", 1);
        this.trustedMemberRefund = config.getDouble("WarpSystem.PlayerWarps.Refunds.Trusted_Member", 0.5);

        //Classes
        this.classes = config.getBoolean("WarpSystem.PlayerWarps.General.Categories.Enabled", true);
        this.classesMin = config.getInt("WarpSystem.PlayerWarps.General.Categories.Min", 1);
        this.classesMax = config.getInt("WarpSystem.PlayerWarps.General.Categories.Max", 2);

        List<?> l = config.getList("WarpSystem.PlayerWarps.General.Categories.Classes");
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
        List<?> data = file.getConfig().getList("PlayerWarps");
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

        new CPlayerWarp().register(WarpSystem.getInstance());
        new CPlayerWarps().register(WarpSystem.getInstance());

        List<String> aliases = config.getStringList("WarpSystem.PlayerWarps.General.Command_References");
        if(!aliases.isEmpty()) new CPlayerWarpReference(aliases.remove(0), aliases.toArray(new String[0])).register(WarpSystem.getInstance());

        WarpSystem.log("    ...got " + warpCategories.size() + " Class(es)");
        if(!imported.isEmpty()) WarpSystem.log("    ...got " + imported.size() + " imported TempWarp(s)");
        imported.clear();

        if(!bungeeCord) WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        if(isEconomy()) API.addTicker(this);

        WarpSystem.getInstance().getBungeeFeatureList().add(this);
        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());

        return success;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        file.clearConfig();

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
            file.getConfig().set("PlayerWarps", a);
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

        ConfigFile config = WarpSystem.getInstance().getFileManager().getFile("Config");
        ConfigWriter writer = new ConfigWriter(config);
        writer.put("WarpSystem.PlayerWarps.General.Categories.Classes", array);
        config.saveConfig();

        file.saveConfig();
        if(!saver && a != null) WarpSystem.log("    ...saved " + a.size() + " PlayerWarp(s)");
    }

    @Override
    public void onConnect() {
        WarpSystem.getInstance().getDataHandler().register(listener);

        if(bungeeCord) {
            if(!getWarps().isEmpty()) {
                List<PlayerWarpData> l = new ArrayList<>();

                for(List<PlayerWarp> value : getWarps().values()) {
                    for(PlayerWarp w : value) {
                        l.add(w.getData());
                    }
                }

                SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(l);
                p.setClearable(true);
                WarpSystem.getInstance().getDataHandler().send(p);
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
        List<PWEditor> l = API.getRemovables(PWEditor.class);

        for(PWEditor pwEditor : l) {
            pwEditor.updateTime();
        }

        l.clear();

        List<List<PlayerWarp>> mapCopy = new ArrayList<>(warps.values());
        for(List<PlayerWarp> value : mapCopy) {
            List<PlayerWarp> copy = new ArrayList<>(value);

            for(PlayerWarp warp : copy) {
                if(warp.isBeingEdited()) continue;
                if(warp.isExpired()) {
                    if(-(warp.getExpireDate() - System.currentTimeMillis()) <= 1000) {
                        Player p = warp.getOwner().getPlayer();
                        if(p != null)
                            p.sendMessage(Lang.getPrefix() + Lang.get("Warp_expiring").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", convertInTimeFormat(inactiveTime, 0, "", "")));
                        else
                            warp.setNotify(true);
                    }

                    for(Long remind : this.inactiveReminds) {
                        if(remind == inactiveTime) continue;

                        long time = -1000L * (inactiveTime - remind);
                        if(warp.getLeftTime() >= time - 1050L && warp.getLeftTime() < time) {
                            Player p = warp.getOwner().getPlayer();
                            if(p != null)
                                p.sendMessage(Lang.getPrefix() + Lang.get("Warp_Deletion_In").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", convertInTimeFormat(remind, 0, "", "")));
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
    public Object getInstance() {
        return this;
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

    public static String convertInTimeFormat(long time) {
        return convertInTimeFormat(time, 0, "", "");
    }

    public static String convertInTimeFormat(long time, int highlight, String highlighter, String reset) {
        long days = 0, hours = 0, min = 0, sec = 0;

        if(time > 0) {
            days = Math.max(TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
            hours = Math.max(TimeUnit.HOURS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
            min = Math.max(TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(min, TimeUnit.MINUTES);
            sec = Math.max(TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(sec, TimeUnit.SECONDS);
        }

        StringBuilder builder = new StringBuilder();

        if(days > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 1) builder.append(highlighter).append("»");
            builder.append(days).append("d");
            if(highlight == 1) builder.append(highlighter).append("«").append(reset);
        }

        if(hours > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 2) builder.append(highlighter).append("»");
            builder.append(hours).append("h");
            if(highlight == 2) builder.append(highlighter).append("«").append(reset);
        }

        if(min > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 3) builder.append(highlighter).append("»");
            builder.append(min).append("m");
            if(highlight == 3) builder.append(highlighter).append("«").append(reset);
        }

        if((days + hours + min + sec == 0) || highlight == 5 || sec > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(sec).append("s");
        }

//        if(!builder.toString().isEmpty()) builder.append(", ");
//        builder.append(time).append("ms");

        return builder.toString();
    }

    private long convertFromTimeFormat(String s, long def) {
        if(s == null || (!s.contains("d") && !s.contains("h") && !s.contains("m") && !s.contains("s"))) return def;

        try {
            return convertFromTimeFormat(s);
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    public static long convertFromTimeFormat(String text) throws NumberFormatException {
        long d = 0, h = 0, m = 0, s = 0;

        text = text.trim().toLowerCase();

        if(text.contains("d")) {
            String[] a = text.split("d")[0].split(" ");
            d = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("h")) {
            String[] a = text.split("h")[0].split(" ");
            h = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("m")) {
            String[] a = text.split("m")[0].split(" ");
            m = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("s")) {
            String[] a = text.split("s")[0].split(" ");
            s = Long.parseLong(a[a.length - 1]);
        }

        return TimeUnit.MILLISECONDS.convert(d, TimeUnit.DAYS) + TimeUnit.MILLISECONDS.convert(h, TimeUnit.HOURS) + TimeUnit.MILLISECONDS.convert(m, TimeUnit.MINUTES) + TimeUnit.MILLISECONDS.convert(s, TimeUnit.SECONDS);
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
        List l = warps.get(id);
        return l == null ? new ArrayList<>() : l;
    }

    private void addModifiedBlacklistNames(List<String> blacklist) {
        int size = blacklist.size();
        for(int i = 0; i < size; i++) {
            String s = blacklist.get(i);

            StringBuilder mod = new StringBuilder();
            for(char c : s.toLowerCase().toCharArray()) {
                if(c == 'a') mod.append(4);
                else mod.append(c);
            }
        }
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

        Pattern p = Pattern.compile("[A-Za-z0-9\\p{Blank}]*");

        for(char c : modifiedName.toCharArray()) {
            if(c == '§') {
                finalName.append(c);
                continue;
            }

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
            if(warp.canTeleport(toTeleport)) warps.add(warp);
        }

        return warps;
    }

    public int getTrustedWarpAmountOf(UUID id, Player trustedPlayer) {
        int i = 0;

        for(PlayerWarp warp : getOwnWarps(id)) {
            if(warp.canTeleport(trustedPlayer)) i++;
        }

        return i;
    }

    public List<PlayerWarp> getWarps(Player player, boolean trusted) {
        if(!trusted) return getOwnWarps(player);
        List<PlayerWarp> warps = new ArrayList<>();

        for(List<PlayerWarp> ws : this.warps.values()) {
            for(PlayerWarp warp : ws) {
                if(warp.canTeleport(player)) warps.add(warp);
            }
        }

        return warps;
    }

    public List<PlayerWarp> getForeignAvailableWarps(Player player) {
        List<PlayerWarp> warps = new ArrayList<>();

        for(List<PlayerWarp> ws : this.warps.values()) {
            for(PlayerWarp warp : ws) {
                if(player.equals(warp.getOwner().getPlayer())) continue;
                if(warp.canTeleport(player)) warps.add(warp);
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
        warps.add(warp);

        if(warp.getStarted() == 0) {
            warp.setStarted(System.currentTimeMillis());
            warp.born();
        }

        names.putIfAbsent(warp.getOwner().getName(), warp.getOwner().getId());
        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
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
        List<PWList> guis = API.getRemovables(PWList.class);

        for(PWList gui : guis) {
            gui.updateList();
        }

        guis.clear();
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

    public static PlayerWarpManager getManager() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARS);
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

    public static boolean isProtected(Player player) {
        if(!getManager().isProtectedRegions()) return false;

        PermissionPlayer check = new PermissionPlayer(player);
        BlockBreakEvent event = new BlockBreakEvent(player.getLocation().getBlock(), check);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
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
}
