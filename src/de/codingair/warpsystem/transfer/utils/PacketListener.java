package de.codingair.warpsystem.transfer.utils;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

public interface PacketListener {
    void onReceive(Packet packet, String extra);
    boolean onSend(Packet packet);
}
