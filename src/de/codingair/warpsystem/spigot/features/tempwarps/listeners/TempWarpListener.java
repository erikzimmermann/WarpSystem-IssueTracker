package de.codingair.warpsystem.spigot.features.tempwarps.listeners;

import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TempWarpListener implements Listener {

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        TempWarpManager.getManager().updateWarps(e.getPlayer());
        TempWarpManager.getManager().loadKeys(e.getPlayer());

        List<TempWarp> warps = TempWarpManager.getManager().getWarps(e.getPlayer());
        if(warps.isEmpty()) return;

        BukkitRunnable runnable = new BukkitRunnable() {
            private int id = 0;

            @Override
            public void run() {
                for(; id < warps.size(); id++) {
                    TempWarp warp = warps.get(id);
                    if(warp.isNotify()) {
                        long time = warp.getExpireDate().getTime() + TimeUnit.MILLISECONDS.convert(TempWarpManager.getManager().getInactiveTime(), TimeUnit.SECONDS) - new Date().getTime();
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("TempWarp_expiring").replace("%TEMP_WARP%", warp.getName()).replace("%TIME_LEFT%", TempWarpManager.getManager().convertInTimeFormat(time, TimeUnit.MILLISECONDS)));
                        id++;
                        break;
                    }
                }

                if(id == warps.size()) {
                    warps.clear();
                    this.cancel();
                }
            }
        };

        runnable.runTaskTimer(WarpSystem.getInstance(), 20 * 5, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TempWarpManager.getManager().saveAndRemoveKeys(e.getPlayer(), true);
    }

}