package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.transfer.packets.bungee.InitialPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class BungeeBukkitListener implements PacketListener, Listener {
    private String[] notice = null;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if(notice != null && (e.getPlayer().hasPermission(WarpSystem.PERMISSION_NOTIFY) || e.getPlayer().isOp())) e.getPlayer().sendMessage(notice);
        }, 20 * 4L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(Bukkit.getOnlinePlayers().size() <= 1) {
            WarpSystem.getInstance().setOnBungeeCord(false);
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        switch(PacketType.getByObject(packet)) {
            case InitialPacket: {
                String version = ((InitialPacket) packet).getVersion();
                if(version.equals(WarpSystem.getInstance().getDescription().getVersion())) {
                    if(WarpSystem.getInstance().getBungeePluginVersion() == null || !WarpSystem.getInstance().getBungeePluginVersion().equals(version)) {
                        WarpSystem.getInstance().getLogger().log(Level.INFO, "Found a valid DataCenter > Init BungeeFeatures");
                    }

                    WarpSystem.getInstance().setOnBungeeCord(true);
                } else if(WarpSystem.getInstance().getBungeePluginVersion() == null || WarpSystem.getInstance().getBungeePluginVersion().equals(WarpSystem.getInstance().getDescription().getVersion())) {
                    this.notice = new String[] {
                            "",
                            "§c§l§nWarpSystem",
                            "",
                            "§7WarpSystem §cversion §7of the DataCenter and the SpigotServer are §cdifferent§7!",
                            "§7Please §cupdate §7the WarpSystem §con your BungeeCord §7(" + version + ") §cand on this Server §7(" + WarpSystem.getInstance().getDescription().getVersion() + ")",
                            ""
                    };
                    WarpSystem.getInstance().getLogger().log(Level.WARNING, "WarpSystem version of the DataCenter and the SpigotServer are different!");
                    WarpSystem.getInstance().getLogger().log(Level.INFO, "Please update the WarpSystem on your BungeeCord (" + version + ") and on this Server (" + WarpSystem.getInstance().getDescription().getVersion() + ")");
                }

                WarpSystem.getInstance().setBungeePluginVersion(version);
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
