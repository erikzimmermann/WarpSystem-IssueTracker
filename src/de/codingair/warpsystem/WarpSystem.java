package de.codingair.warpsystem;

import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Files.FileManager;
import de.CodingAir.v1_6.CodingAPI.Server.Reflections.IReflection;
import de.CodingAir.v1_6.CodingAPI.Server.Version;
import de.CodingAir.v1_6.CodingAPI.Time.Timer;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.commands.CWarpSystem;
import de.codingair.warpsystem.commands.CWarps;
import de.codingair.warpsystem.listeners.NotifyListener;
import de.codingair.warpsystem.listeners.TeleportListener;
import de.codingair.warpsystem.managers.IconManager;
import de.codingair.warpsystem.managers.TeleportManager;
import de.codingair.warpsystem.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class WarpSystem extends JavaPlugin {
    public static final String PERMISSION_NOTIFY = "WarpSystem.Notify";
    public static final String PERMISSION_MODIFY = "WarpSystem.Modify";
    public static final String PERMISSION_ByPass_Maintenance = "WarpSystem.ByPass.Maintenance";
    public static final String PERMISSION_ByPass_Teleport_Delay = "WarpSystem.ByPass.Teleport.Delay";
    public static boolean OP_CAN_SKIP_DELAY = false;

    private static WarpSystem instance;
    public static boolean activated = false;
    public static boolean maintenance = false;

    private boolean onBungeeCord;

    private IconManager iconManager = new IconManager();
    private TeleportManager teleportManager = new TeleportManager();
    private FileManager fileManager = new FileManager(this);

    private UpdateChecker updateChecker = new UpdateChecker("https://www.spigotmc.org/resources/warpsystem-gui.29595/history");
    private Timer timer = new Timer();

    private static boolean updateAvailable = false;
    private boolean old = false;

    @Override
    public void onEnable() {
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
        log("CodingAPI-Version: " + API.VERSION);
        log(" ");

        log("Loading files.");
        this.fileManager.loadFile("ActionIcons", "/Memory/");
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

        getCommand("warp").setExecutor(new CWarps());
        getCommand("warp").setTabCompleter(new CWarps());
        getCommand("warpsystem").setExecutor(new CWarpSystem());
        getCommand("warpsystem").setTabCompleter(new CWarpSystem());

        this.startAutoSaver();

        timer.stop();

        log(" ");
        log("Done (" + timer.getLastStoppedTime() + "s)");
        log(" ");
        log("__________________________________________________________");
        log(" ");

        activated = true;
        notifyPlayers(null);

        //TODO: Unnecessary in this version
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if(!Bukkit.getOnlinePlayers().isEmpty()) {
//                    BungeeCordHelper.runningOnBungeeCord(WarpSystem.this, 2 * 20, new Callback<Boolean>() {
//                        @Override
//                        public void accept(Boolean onBungee) {
//                            onBungeeCord = onBungee;
//                        }
//                    });
//
//                    this.cancel();
//                }
//            }
//        }.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable();
        save(false);
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver.");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(WarpSystem.getInstance(), () -> save(true), 20 * 60 * 20, 20 * 60 * 20);
    }

    private void save(boolean saver) {
        timer.start();

        log(" ");
        log("__________________________________________________________");
        log(" ");
        if(saver)
            log("           AutoSaver - WarpSystem [" + getDescription().getVersion() + "]");
        else
            log("                       WarpSystem [" + getDescription().getVersion() + "]");
        if(updateAvailable) {
            log(" ");
            log("New update available [v" + updateChecker.getVersion() + " - " + WarpSystem.this.updateChecker.getUpdateInfo() + "]. Download it on \n\n" + updateChecker.getDownload() + "\n");
        }
        log(" ");
        log("Status:");
        log(" ");
        log("MC-Version: " + Version.getVersion().name());
        log("CodingAPI-Version: " + API.VERSION);
        log(" ");

        log("Saving icons.");
        iconManager.save(true);
        timer.stop();

        log(" ");
        log("Done (" + timer.getLastStoppedTime() + "s)");
        log(" ");
        log("__________________________________________________________");
        log(" ");
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
}
