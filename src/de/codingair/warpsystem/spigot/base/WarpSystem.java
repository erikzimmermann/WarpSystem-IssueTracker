package de.codingair.warpsystem.spigot.base;

import de.codingair.codingapi.API;
import de.codingair.codingapi.bungeecord.BungeeCordHelper;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.FileManager;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.fancymessages.FancyMessage;
import de.codingair.codingapi.server.fancymessages.MessageTypes;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.time.TimeFetcher;
import de.codingair.codingapi.time.Timer;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.commands.CWarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.*;
import de.codingair.warpsystem.spigot.base.managers.*;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.UpdateNotifier;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.PortalOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpGUIOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpSignOptions;
import de.codingair.warpsystem.transfer.packets.spigot.RequestInitialPacket;
import de.codingair.warpsystem.transfer.spigot.SpigotDataHandler;
import de.codingair.warpsystem.utils.Manager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WarpSystem extends JavaPlugin {
    public static final int PREMIUM_THREAD_ID = 369986;
    public static final int FREE_THREAD_ID = 182037;

    public static final String PERMISSION_NOTIFY = "warpsystem.notify";
    public static final String PERMISSION_MODIFY = "warpsystem.modify";

    public static final String PERMISSION_MODIFY_WARP_GUI = "warpsystem.modify.warpgui";
    public static final String PERMISSION_MODIFY_SHORTCUTS = "warpsystem.modify.shortcuts";
    public static final String PERMISSION_MODIFY_WARP_SIGNS = "warpsystem.modify.warpsigns";
    public static final String PERMISSION_MODIFY_GLOBAL_WARPS = "warpsystem.modify.globalwarps";
    public static final String PERMISSION_MODIFY_SIMPLE_WARPS = "warpsystem.modify.simplewarps";
    public static final String PERMISSION_MODIFY_PORTALS = "warpsystem.modify.portals";
    public static final String PERMISSION_MODIFY_RANDOM_TELEPORTER = "warpsystem.modify.randomteleporters";
    public static final String PERMISSION_MODIFY_PLAYER_WARPS = "warpsystem.modify.playerwarps";
    public static final String PERMISSION_MODIFY_SPAWN = "warpsystem.modify.spawn";

    public static final String PERMISSION_USE_TELEPORT_COMMAND = "warpsystem.use.teleportCommand";
    public static final String PERMISSION_USE_TELEPORT_COMMAND_TP = PERMISSION_USE_TELEPORT_COMMAND + ".tp";
    public static final String PERMISSION_USE_TELEPORT_COMMAND_TP_TOGGLE = PERMISSION_USE_TELEPORT_COMMAND + ".tptoggle";
    public static final String PERMISSION_USE_TELEPORT_COMMAND_TPALL = PERMISSION_USE_TELEPORT_COMMAND + ".tpall";
    public static final String PERMISSION_USE_TELEPORT_COMMAND_TPA_ALL = PERMISSION_USE_TELEPORT_COMMAND + ".tpaall";
    public static String PERMISSION_USE_TELEPORT_COMMAND_BACK = PERMISSION_USE_TELEPORT_COMMAND + ".back";
    public static String PERMISSION_USE_TELEPORT_COMMAND_BACK_DETECT_DEATHS = PERMISSION_USE_TELEPORT_COMMAND_BACK + ".deaths";
    public static String PERMISSION_USE_TELEPORT_COMMAND_TPA = PERMISSION_USE_TELEPORT_COMMAND + ".tpa";
    public static String PERMISSION_USE_TELEPORT_COMMAND_TP_ACCEPT = PERMISSION_USE_TELEPORT_COMMAND + ".tpaccept";
    public static String PERMISSION_USE_TELEPORT_COMMAND_TP_DENY = PERMISSION_USE_TELEPORT_COMMAND + ".tpdeny";
    public static String PERMISSION_USE_TELEPORT_COMMAND_TPA_TOGGLE = PERMISSION_USE_TELEPORT_COMMAND + ".tpatoggle";
    public static String PERMISSION_USE_TELEPORT_COMMAND_TPA_HERE = PERMISSION_USE_TELEPORT_COMMAND + ".tpahere";

    public static final String PERMISSION_WARP_GUI_OTHER = "warpsystem.warpgui.other";
    public static final String PERMISSION_HIDE_ALL_ICONS = "warpgui.hideall";
    public static final String PERMISSION_TELEPORT_PRELOAD_CHUNKS = "warpsystem.teleport.chunkpreloading";
    public static final String PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT = "warpsystem.simplewarp.directteleport";
    public static final String PERMISSION_GLOBAL_WARPS_DIRECT_TELEPORT = "warpsystem.globalwarp.directteleport";
    public static final String PERMISSION_RANDOM_TELEPORT_SELECTION_SELF = "warpsystem.randomteleporters.selection";
    public static final String PERMISSION_RANDOM_TELEPORT_SELECTION_OTHER = "warpsystem.randomteleporters.selection.other";
    public static final String PERMISSION_ByPass_Maintenance = "warpsystem.bypass.maintenance";
    public static final String PERMISSION_ByPass_Teleport_Costs = "warpsystem.bypass.teleport.costs";
    public static final String PERMISSION_ByPass_Teleport_Delay = "warpsystem.bypass.teleport.delay";

    public static String PERMISSION_USE_WARP_GUI = "warpsystem.use.warpgui";
    public static String PERMISSION_USE_WARP_SIGNS = "warpsystem.use.warpsigns";
    public static String PERMISSION_USE_GLOBAL_WARPS = "warpsystem.use.globalwarps";
    public static String PERMISSION_USE_SIMPLE_WARPS = "warpsystem.use.simplewarps";
    public static String PERMISSION_USE_PLAYER_WARPS = "warpsystem.use.playerwarps";
    public static String PERMISSION_USE_PORTALS = "warpsystem.use.portals";
    public static String PERMISSION_USE_RANDOM_TELEPORTER = "warpsystem.use.randomteleporters";
    public static String PERMISSION_USE_SPAWN = "warpsystem.use.spawn";

    public static String PERMISSION_ADMIN = "warpsystem.admin";
    public static boolean activated = false;
    public static boolean maintenance = false;
    private static WarpSystem instance;
    private static boolean updateAvailable = false;
    private OptionBundle options;

    private boolean onBungeeCord = false;
    private String bungeePluginVersion = null;
    private String server = null;
    private BungeeBukkitListener packetListener;
    private final List<BungeeFeature> bungeeFeatureList = new ArrayList<>();

    private final TeleportManager teleportManager = new TeleportManager();
    private final FileManager fileManager = new FileManager(this);
    private DataManager dataManager;
    private final HeadManager headManager = new HeadManager();

    private UpdateNotifier updateNotifier;
    private List<String> runningFirstTime = null;

    private final Timer timer = new Timer();
    private boolean old = false;
    private boolean ERROR = true;
    private boolean shouldSave = true;
    private final SpigotDataHandler dataHandler = new SpigotDataHandler(this);
    private UUIDManager uuidManager;

    public static boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || sender.hasPermission(permission);
    }

    public static void updateCommandList() {
        if(Version.getVersion().isBiggerThan(Version.v1_12)) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                IReflection.MethodAccessor updateCommands = IReflection.getMethod(Player.class, "updateCommands");
                updateCommands.invoke(player);
            }
        }
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public static <E extends Options> E getOptions(Class<? extends E> clazz) {
        if(instance == null) return null;

        for(Options option : getInstance().getOptions().getOptions()) {
            if(option.getClass().equals(clazz)) return (E) option;
        }

        return null;
    }

    @Override
    public void onEnable() {
        timer.start();

        instance = this;
        this.dataManager = new DataManager();
        this.dataManager.preLoad();

        this.updateNotifier = new UpdateNotifier();
        loadOptions();

        try {
            checkOldDirectory();

            API.getInstance().onEnable(this);
            SpigotAPI.getInstance().onEnable(this);

            log(" ");
            log("__________________________________________________________");
            log(" ");
            log("                       WarpSystem [" + getDescription().getVersion() + "]");
            log(" ");
            log("Status:");
            log(" ");
            log("MC-Version: " + Version.getVersion().getVersionName());
            log(" ");

            if(this.fileManager.getFile("Config") == null) this.fileManager.loadFile("Config", "/");
            Lang.initPreDefinedLanguages(this);
            this.uuidManager = new UUIDManager();

            PERMISSION_ADMIN = this.fileManager.getFile("Config").getConfig().getString("WarpSystem.Admin.Permission", "WarpSystem.Admin");

            this.runningFirstTime = fileManager.getFile("Config").getConfig().getString("Do_Not_Edit.Last_Version", "0").equals("0") ? new ArrayList<>() : null;
            if(this.runningFirstTime()) createBackup();

            //check permission before loading features
            checkPermissions();

            log("Loading features");
            this.fileManager.loadAll();
            this.dataManager.removeDisabled();

            CWarpSystem cWarpSystem = new CWarpSystem();
            cWarpSystem.register(this);

            boolean createBackup = false;
            if(!this.dataManager.load()) createBackup = true;
            log(" ");
            log("Loading TeleportManager");
            if(!this.teleportManager.load()) createBackup = true;

            if(createBackup) {
                log(" ");
                log(" ");
                log("Loading with errors > Create backup...");
                if(!this.runningFirstTime()) createBackup();
                log("Backup successfully created");
                log(" ");
            }

            maintenance = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Maintenance", false);

            Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
            Bukkit.getPluginManager().registerEvents(new NotifyListener(), this);
            Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
            Bukkit.getPluginManager().registerEvents(new UUIDListener(), this);
            Bukkit.getPluginManager().registerEvents(new HeadListener(), this);
            getBungeeFeatureList().add(new VanishManager());

            this.startAutoSaver();
            afterOnEnable();

            timer.stop();

            log(" ");
            log("Finished (" + timer.getLastStoppedTime() + "s)");
            log(" ");
            log("__________________________________________________________");
            log(" ");

            activated = true;
            startUpdateNotifier();

            this.ERROR = false;

            this.dataHandler.onEnable();
            this.dataHandler.send(new RequestInitialPacket());
            this.dataHandler.register(this.packetListener = new BungeeBukkitListener());
            Bukkit.getPluginManager().registerEvents(this.packetListener, this);

            ConfigFile config = fileManager.getFile("Config");
            if(config.getConfig().getBoolean("WarpSystem.Functions.CommandBlocks", true))
                Bukkit.getPluginManager().registerEvents(new CommandBlockListener(), this);

            if(runningFirstTime()) Bukkit.getScheduler().runTaskLater(this, () -> notifyPlayers(null), 100L);
        } catch(Throwable ex) {
            //make error-report

            if(!getDataFolder().exists()) {
                try {
                    getDataFolder().createNewFile();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            BufferedWriter writer = null;
            try {
                File log = new File(getDataFolder(), "ErrorReport.txt");
                if(log.exists()) log.delete();

                writer = new BufferedWriter(new FileWriter(log));

                PrintWriter printWriter = new PrintWriter(writer);
                ex.printStackTrace(printWriter);
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch(Exception ignored) {
                }
            }


            log(" ");
            log("__________________________________________________________");
            log(" ");
            log("                       WarpSystem [" + getDescription().getVersion() + "]");
            log(" ");
            log("       COULD NOT ENABLE CORRECTLY!!");
            log(" ");
            log("       Please contact the author with the ErrorReport.txt");
            log("       file in the plugins/WarpSystem folder.");
            log(" ");
            log(" ");
            log("       Thanks for supporting!");
            log(" ");
            log("__________________________________________________________");
            log(" ");

            this.ERROR = true;
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void checkPermissions() {
        ConfigFile config = fileManager.getFile("Config");
        if(config.getConfig().getString("Do_Not_Edit.Last_Version").equals("0")) {
            config.getConfig().set("WarpSystem.Permissions", false);
            config.saveConfig();
        }

        if(!config.getConfig().getBoolean("WarpSystem.Permissions", true)) {
            for(Field f : getClass().getDeclaredFields()) {
                if(!Modifier.isFinal(f.getModifiers()) && f.getName().startsWith("PERMISSION_USE_")) {
                    f.setAccessible(true);
                    try {
                        f.set(this, null);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void afterOnEnable() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            //update command dispatcher for players to synchronize CommandList
            Bukkit.getScheduler().runTask(this, WarpSystem::updateCommandList);
            if(this.uuidManager.isEmpty()) this.uuidManager.downloadAll();
        }, 20);
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        SpigotAPI.getInstance().onDisable(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI.unregisterPlaceholderHook("warpsystem");
        }

        save(false);
        teleportManager.getTeleports().forEach(t -> t.cancel(false, false));

        //Disable all functions
        activated = false;
        maintenance = false;
        onBungeeCord = false;
        server = null;
        updateAvailable = false;
        old = false;
        ERROR = true;
        shouldSave = true;
        BungeeCordHelper.bungeeMessenger = null;

        HandlerList.unregisterAll(this);

        this.bungeeFeatureList.forEach(BungeeFeature::onDisconnect);
        this.bungeeFeatureList.clear();

        this.dataHandler.onDisable();
        if(this.packetListener != null) this.dataHandler.unregister(this.packetListener);
        if(this.runningFirstTime != null) this.runningFirstTime.clear();

        destroy();
        this.uuidManager.removeAll();
    }

    private void loadOptions() {
        if(this.options == null) this.options = new OptionBundle(new GeneralOptions(), new WarpGUIOptions(), new WarpSignOptions(), new PortalOptions());
        this.options.read();
        for(Options option : this.options.getOptions()) {
            option.write();
        }
    }

    public void reloadOptions(boolean save) {
        if(save) this.options.write();
        this.options.read();
    }

    public void saveOptions() {
        this.options.write();
    }

    private void startUpdateNotifier() {
        Value<BukkitTask> task = new Value<>(null);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateAvailable = WarpSystem.this.updateNotifier.read();

                if(updateAvailable) {
                    String v = updateNotifier.getVersion();
                    if(!v.startsWith("v")) v = "v" + v;

                    log("-----< WarpSystem >-----");
                    log("New update available [" + v + " - " + WarpSystem.this.updateNotifier.getUpdateInfo() + "].");
                    log("Download it on\n\n" + updateNotifier.getDownload() + "\n");
                    log("------------------------");

                    WarpSystem.getInstance().notifyPlayers(null);
                    task.getValue().cancel();
                }
            }
        };

        task.setValue(Bukkit.getScheduler().runTaskTimerAsynchronously(WarpSystem.getInstance(), runnable, 20L * 5, 5 * 60 * 20L));
    }

    public void reload(boolean save) {
        this.shouldSave = save;

        try {
            API.getInstance().reload(this);
        } catch(InvalidDescriptionException | FileNotFoundException | InvalidPluginException e) {
            e.printStackTrace();
        }
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(WarpSystem.getInstance(), () -> save(true), 10 * 60 * 20, 10 * 60 * 20);
    }

    private void destroy() {
        this.dataManager.getManagers().forEach(Manager::destroy);
        this.bungeeFeatureList.clear();
    }

    private void save(boolean saver) {
        if(!this.shouldSave) return;
        try {
            if(!this.ERROR) {
                if(!saver) {
                    timer.start();

                    log(" ");
                    log("__________________________________________________________");
                    log(" ");
                    log("                       WarpSystem [" + getDescription().getVersion() + "]");
                    if(updateAvailable) {
                        log(" ");
                        log("New update available [" + updateNotifier.getVersion() + " - " + WarpSystem.this.updateNotifier.getUpdateInfo() + "]. Download it on \n\n" + updateNotifier.getDownload() + "\n");
                    }
                    log(" ");
                    log("Status:");
                    log(" ");
                    log("MC-Version: " + Version.getVersion().name());
                    log(" ");
                }

                if(!saver) log("Saving options");
                fileManager.getFile("Config").loadConfig();
                fileManager.getFile("Config").getConfig().set("WarpSystem.Maintenance", maintenance);
                this.options.write();

                if(!saver) log("Saving features");
                this.dataManager.save(saver);
                this.teleportManager.save();

                if(!saver) {
                    timer.stop();

                    log(" ");
                    log("Finished (" + timer.getLastStoppedTime() + "s)");
                    log(" ");
                    log("__________________________________________________________");
                    log(" ");
                }
            }
        } catch(Exception ex) {
            getLogger().log(Level.SEVERE, "Error at saving data! Exception: \n\n");
            ex.printStackTrace();
            getLogger().log(Level.SEVERE, "\n");
        }
    }

    private void checkOldDirectory() {
        File file = getDataFolder();

        if(file.exists()) {
            File warps = new File(file, "Memory/Warps.yml");

            if(warps.exists()) {
                old = true;
                renameUnnecessaryFiles();
            }
        }
    }

    private void renameUnnecessaryFiles() {
        File file = getDataFolder();

        new File(file, "Config.yml").renameTo(new File(file, "OldConfig_Update_2.0.yml"));
        new File(file, "Language.yml").renameTo(new File(file, "OldLanguage_Update_2.0.yml"));
    }

    public void createBackup() {
        try {
            getDataFolder().createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }

        File backupFolder = new File(getDataFolder().getPath() + "/Backups/", TimeFetcher.getYear() + "_" + (TimeFetcher.getMonthNum() + 1) + "_" + TimeFetcher.getDay() + " " + TimeFetcher.getHour() + "_" + TimeFetcher.getMinute() + "_" + TimeFetcher.getSecond());
        backupFolder.mkdirs();

        for(File file : getDataFolder().listFiles()) {
            if(file.getName().equals("Backups") || file.getName().equals("ErrorReport.txt")) continue;
            File dest = new File(backupFolder, file.getName());

            try {
                if(file.isDirectory()) {
                    copyFolder(file, dest);
                    continue;
                }

                copyFileUsingFileChannels(file, dest);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFolder(File source, File dest) throws IOException {
        dest.mkdirs();
        for(File file : source.listFiles()) {
            File copy = new File(dest, file.getName());

            if(file.isDirectory()) {
                copyFolder(file, copy);
                continue;
            }

            copyFileUsingFileChannels(file, copy);
        }
    }

    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public void notifyPlayers(Player player) {
        if(player == null) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                notifyPlayers(p);
            }
        } else {
            if(player.hasPermission(WarpSystem.PERMISSION_NOTIFY) && WarpSystem.updateAvailable) {
                String v = updateNotifier.getVersion();
                if(!v.startsWith("v")) v = "v" + v;

                TextComponent tc0 = new TextComponent(Lang.getPrefix() + "§7A new update is available §8[§b" + v + "§8 - §b" + WarpSystem.getInstance().updateNotifier.getUpdateInfo() + "§8]§7. Download it §7»");
                TextComponent click = new TextComponent("§chere");
                TextComponent tc1 = new TextComponent("§7«!");

                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getUpdateNotifier().getDownload()));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                tc0.addExtra(click);
                tc0.addExtra(tc1);
                tc0.setColor(ChatColor.GRAY);

                player.sendMessage("");
                player.sendMessage("");
                player.spigot().sendMessage(tc0);
                player.sendMessage("");
            }

            if(player.hasPermission(WarpSystem.PERMISSION_NOTIFY) && this.runningFirstTime() && !this.runningFirstTime.contains(player.getName())) {
                this.runningFirstTime.add(player.getName());

                FancyMessage message = new FancyMessage(player, MessageTypes.INFO_MESSAGE, true);
                message.addMessages("                         §c§l§n" + getDescription().getName() + " §c- §l" + getDescription().getVersion());
                message.addMessages("");
                message.addMessages("    §7Hey there,");
                message.addMessages("    §7This is the first time for this server running my §l" + getDescription().getVersion() + "§7!");
                message.addMessages("    §7If you're struggling with all the §cnew stuff§7, run");
                message.addMessages("    §7\"§c/WarpSystem news§7\". And if you'll find some new §cbugs§7,");
                message.addMessages("    §7please run \"§c/WarpSystem report§7\" to report the bug!");
                message.addMessages("");
                message.addMessages("    §7Thank you for using my plugin!");
                message.addMessages("");
                message.addMessages("    §bCodingAir");
                message.send();
            }

            ConfigFile file = fileManager.getFile("Config");
            if(!file.getConfig().getString("Do_Not_Edit.Last_Version").equals(getDescription().getVersion())) {
                file.getConfig().set("Do_Not_Edit.Last_Version", getDescription().getVersion());
                file.saveConfig();
            }
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public boolean isOnBungeeCord() {
        return onBungeeCord;
    }

    public void setOnBungeeCord(boolean onBungeeCord) {
        this.onBungeeCord = onBungeeCord;
        if(onBungeeCord) {
            this.uuidManager.downloadAll();
            this.bungeeFeatureList.forEach(BungeeFeature::onConnect);
        } else {
            this.bungeeFeatureList.forEach(BungeeFeature::onDisconnect);
        }
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public boolean isOld() {
        return old;
    }

    public SpigotDataHandler getDataHandler() {
        return dataHandler;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public String getCurrentServer() {
        return server;
    }

    public void setCurrentServer(String server) {
        this.server = server;
    }

    public UpdateNotifier getUpdateNotifier() {
        return updateNotifier;
    }

    public UUIDManager getUUIDManager() {
        return uuidManager;
    }

    public String getBungeePluginVersion() {
        return bungeePluginVersion;
    }

    public void setBungeePluginVersion(String bungeePluginVersion) {
        this.bungeePluginVersion = bungeePluginVersion;
    }

    public List<BungeeFeature> getBungeeFeatureList() {
        return bungeeFeatureList;
    }

    public HeadManager getHeadManager() {
        return headManager;
    }

    public final boolean isPremium() {
        return true;
    }

    public OptionBundle getOptions() {
        return options;
    }

    private boolean runningFirstTime() {
        return runningFirstTime != null;
    }
}
