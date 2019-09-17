package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.TimeList;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.teleportcommand.commands.*;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ClearInvitesPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.TeleportRequestOptionsPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class TeleportCommandManager implements Manager, BungeeFeature {
    private TimeList<String> hasInvites = new TimeList<>();
    private List<String> denyTpa = new ArrayList<>();
    private List<String> denyForceTps = new ArrayList<>();

    private CTeleport teleportCommand;
    private CTpHere tpHere;
    private CTpa tpa;
    private CTpAll tpAll;
    private CTpaHere tpaHere;
    private CTpToggle tpToggle;
    private CTpaToggle tpaToggle;
    private TeleportPacketListener packetListener;

    private int expireDelay = 30;
    private boolean bungeeCord = false;
    private boolean tpAllNotifySender = true;

    @Override
    public boolean load() {
        WarpSystem.getInstance().getBungeeFeatureList().add(this);
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), WarpSystem.getInstance());

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        expireDelay = file.getConfig().getInt("WarpSystem.TeleportCommands.TeleportRequests.ExpireDelay", 30);
        bungeeCord = file.getConfig().getBoolean("WarpSystem.TeleportCommands.TeleportRequests.BungeeCord", true);
        tpAllNotifySender = file.getConfig().getBoolean("WarpSystem.TeleportCommands.TeleportRequests.TpAll", true);

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.Tp", true)) {
            teleportCommand = new CTeleport();
            teleportCommand.register(WarpSystem.getInstance());
            tpHere = new CTpHere(teleportCommand);
            tpHere.register(WarpSystem.getInstance());
        }

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.TpToggle", true)) {
            tpToggle = new CTpToggle();
            tpToggle.register(WarpSystem.getInstance());
        }

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.Tpa", true)) {
            tpa = new CTpa();
            tpa.register(WarpSystem.getInstance());
        }

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.TpaHere", true)) {
            tpaHere = new CTpaHere();
            tpaHere.register(WarpSystem.getInstance());
        }

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.TpaToggle", true)) {
            tpaToggle = new CTpaToggle();
            tpaToggle.register(WarpSystem.getInstance());
        }

        if(file.getConfig().getBoolean("WarpSystem.TeleportCommands.TpAll", true)) {
            tpAll = new CTpAll();
            tpAll.register(WarpSystem.getInstance());
        }

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
        Bukkit.getPluginManager().registerEvents(this.packetListener, WarpSystem.getInstance());

        WarpSystem.getInstance().getDataHandler().send(new TeleportRequestOptionsPacket(bungeeCord));
    }

    @Override
    public void onDisconnect() {
        if(this.packetListener != null) {
            HandlerList.unregisterAll(this.packetListener);
            WarpSystem.getInstance().getDataHandler().unregister(this.packetListener);
            this.packetListener = null;
        }
    }

    public boolean deniesTpaRequests(Player player) {
        return this.denyTpa.contains(player.getName());
    }

    public boolean toggleDenyTpaRequest(Player player) {
        if(this.denyTpa.contains(player.getName())) {
            this.denyTpa.remove(player.getName());
            return false;
        } else {
            this.denyTpa.add(player.getName());
            return true;
        }
    }

    public boolean deniesForceTps(Player player) {
        return this.denyForceTps.contains(player.getName());
    }

    public boolean toggleDenyForceTps(Player player) {
        if(this.denyForceTps.contains(player.getName())) {
            this.denyForceTps.remove(player.getName());
            return false;
        } else {
            this.denyForceTps.add(player.getName());
            return true;
        }
    }

    public boolean hasOpenInvites(Player player) {
        return this.hasInvites.contains(player.getName());
    }

    public void clear(Player player) {
        this.hasInvites.remove(player.getName());
    }

    /**
     * @param sender     Player
     * @param tpToSender boolean
     * @param receiver   Player...
     * @return the amount of players, who received the tp request
     */
    public int sendTeleportRequest(BungeePlayer sender, boolean tpToSender, boolean notifySender, Player... receiver) {
        if(tpAllNotifySender) notifySender = true;
        boolean finalNotifySender = notifySender;

        SimpleMessage m = new SimpleMessage(Lang.getPrefix() + Lang.get("TeleportRequest_tpTo" + (tpToSender ? "Sender" : "Receiver")).replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())).replace("%SECONDS%", expireDelay+"").replace("%PLAYER%", sender.getDisplayName()), WarpSystem.getInstance()) {
            @Override
            public void onTimeOut() {
                if(sender.onSpigot()) {
                    if(receiver.length == 1) hasInvites.remove(sender.getName());
                }

                if(WarpSystem.getInstance().isOnBungeeCord()) WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(sender.getName()));
            }
        };

        m.replace("%ACCEPT%", new ChatButton(Lang.get("Accept"), Lang.get("Click_Hover")) {
            @Override
            public void onClick(Player player) {
                if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
                } else {
                    if(sender.onSpigot()) {
                        if(sender.getSpigotPlayer().isOnline()) {
                            TeleportOptions options = new TeleportOptions(tpToSender ? sender.getSpigotPlayer().getLocation() : player.getLocation(), tpToSender ? sender.getName() : player.getName());
                            options.setOrigin(Origin.CustomTeleportCommands);
                            options.setWaitForTeleport(true);
                            options.setCallback(new Callback<TeleportResult>() {
                                @Override
                                public void accept(TeleportResult object) {
                                    if(receiver.length == 1) hasInvites.remove(sender.getName());
                                    if(WarpSystem.getInstance().isOnBungeeCord()) WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(sender.getName()));
                                }
                            });

                            WarpSystem.getInstance().getTeleportManager().teleport(tpToSender ? player : sender.getSpigotPlayer(), options);

                            if(finalNotifySender) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_sender").replace("%PLAYER%", ChatColor.stripColor(player.getDisplayName())));
                            player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_other").replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())));
                        } else {
                            player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())));
                        }
                    } else {
                        if(finalNotifySender) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_sender").replace("%PLAYER%", ChatColor.stripColor(player.getDisplayName())));
                        player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_other").replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())));

                        if(tpToSender) {
                            TeleportOptions options = new TeleportOptions(new Destination(new EmptyAdapter()), null);
                            options.setOrigin(Origin.CustomTeleportCommands);
                            options.setWaitForTeleport(true);
                            options.setMessage(null);
                            options.setCallback(new Callback<TeleportResult>() {
                                @Override
                                public void accept(TeleportResult result) {
                                    //move
                                    WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(sender.getName()));
                                    WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPlayerToPlayerPacket(player.getName(), sender.getName(), new Callback<Integer>() {
                                        @Override
                                        public void accept(Integer result) {
                                            if(result == 0) {
                                                //teleported
                                            } else {
                                                player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())));
                                            }
                                        }
                                    }));
                                }
                            });

                            WarpSystem.getInstance().getTeleportManager().teleport(tpToSender ? player : sender.getSpigotPlayer(), options);
                        } else {
                            //tp other
                            WarpSystem.getInstance().getDataHandler().send(new StartTeleportToPlayerPacket(sender.getName(), player.getName(), player.getDisplayName(), sender.getName()));
                        }
                    }

                    m.destroy();
                }
            }
        });

        m.replace("%DENY%", new ChatButton(Lang.get("Deny"), Lang.get("Click_Hover")) {
            @Override
            public void onClick(Player player) {
                if(finalNotifySender) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(player.getDisplayName())));
                player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_other").replace("%PLAYER%", ChatColor.stripColor(sender.getDisplayName())));

                if(sender.onSpigot()) {
                    if(receiver.length == 1) hasInvites.remove(sender.getName());
                } else {
                    WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(sender.getName()));
                }

                m.destroy();
            }
        });

        m.setTimeOut(expireDelay);

        int success = 0;
        for(Player p : receiver) {
            if(!deniesTpaRequests(p)) {
                m.send(p);
                success++;
            }
        }

        if(sender.onSpigot()) {
            if(receiver.length == 1) hasInvites.add(sender.getName());
            else hasInvites.add(sender.getName(), expireDelay);
        }

        return success;
    }

    public static TeleportCommandManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT_COMMAND);
    }
}
