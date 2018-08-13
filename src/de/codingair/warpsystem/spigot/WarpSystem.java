package de.codingair.warpsystem.spigot;

import de.codingair.codingapi.API;
import de.codingair.codingapi.bungeecord.BungeeCordHelper;
import de.codingair.codingapi.files.FileManager;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.time.Timer;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.commands.*;
import de.codingair.warpsystem.spigot.features.signs.SignListener;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.spigot.listeners.NotifyListener;
import de.codingair.warpsystem.spigot.listeners.PortalListener;
import de.codingair.warpsystem.spigot.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.managers.IconManager;
import de.codingair.warpsystem.spigot.managers.TeleportManager;
import de.codingair.warpsystem.spigot.utils.UpdateChecker;
import de.codingair.warpsystem.transfer.spigot.SpigotDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WarpSystem extends JavaPlugin {
    public static final String PERMISSION_NOTIFY = "WarpSystem.Notify";
    public static final String PERMISSION_MODIFY = "WarpSystem.Modify";
    public static final String PERMISSION_MODIFY_ICONS = "WarpSystem.Modify.Icons";
    public static final String PERMISSION_MODIFY_GLOBAL_WARPS = "WarpSystem.Modify.GlobalWarps";
    public static final String PERMISSION_MODIFY_PORTALS = "WarpSystem.Modify.Portals";
    public static final String PERMISSION_USE = "WarpSystem.Use";
    public static final String PERMISSION_ByPass_Maintenance = "WarpSystem.ByPass.Maintenance";
    public static final String PERMISSION_ByPass_Teleport_Costs = "WarpSystem.ByPass.Teleport.Costs";
    public static final String PERMISSION_ByPass_Teleport_Delay = "WarpSystem.ByPass.Teleport.Delay";
    public static boolean OP_CAN_SKIP_DELAY = false;

    private static WarpSystem instance;
    public static boolean activated = false;
    public static boolean maintenance = false;

    private boolean onBungeeCord = false;
    private String server = null;

    private IconManager iconManager = new IconManager();
    private TeleportManager teleportManager = new TeleportManager();
    private FileManager fileManager = new FileManager(this);

    private UpdateChecker updateChecker = new UpdateChecker("https://www.spigotmc.org/resources/warpsystem-gui.29595/history");
    private Timer timer = new Timer();

    private static boolean updateAvailable = false;
    private boolean old = false;
    private boolean ERROR = true;
    private List<CommandBuilder> commands = new ArrayList<>();
    private boolean shouldSave = true;

    private SpigotDataHandler dataHandler = new SpigotDataHandler(this);
    private GlobalWarpManager globalWarpManager = new GlobalWarpManager();

    @Override
    public void onEnable() {
        try {
            checkOldDirectory();

            instance = this;
            API.getInstance().onEnable(this);

            timer.start();

            updateAvailable = WarpSystem.this.updateChecker.needsUpdate();

            log(" ");
            log("__________________________________________________________");
            log(" ");
            log("                       WarpSystem [" + getDescription().getVersion() + "]");
            if(updateAvailable) {
                log(" ");
                log("New update available [v" + updateChecker.getVersion() + " - " + WarpSystem.this.updateChecker.getUpdateInfo() + "].");
                log("Download it on\n\n" + updateChecker.getDownload() + "\n");
            }
            log(" ");
            log("Status:");
            log(" ");
            log("MC-Version: " + Version.getVersion().getVersionName());
            log(" ");

            log("Loading files.");
            this.fileManager.loadFile("ActionIcons", "/Memory/");
            this.fileManager.loadFile("Teleporters", "/Memory/");
            this.fileManager.loadFile("Language", "/");
            this.fileManager.loadFile("Config", "/");

            log("Loading icons.");
            this.iconManager.load(true);
            log("Loading TeleportManager.");
            this.teleportManager.load();

            maintenance = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Maintenance", false);
            OP_CAN_SKIP_DELAY = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Op_Can_Skip_Delay", false);

            Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
            Bukkit.getPluginManager().registerEvents(new NotifyListener(), this);
            Bukkit.getPluginManager().registerEvents(new PortalListener(), this);
            Bukkit.getPluginManager().registerEvents(new SignListener(), this);

            if(fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Warps", true)) {
                CWarp cWarp = new CWarp();
                CWarps cWarps = new CWarps();

                this.commands.add(cWarp);
                this.commands.add(cWarps);

                cWarp.register(this);
                cWarps.register(this);
            }

            CWarpSystem cWarpSystem = new CWarpSystem();
            this.commands.add(cWarpSystem);
            cWarpSystem.register(this);

            if(fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
                CPortal cPortal = new CPortal();
                this.commands.add(cPortal);
                cPortal.register(this);
            }

            this.startAutoSaver();

            timer.stop();

            log(" ");
            log("Done (" + timer.getLastStoppedTime() + "s)");
            log(" ");
            log("__________________________________________________________");
            log(" ");

            activated = true;
            notifyPlayers(null);

            this.ERROR = false;

            this.dataHandler.onEnable();

            log("WarpSystem - Looking for a BungeeCord...");

            if(Bukkit.getOnlinePlayers().isEmpty()) {
                log("WarpSystem - Needs a player to search for a BungeeCord. Waiting...");

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    private void unregister() {
                        HandlerList.unregisterAll(this);
                    }

                    @EventHandler
                    public void onJoin(PlayerJoinEvent e) {
                        Bukkit.getScheduler().runTaskLater(WarpSystem.this, () -> {
                            if(Bukkit.getOnlinePlayers().isEmpty()) return;

                            checkBungee();
                        }, 5);
                    }
                }, this);
            } else {
                checkBungee();
            }
        } catch(Exception ex) {
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

    private void checkBungee() {
        BungeeCordHelper.getCurrentServer(WarpSystem.this, 20 * 10, new Callback<String>() {
            @Override
            public void accept(String server) {
                WarpSystem.this.onBungeeCord = server != null;

                if(onBungeeCord) {
                    log("WarpSystem - Found a BungeeCord > Init GlobalWarps");
                    WarpSystem.this.server = server;
                    new CGlobalWarp().register(WarpSystem.this);
                    globalWarpManager.loadAllGlobalWarps();
                } else {
                    log("WarpSystem - Did not find a BungeeCord > Ignore GlobalWarps");
                }
            }
        });
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable(this);
        save(false);
        teleportManager.getTeleports().forEach(t -> t.cancel(false, false));

        //Disable all functions
        OP_CAN_SKIP_DELAY = false;
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
        this.dataHandler.onDisable();

        for(int i = 0; i < this.commands.size(); i++) {
            this.commands.remove(0).unregister(this);
        }

        this.globalWarpManager.getGlobalWarps().clear();
    }

    public void reload(boolean save) {
        this.shouldSave = save;

        Bukkit.getPluginManager().disablePlugin(WarpSystem.getInstance());
        Bukkit.getPluginManager().enablePlugin(WarpSystem.getInstance());
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver.");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(WarpSystem.getInstance(), () -> save(true), 20 * 60 * 20, 20 * 60 * 20);
    }

    private void save(boolean saver) {
        if(!this.shouldSave) return;
        try {
            if(!saver) {
                timer.start();

                log(" ");
                log("__________________________________________________________");
                log(" ");
                log("                       WarpSystem [" + getDescription().getVersion() + "]");
                if(updateAvailable) {
                    log(" ");
                    log("New update available [v" + updateChecker.getVersion() + " - " + WarpSystem.this.updateChecker.getUpdateInfo() + "]. Download it on \n\n" + updateChecker.getDownload() + "\n");
                }
                log(" ");
                log("Status:");
                log(" ");
                log("MC-Version: " + Version.getVersion().name());
                log(" ");

                if(!this.ERROR) log("Saving icons.");
                else {
                    log("Does not save data, because of errors at enabling this plugin.");
                    log(" ");
                    log("Please submit the ErrorReport.txt file to CodingAir.");
                }
            }

            if(!this.ERROR) {
                iconManager.save(true);
                if(!saver) log("Saving options.");
                fileManager.getFile("Config").loadConfig();
                fileManager.getFile("Config").getConfig().set("WarpSystem.Maintenance", maintenance);
                fileManager.getFile("Config").getConfig().set("WarpSystem.Teleport.Op_Can_Skip_Delay", OP_CAN_SKIP_DELAY);
                if(!saver) log("Saving features.");
                teleportManager.save(saver);
            }

            if(!saver) {
                timer.stop();

                log(" ");
                log("Done (" + timer.getLastStoppedTime() + "s)");
                log(" ");
                log("__________________________________________________________");
                log(" ");
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

    public void notifyPlayers(Player player) {
        if(player == null) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                notifyPlayers(p);
            }
        } else {
            if(player.hasPermission(WarpSystem.PERMISSION_NOTIFY) && WarpSystem.updateAvailable) {
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage(Lang.getPrefix() + "§aA new update is available §8[§bv" + WarpSystem.getInstance().updateChecker.getVersion() + "§8 - §b" + WarpSystem.getInstance().updateChecker.getUpdateInfo() + "§8]§a. Download it on §b§nhttps://www.spigotmc.org/resources/warpsystem-gui.29595/history");
                player.sendMessage("");
                player.sendMessage("");
            }
        }
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public boolean isOnBungeeCord() {
        return onBungeeCord;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public boolean isOld() {
        return old;
    }

    public SpigotDataHandler getDataHandler() {
        return dataHandler;
    }

    public GlobalWarpManager getGlobalWarpManager() {
        return globalWarpManager;
    }

    public String getCurrentServer() {
        return server;
    }

    public List<CommandBuilder> getCommands() {
        return commands;
    }
}
