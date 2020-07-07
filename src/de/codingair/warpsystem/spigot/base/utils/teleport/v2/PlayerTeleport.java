package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportAcceptEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTeleport extends TeleportStage {
    private Listener listener;
    private final Value<Location> afterEffectPosition;

    protected PlayerTeleport(Value<Location> afterEffectPosition) {
        this.afterEffectPosition = afterEffectPosition;
    }

    @Override
    public void destroy() {
        if(listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
        }
    }

    @Override
    public void start() {
        MessageAPI.stopSendingActionBar(player);

        String message = options.getFinalMessage(player);
        if(message != null) {
            message = message.replace("%AMOUNT%", new ImprovedDouble(options.getCosts(player)) + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', options.getDisplayName()));
        }

        String finalMessage = message;

        if(!options.getDestination().isBungee()) {
            Bukkit.getPluginManager().registerEvents(listener = new Listener() {
                @EventHandler(priority = EventPriority.MONITOR)
                public void onTeleport(PlayerTeleportEvent e) {
                    if(player.equals(e.getPlayer())) {
                        afterEffectPosition.setValue(e.getTo());

                        if(e.isCancelled()) {
                            MessageAPI.sendActionBar(player, Lang.get("Teleport_Cancelled"));
                            HandlerList.unregisterAll(this);

                            cancel(Result.CANCELLED);
                        } else if(Version.getVersion().getId() <= 8)
                            Bukkit.getPluginManager().callEvent(new PlayerTeleportAcceptEvent(e.getPlayer())); //1.8 doesn't contain PlayerTeleportAcceptEvent
                        else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bukkit.getPluginManager().callEvent(new PlayerTeleportAcceptEvent(player));
                                }
                            }.runTaskLater(WarpSystem.getInstance(), 5); //safety timeout (PlayerTeleportAcceptEvent doesn't get triggered while spawning)
                        }
                    }
                }

                @EventHandler
                public void onTeleported(PlayerTeleportAcceptEvent e) {
                    if(player.equals(e.getPlayer())) {
                        if(player.isOnline()) {
                            options.getDestination().sendMessage(player, finalMessage, options.getDisplayName(), options.getCosts(player));
                            if(options.getTeleportSound() != null) options.getTeleportSound().play(player);
                            end();
                        }
                    }
                }

                @EventHandler
                public void onQuit(PlayerQuitEvent e) {
                    if(player.equals(e.getPlayer())) {
                        HandlerList.unregisterAll(this);
                        cancel(Result.DISCONNECT);
                    }
                }
            }, WarpSystem.getInstance());
        }

        options.getDestination().teleport(player, message, options.getDisplayName(), options.getPermission() == null, options.isSilent(), options.getCosts(player), new Callback<Result>() {
            @Override
            public void accept(Result res) {
                if(res == Result.SERVER_NOT_AVAILABLE) player.sendMessage(options.getServerNotOnline());

                if(options.getDestination().isBungee()) {
                    if(res == Result.SUCCESS) end();
                    else cancel(res);
                }
            }
        });
    }
}
