package de.codingair.warpsystem.spigot.features.shortcuts.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;

import java.util.ArrayList;
import java.util.List;

public class ShortcutPacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        switch(PacketType.getByObject(packet)) {
            case SendGlobalWarpNamesPacket:
                List<Shortcut> found = new ArrayList<>();
                List<Shortcut> delete = new ArrayList<>();

                for(String name : ((SendGlobalWarpNamesPacket) packet).getNames().keySet()) {
                    for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                        if(shortcut.getDestination().getType() == DestinationType.GlobalWarp && shortcut.getDestination().getId().equals(name)) found.add(shortcut);
                    }
                }

                for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                    if(shortcut.getDestination().getType() == DestinationType.GlobalWarp && !found.contains(shortcut)) delete.add(shortcut);
                }

                ShortcutManager.getInstance().getShortcuts().removeAll(delete);
                found.clear();
                delete.clear();
                break;

            case UpdateGlobalWarpPacket:
                switch(((UpdateGlobalWarpPacket) packet).getAction()) {
                    case DELETE:
                        ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().remove(((UpdateGlobalWarpPacket) packet).getName());
                        String globalWarp = ((UpdateGlobalWarpPacket) packet).getName();

                        List<Shortcut> toDelete = new ArrayList<>();
                        for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                            if(shortcut.getDestination().getType() == DestinationType.GlobalWarp && shortcut.getDestination().getId().equals(globalWarp)) toDelete.add(shortcut);
                        }

                        ShortcutManager.getInstance().getShortcuts().removeAll(toDelete);
                        toDelete.clear();
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
