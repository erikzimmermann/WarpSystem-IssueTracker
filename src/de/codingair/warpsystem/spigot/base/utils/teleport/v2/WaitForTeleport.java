package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitForTeleport extends TeleportStage {

    protected WaitForTeleport() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        if((options.isCanMove() || options.isSkip() || options.getDelay(player) == 0) && options.getCosts(player) == 0) {
            end();
            return;
        }

        wait(player, new Callback<Result>() {
            @Override
            public void accept(Result result) {
                if(result == Result.SUCCESS) end();
                else cancel(result);
            }
        });
    }

    public static void wait(Player player, Callback<Result> callback) {
        new BukkitRunnable() {
            int notMoving = 0;
            int shakeTicks = 0;
            boolean shake = false;
            Location location = player.getLocation();

            @Override
            public void run() {
                if(!player.isOnline() || location.getWorld() != player.getWorld()) {
                    this.cancel();
                    callback.accept(Result.CANCELLED);
                    return;
                }

                if(location.distance(player.getLocation()) <= 0.01) notMoving++;
                else {
                    notMoving = 0;
                    location = player.getLocation();

                    MessageAPI.sendActionBar(player, "§7» " + (shake ? " " : "") + Lang.get("Teleport_Stop_Moving") + (shake ? " " : "") + " §7«");

                    if(shakeTicks == 3) {
                        shakeTicks = 0;
                        shake = !shake;
                    } else shakeTicks++;
                }

                if(notMoving == 2) {
                    MessageAPI.stopSendingActionBar(player);
                    this.cancel();
                    callback.accept(Result.SUCCESS);
                }
            }
        }.runTaskTimer(WarpSystem.getInstance(), 2, 2);
    }
}
