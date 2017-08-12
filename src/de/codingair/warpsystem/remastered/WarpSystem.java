package de.codingair.warpsystem.remastered;

import de.CodingAir.v1_6.CodingAPI.API;
import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Files.FileManager;
import de.CodingAir.v1_6.CodingAPI.Player.Data.GameProfile.GameProfileUtils;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_6.CodingAPI.Tools.Callback;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.CodingAir.v1_6.CodingAPI.Tools.OldItemBuilder;
import de.codingair.warpsystem.remastered.commands.CWarps;
import de.codingair.warpsystem.remastered.managers.IconManager;
import de.codingair.warpsystem.remastered.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WarpSystem extends JavaPlugin {
    public static final String PERMISSION_MODIFY = "CWarps.Modify";

    private static WarpSystem instance;
    public static boolean activated = false;

    private boolean onBungeeCord;

    private IconManager iconManager = new IconManager();
    private TeleportManager teleportManager = new TeleportManager();
    private FileManager fileManager = new FileManager(this);

    @Override
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        this.fileManager.loadFile("ActionIcons", "/Memory/");
        this.fileManager.loadFile("Language", "/");
        this.iconManager.load(true);

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
