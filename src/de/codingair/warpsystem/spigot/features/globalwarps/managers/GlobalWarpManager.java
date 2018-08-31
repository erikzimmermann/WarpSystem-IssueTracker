package de.codingair.warpsystem.spigot.features.globalwarps.managers;

import de.codingair.codingapi.bungeecord.BungeeCordHelper;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.commands.CGlobalWarps;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.listeners.GlobalWarpListener;
import de.codingair.warpsystem.utils.Manager;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.serializeable.SLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.logging.Level;

public class GlobalWarpManager implements Manager {
    //              Name,   Server
    private HashMap<String, String> globalWarps = new HashMap<>();
    private boolean globalWarpsOfGUI = false;

    private void loadAllGlobalWarps() {
        this.getGlobalWarps().clear();
        WarpSystem.getInstance().getDataHandler().send(new RequestGlobalWarpNamesPacket());
    }

    public void create(String warpName, Location loc, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new PublishGlobalWarpPacket(new SGlobalWarp(warpName, new SLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch())), callback));
    }

    public void delete(String warpName, Callback<Boolean> callback) {
        WarpSystem.getInstance().getDataHandler().send(new DeleteGlobalWarpPacket(warpName, callback));
    }

    public HashMap<String, String> getGlobalWarps() {
        return globalWarps;
    }

    public String getCaseCorrectlyName(String name) {
        for(String warp : this.globalWarps.keySet()) {
            if(warp.equalsIgnoreCase(name)) return warp;
        }

        return name;
    }

    public boolean exists(String name) {
        for(String warp : this.globalWarps.keySet()) {
            if(warp.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    public void teleport(Player player, String display, String name, Callback<PrepareTeleportPacket.Result> callback) {
        teleport(player, display, name, 0, callback);
    }

    public void teleport(Player player, String display, String name, double costs, Callback<PrepareTeleportPacket.Result> callback) {
        if(name == null) return;
        name = getCaseCorrectlyName(name);
        if(!this.globalWarps.containsKey(name)) return;

        WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPacket(player.getName(), name, display, costs, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
                callback.accept(PrepareTeleportPacket.Result.getById(object));
            }
        }));
    }

    @Override
    public boolean load() {
        ConfigFile config = WarpSystem.getInstance().getFileManager().getFile("Config");
        Object test = config.getConfig().get("WarpSystem.GlobalWarps.Use_Warps_Of_WarpsGUI", null);
        if(test == null) {
            config.getConfig().set("WarpSystem.GlobalWarps.Use_Warps_Of_WarpsGUI", false);
        } else if(test instanceof Boolean) {
            globalWarpsOfGUI = (boolean) test;
        }

        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            WarpSystem.getInstance().getLogger().log(Level.INFO, "Looking for a BungeeCord...");

            if(Bukkit.getOnlinePlayers().isEmpty()) {
                WarpSystem.getInstance().getLogger().log(Level.INFO, "Needs a player to search. Waiting...");

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onJoin(PlayerJoinEvent e) {
                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                            if(Bukkit.getOnlinePlayers().isEmpty()) return;

                            WarpSystem.getInstance().getLogger().log(Level.INFO, "Got a player > Searching...");
                            checkBungee();
                            HandlerList.unregisterAll(this);
                        }, 5);
                    }
                }, WarpSystem.getInstance());
            } else {
                checkBungee();
            }
        }, 1L);

        return true;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) this.globalWarps.clear();
    }

    private void checkBungee() {
        BungeeCordHelper.getCurrentServer(WarpSystem.getInstance(), 20 * 10, new Callback<String>() {
            @Override
            public void accept(String server) {
                WarpSystem.getInstance().setOnBungeeCord(server != null);

                if(server != null) {
                    WarpSystem.getInstance().getLogger().log(Level.INFO, "Found a BungeeCord > Init GlobalWarps");
                    WarpSystem.getInstance().getDataHandler().register(new GlobalWarpListener());
                    WarpSystem.getInstance().setCurrentServer(server);
                    new CGlobalWarps().register(WarpSystem.getInstance());
                    new CGlobalWarp().register(WarpSystem.getInstance());
                    loadAllGlobalWarps();
                } else WarpSystem.getInstance().getLogger().log(Level.INFO, "Did not find a BungeeCord > Ignore GlobalWarps");
            }
        });
    }

    public static GlobalWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
    }

    public boolean isGlobalWarpsOfGUI() {
        return globalWarpsOfGUI;
    }
}
