package de.codingair.warpsystem.spigot.features.globalwarps.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.event.Listener;

public class GlobalWarpListener implements Listener, PacketListener {

    @Override
    public void onReceive(Packet packet, String extra) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARP_GUI);

        switch(PacketType.getByObject(packet)) {
            case SendGlobalWarpNamesPacket:
                GlobalWarpManager gwManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
                if(((SendGlobalWarpNamesPacket) packet).isStart()) {
                    gwManager.getGlobalWarps().clear();
                }
                gwManager.getGlobalWarps().putAll(((SendGlobalWarpNamesPacket) packet).getNames());
                break;

            case UpdateGlobalWarpPacket:
                switch(((UpdateGlobalWarpPacket) packet).getAction()) {
                    case ADD:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().put(((UpdateGlobalWarpPacket) packet).getName(), ((UpdateGlobalWarpPacket) packet).getServer());
                        break;

                    case UPDATE_POSITION:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().replace(((UpdateGlobalWarpPacket) packet).getName(), ((UpdateGlobalWarpPacket) packet).getServer());
                        break;

                    case DELETE:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().remove(((UpdateGlobalWarpPacket) packet).getName());

                        for(Icon warpIcon : manager.getIcons()) {
                            if(warpIcon.getAction(Action.WARP) != null) {
                                if(warpIcon.getAction(WarpAction.class).getValue().getType() == DestinationType.GlobalWarp &&
                                        warpIcon.getAction(WarpAction.class).getValue().getId().equalsIgnoreCase(((UpdateGlobalWarpPacket) packet).getName()))
                                    warpIcon.getAction(WarpAction.class).setValue(null);
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
