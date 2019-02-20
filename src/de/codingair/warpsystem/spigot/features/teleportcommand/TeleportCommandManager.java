package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.utils.Manager;

public class TeleportCommandManager implements Manager, BungeeFeature {
    private CTeleportDummy teleportCommand;
    private TeleportPacketListener packetListener;

    @Override
    public boolean load() {
        WarpSystem.getInstance().getBungeeFeatureList().add(this);
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void onConnect() {
        this.packetListener = new TeleportPacketListener();
        WarpSystem.getInstance().getDataHandler().register(this.packetListener);

        this.teleportCommand = new CTeleportDummy();
        this.teleportCommand.register(WarpSystem.getInstance());
    }

    @Override
    public void onDisconnect() {
        if(this.teleportCommand != null) {
            this.teleportCommand.unregister(WarpSystem.getInstance());
            this.teleportCommand = null;
        }

        if(this.packetListener != null) {
            WarpSystem.getInstance().getDataHandler().unregister(this.packetListener);
            this.packetListener = null;
        }
    }
}
