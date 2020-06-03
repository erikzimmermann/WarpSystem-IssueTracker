package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.bungee.base.utils.PacketVanishInfo;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VanishManager implements BungeeFeature {
    private BukkitRunnable runnable;
    private final List<Player> vanished = new ArrayList<>();

    private BukkitRunnable build() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> c = Bukkit.getOnlinePlayers();
                if(c.size() <= 1) return;

                for(Player p : c) {
                    boolean isVanished = false;

                    for(Player other : Bukkit.getOnlinePlayers()) {
                        if(!other.canSee(p)) {
                            //vanished
                            isVanished = true;

                            if(!vanished.contains(p)) {
                                //toggled
                                vanished.add(p);
                                WarpSystem.getInstance().getDataHandler().send(new PacketVanishInfo(p.getName(), true));
                            }
                            break;
                        }
                    }

                    if(!isVanished && vanished.remove(p)) {
                        //toggled
                        WarpSystem.getInstance().getDataHandler().send(new PacketVanishInfo(p.getName(), false));
                    }
                }
            }
        };
    }

    @Override
    public void onConnect() {
        this.runnable = build();
        this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 200L);
    }

    @Override
    public void onDisconnect() {
        runnable.cancel();
        this.runnable = null;
    }
}
