package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.features.teleportcommand.dummy.CTeleportDummy;
import de.codingair.warpsystem.spigot.features.teleportcommand.dummy.TeleportPacketListener;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class TeleportCommandManager implements Manager, BungeeFeature {
    private CTeleportDummy teleportCommandDummy;
    private CTeleport teleportCommand;
    private TeleportPacketListener packetListener;

    @Override
    public boolean load() {
        WarpSystem.getInstance().getBungeeFeatureList().add(this);
        teleportCommand = new CTeleport();
        teleportCommand.register(WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
        teleportCommand.unregister(WarpSystem.getInstance());
    }

    @Override
    public void onConnect() {
        teleportCommand.unregister(WarpSystem.getInstance());

        this.packetListener = new TeleportPacketListener();
        WarpSystem.getInstance().getDataHandler().register(this.packetListener);
        Bukkit.getPluginManager().registerEvents(this.packetListener, WarpSystem.getInstance());

        this.teleportCommandDummy = new CTeleportDummy();
        this.teleportCommandDummy.register(WarpSystem.getInstance());
    }

    @Override
    public void onDisconnect() {
        if(this.teleportCommandDummy != null) {
            this.teleportCommandDummy.unregister(WarpSystem.getInstance());
            this.teleportCommandDummy = null;
        }

        if(this.packetListener != null) {
            HandlerList.unregisterAll(this.packetListener);
            WarpSystem.getInstance().getDataHandler().unregister(this.packetListener);
            this.packetListener = null;
        }

        teleportCommand.register(WarpSystem.getInstance());
    }
}
