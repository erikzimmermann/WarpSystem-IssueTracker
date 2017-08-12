package de.codingair.warpsystem;

import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Files.FileManager;
import de.CodingAir.v1_6.CodingAPI.Particles.Particle;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.PlayerItem;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.commands.CWarps;
import de.codingair.warpsystem.listeners.TeleportListener;
import de.codingair.warpsystem.managers.IconManager;
import de.codingair.warpsystem.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WarpSystem extends JavaPlugin {
    public static final String PERMISSION_MODIFY = "WarpSystem.Modify";
    public static final String PERMISSION_ByPass_Maintenance = "WarpSystem.ByPass.Maintenance";
    public static final String PERMISSION_ByPass_Teleport_Delay = "WarpSystem.ByPass.Teleport.Delay";

    private static WarpSystem instance;
    public static boolean activated = false;
    public static boolean maintenance = false;

    private boolean onBungeeCord;

    private IconManager iconManager = new IconManager();
    private TeleportManager teleportManager = new TeleportManager();
    private FileManager fileManager = new FileManager(this);

    private int id = 0;

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        this.fileManager.loadFile("ActionIcons", "/Memory/");
        this.fileManager.loadFile("Language", "/");
        this.fileManager.loadFile("Config", "/");

        this.iconManager.load(true);
        this.teleportManager.load();

        maintenance = fileManager.getFile("Config").getConfig().getBoolean("WarpSystem.Maintenance", false);

        Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
        getCommand("warp").setExecutor(new CWarps());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    BungeeCordHelper.runningOnBungeeCord(WarpSystem.this, 2 * 20, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean onBungee) {
                            onBungeeCord = onBungee;
                            activated = true;
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

        this.teleportManager.save();
        this.iconManager.save(true);
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
}
