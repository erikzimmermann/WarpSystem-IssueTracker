package de.codingair.warpsystem.spigot.base.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.teleport.v2.Teleport;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class TeleportManager {
    private static TeleportManager instance;
    public static final String NO_PERMISSION = "%NO_PERMISSION%";
    private Cache<String, Teleport> teleports;
    private GeneralOptions options;

    private TeleportManager() {
    }

    /**
     * Have to be launched after the IconManager (see WarpSign.class - fromJSONString method - need warps and categories)
     */
    public boolean load() {
        boolean success = true;

        this.options = WarpSystem.getOptions(GeneralOptions.class);
        this.teleports = CacheBuilder.newBuilder().expireAfterAccess(options.getTeleportDelay(), TimeUnit.SECONDS).build();
        return success;
    }

    public void save() {
        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();
    }

    public synchronized void teleport(Player player, TeleportOptions options) {
        if(isTeleporting(player)) {
            Teleport teleport = getTeleport(player);
            long diff = System.currentTimeMillis() - teleport.getStartTime();
            if(diff > 50)
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
            return;
        }

        if(options.getDestination() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return;
        }

        if((options.getDestination().getType() == DestinationType.GlobalWarp || options.getDestination().getType() == DestinationType.Server) && !WarpSystem.getInstance().isOnBungeeCord()) {
            options.fireCallbacks(Result.NOT_ON_BUNGEE_CORD);
            player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
            return;
        }

        options.addCallback(new Callback<Result>() {
            @Override
            public void accept(de.codingair.warpsystem.spigot.base.utils.teleport.Result result) {
                teleports.invalidate(player.getName());
            }
        });

        this.teleports.put(player.getName(), new Teleport(player, options).start());
    }

    public void cancelTeleport(Player player) {
        if(!isTeleporting(player)) return;

        Teleport teleport = getTeleport(player);
        teleport.cancel(Result.CANCELLED_BY_SYSTEM);
        this.teleports.invalidate(player.getName());

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Cancel_Message", true)) {
            MessageAPI.sendActionBar(player, Lang.get("Teleport_Cancelled"));
        }
    }

    public Teleport getTeleport(Player player) {
        Teleport t = teleports.getIfPresent(player.getName());
        return t == null || t.expired() ? null : t;
    }

    public boolean isTeleporting(Player p) {
        return getTeleport(p) != null;
    }

    public Collection<Teleport> getTeleports() {
        return teleports == null ? new ArrayList<>() : teleports.asMap().values();
    }

    public void clear() {
        if(this.teleports != null) this.teleports.invalidateAll();
    }

    public GeneralOptions getOptions() {
        return options;
    }

    public static TeleportManager getInstance() {
        if(instance == null) instance = new TeleportManager();
        return instance;
    }
}
