package de.codingair.warpsystem;

import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Files.FileManager;
import de.CodingAir.v1_6.CodingAPI.Server.Version;
import de.CodingAir.v1_6.CodingAPI.Time.Timer;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.codingair.warpsystem.Language.Lang;
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

    public UpdateChecker updateChecker = new UpdateChecker("https://www.spigotmc.org/resources/warpsystem-gui.29595/history");
    private Timer timer = new Timer();

    public static boolean updateAvailable = false;

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        timer.start();

        updateAvailable = WarpSystem.this.updateChecker.needsUpdate();

        WarpSystem.log(" ");
        WarpSystem.log("__________________________________________________________");
        WarpSystem.log(" ");
        WarpSystem.log("                       WarpSystem [" + getDescription().getVersion() + "]");
        if(updateAvailable) {
            WarpSystem.log(" ");
            WarpSystem.log("New update available [v" + updateChecker.getVersion() + " - "+WarpSystem.this.updateChecker.getUpdateInfo()+"].");
            WarpSystem.log("Download it on\n\n" + updateChecker.getDownload() + "\n");
        }
        WarpSystem.log(" ");
        WarpSystem.log("Status:");
        WarpSystem.log(" ");
        WarpSystem.log("MC-Version: " + Version.getVersion().getVersionName());
        WarpSystem.log("CodingAPI-Version: " + API.VERSION);
        WarpSystem.log(" ");

        WarpSystem.log("Loading files.");
        this.fileManager.loadFile("ActionIcons", "/Memory/");
        this.fileManager.loadFile("Language", "/");
        this.fileManager.loadFile("Config", "/");

        WarpSystem.log("Loading icons.");
        this.iconManager.load(true);
        WarpSystem.log("Loading TeleportManager.");
        this.teleportManager.load();

        maintenance = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Maintenance", false);
        OP_CAN_SKIP_DELAY = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Op_Can_Skip_Delay", false);

        Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
        Bukkit.getPluginManager().registerEvents(new NotifyListener(), this);
        getCommand("warp").setExecutor(new CWarps());

        this.startAutoSaver();

        timer.stop();

        WarpSystem.log(" ");
        WarpSystem.log("Done (" + timer.getLastStoppedTime() + "s)");
        WarpSystem.log(" ");
        WarpSystem.log("__________________________________________________________");
        WarpSystem.log(" ");

        activated = true;
        notifyPlayers(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    BungeeCordHelper.runningOnBungeeCord(WarpSystem.this, 2 * 20, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean onBungee) {
                            onBungeeCord = onBungee;
                        }
                    });

                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        API.getInstance().onDisable();
        save();
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver.");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(WarpSystem.getInstance(), this::save, 20 * 60 * 20, 20 * 60 * 20);
    }

    private void save() {
        timer.start();

        log(" ");
        log("__________________________________________________________");
        log(" ");
        log("           AutoSaver - WarpSystem [" + getDescription().getVersion() + "]");
        if(updateAvailable) {
            WarpSystem.log(" ");
            WarpSystem.log("New update available [v" + updateChecker.getVersion() + " - "+WarpSystem.this.updateChecker.getUpdateInfo()+"]. Download it on \n\n" + updateChecker.getDownload() + "\n");
        }
        log(" ");
        WarpSystem.log("Status:");
        log(" ");
        WarpSystem.log("MC-Version: " + Version.getVersion().name());
        WarpSystem.log("CodingAPI-Version: " + API.VERSION);
        WarpSystem.log(" ");

        iconManager.save(true);
        timer.stop();

        log(" ");
        WarpSystem.log("Done (" + timer.getLastStoppedTime() + "s)");
        log(" ");
        log("__________________________________________________________");
        log(" ");
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
}
