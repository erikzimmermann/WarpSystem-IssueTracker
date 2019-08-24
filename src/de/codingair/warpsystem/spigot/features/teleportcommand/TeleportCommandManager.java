package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.commands.CTeleport;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TeleportCommandManager implements Manager, BungeeFeature {
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
        this.packetListener = new TeleportPacketListener();
        WarpSystem.getInstance().getDataHandler().register(this.packetListener);
        Bukkit.getPluginManager().registerEvents(this.packetListener, WarpSystem.getInstance());
    }

    @Override
    public void onDisconnect() {
        if(this.packetListener != null) {
            HandlerList.unregisterAll(this.packetListener);
            WarpSystem.getInstance().getDataHandler().unregister(this.packetListener);
            this.packetListener = null;
        }
    }

    public void sendTeleportRequest(Player sender, boolean tpToSender, Player... receiver) {
        SimpleMessage m = new SimpleMessage(Lang.getPrefix() + Lang.get("TeleportRequest_tpTo" + (tpToSender ? "Sender" : "Receiver")).replace("%PLAYER%", sender.getDisplayName()), WarpSystem.getInstance());

        m.replace("%ACCEPT%", new ChatButton(Lang.get("Accept"), Lang.get("Click_Hover")) {
            @Override
            public void onClick(Player player) {
                if(tpToSender)
                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CustomTeleportCommands, sender.getLocation(), sender.getName(), true);
                else
                    WarpSystem.getInstance().getTeleportManager().teleport(sender, Origin.CustomTeleportCommands, player.getLocation(), player.getName(), true);
            }
        });

        m.replace("%DENY%", new ChatButton(Lang.get("Deny"), Lang.get("Click_Hover")) {
            @Override
            public void onClick(Player player) {

            }
        });

        m.setTimeOut(20);

        for(Player p : receiver) {
            m.send(p);
        }

        //Send message to sender (Sent request to 34/55 players e.g.)
    }
}
