package de.codingair.warpsystem.spigot.features.playerwarps.managers;

import de.codingair.codingapi.API;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.yml.ConfigWriter;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Ticker;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.Category;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GCreate;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GDelete;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GTempWarpList;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.json.simple.JSONArray;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerWarpManager implements Manager, Ticker {
    public static boolean hasPermission(Player player) {
        if(player.isOp()) return true;

        int warps = PlayerWarpManager.getManager().getWarps(player).size() + 1;
        for(PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String perm = effectivePermission.getPermission();

            if(perm.equals("*") || perm.equalsIgnoreCase("warpsystem.*")) return true;
            if(perm.toLowerCase().startsWith("warpsystem.playerwarps.")) {
                String s = perm.substring(24);
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
    private List<Category> warpCategories = new ArrayList<>();

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
    private int classesMin;
    private int classesMax;
    private boolean classes;

    @Override
    public boolean load() {
        boolean success = true;

        this.warps.clear();
        this.warpCategories.clear();

        if(WarpSystem.getInstance().getFileManager().getFile("PlayerWarps") == null) WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/Memory/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        WarpSystem.log("  > Loading PlayerWarps");
        int size = 0;

        ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("Config");
        FileConfiguration config = configFile.getConfig();

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
        this.createCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Create", 200);
        this.editCosts = config.getDouble("WarpSystem.PlayerWarps.Costs.Edit", 200);
        this.naturalNumbers = config.getBoolean("WarpSystem.PlayerWarps.Costs.Round_costs_to_natural_numbers", false);
        this.internalRefundFactor = config.getBoolean("WarpSystem.PlayerWarps.Costs.Internal_Refund_Factor", false);
        this.economy = config.getBoolean("WarpSystem.PlayerWarps.General.Economy", true);
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

        //generally
        this.firstPublic = config.getBoolean("WarpSystem.PlayerWarps.Public_as_create_state", false);
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
        this.classes = config.getBoolean("WarpSystem.PlayerWarps.Categories.Enabled", true);
        this.classesMin = config.getInt("WarpSystem.PlayerWarps.Categories.Min", 1);
        this.classesMax = config.getInt("WarpSystem.PlayerWarps.Categories.Max", 2);

        List<?> l = config.getList("WarpSystem.PlayerWarps.General.Categories.Classes");
        if(l != null)
            for(Object o : l) {
                JSON json = new JSON((Map<Object, Object>) o);
                for(Object key : json.keySet(false)) {
                    Category c = json.getSerializable((String) key, new Category());
                    if(c != null) this.warpCategories.add(c);
                }
            }

        //loading PlayerWarps
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

        WarpSystem.log("    ...got " + warpCategories.size() + " Class(es)");
        WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        if(isEconomy()) API.addTicker(this);

        return success;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving PlayerWarps...");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        int entries = 0;

        file.clearConfig();

        for(List<PlayerWarp> data : this.warps.values()) {
            for(PlayerWarp w : data) {
                ConfigWriter writer = new ConfigWriter(file, w.getName(false));
                w.write(writer);
            }

            entries += data.size();
        }

        JSONArray array = new JSONArray();
        for(Category c : this.warpCategories) {
            JSON json = new JSON();
            json.put(ChatColor.stripColor(c.getName()), c);
            array.add(json);
        }

        ConfigFile config = WarpSystem.getInstance().getFileManager().getFile("Config");
        ConfigWriter writer = new ConfigWriter(config);
        writer.put("WarpSystem.PlayerWarps.General.Categories.Classes", array);
        config.saveConfig();

        file.saveConfig();
        if(!saver) WarpSystem.log("    ...saved " + entries + " PlayerWarp(s)");
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

        for(List<PlayerWarp> value : warps.values()) {
            for(PlayerWarp warp : value) {
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
                        delete(warp);
                        Player player = warp.getOwner().getPlayer();
                        if(player != null) {
                            GTempWarpList list = API.getRemovable(player, GTempWarpList.class);
                            if(list != null) list.reinitialize();

                            GUI gui = API.getRemovable(player, GCreate.class);
                            if(gui != null) gui.close();
                            gui = API.getRemovable(player, GDelete.class);
                            if(gui != null) gui.close();

                            player.sendMessage(Lang.getPrefix() + Lang.get("Warp_was_deleted").replace("%NAME%", warp.getName()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object getInstance() {
        return this;
    }

    @Override
    public void destroy() {

    }

    public static String convertInTimeFormat(long time) {
        return convertInTimeFormat(time, 0, "", "");
    }

    public static String convertInTimeFormat(long time, int highlight, String highlighter, String reset) {
        long days = 0, hours = 0, min = 0, sec = 0;

        if(time > 0) {
            time += 500;
            days = Math.max(TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
            hours = Math.max(TimeUnit.HOURS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
            min = Math.max(TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(min, TimeUnit.MINUTES);
            sec = Math.max(TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS), 0);
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

        if((days + hours + min + sec == 0) || highlight == 5 || (highlight < 10 && sec > 0)) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(sec).append("s");
        }

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

    public List<PlayerWarp> getPublicWarps() {
        List<PlayerWarp> warps = new ArrayList<>();
        for(List<PlayerWarp> value : this.warps.values()) {
            for(PlayerWarp warp : value) {
                if(warp.isPublic()) warps.add(warp);
            }
        }

        return warps;
    }

    public Set<UUID> getUUIDs() {
        return this.warps.keySet();
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

        if(warp.getStarted() == 0) {
            warp.setStarted(System.currentTimeMillis());
            warp.born();
        }

        this.warps.putIfAbsent(warp.getOwner().getId(), warps);
    }

    public double delete(PlayerWarp warp) {
        List<PlayerWarp> warps = getWarps(warp.getOwner().getId());
        double refund = warps.remove(warp) ? calculateRefund(warp) : -1;

        if(warps.isEmpty()) this.warps.remove(warp.getOwner().getId());

        return refund;
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

        return refund;
    }

    public boolean exists(String name, PlayerWarp except) {
        return getWarp(name, except) != null;
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
}
