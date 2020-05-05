package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ClearInvitesPacket;
import de.codingair.warpsystem.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Invitation {
    private String sender;
    private String[] recipients; //notify if receiver.length == 1
    private boolean toSender; //only send if receiver.length == 1
    private List<String> handled = new ArrayList<>();

    protected Invitation(String sender, String[] recipients) {
        this.sender = sender;
        this.recipients = recipients;
    }

    protected Invitation(String sender, boolean toSender, String recipients) {
        this.sender = sender;
        this.toSender = toSender;
        this.recipients = new String[] {recipients};
    }

    public boolean isRecipient(String player) {
        for(String s : this.recipients) {
            if(s.equalsIgnoreCase(player)) return !handled.contains(s);
        }

        return false;
    }

    public void accept(Player player) {
        if(!isRecipient(player.getName())) return;
        //to sender
        handled.add(player.getName());

        BungeePlayer sender = new BungeePlayer(this.sender);

        if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
        } else {
            if(sender.onSpigot()) {
                if(sender.getSpigotPlayer().isOnline()) {
                    TeleportOptions options = new TeleportOptions(toSender ? sender.getSpigotPlayer().getLocation() : player.getLocation(), toSender ? sender.getName() : player.getName());
                    options.setOrigin(Origin.CustomTeleportCommands);
                    options.setWaitForTeleport(true);
                    options.setCosts(TeleportCommandManager.getInstance().getTpaCosts());

                    WarpSystem.getInstance().getTeleportManager().teleport(toSender ? player : sender.getSpigotPlayer(), options);

                    if(recipients.length == 1)
                        sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
                    player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
                } else {
                    player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
                }
            } else {
                if(toSender) {
                    TeleportOptions options = new TeleportOptions(new Destination(new EmptyAdapter()), sender.getName());
                    options.setOrigin(Origin.CustomTeleportCommands);
                    options.setWaitForTeleport(true);
                    options.setMessage(null);
                    options.setPayMessage(null);
                    options.setCosts(TeleportCommandManager.getInstance().getTpaCosts());
                    options.setPaymentDeniedMessage(null);
                    options.addCallback(new Callback<TeleportResult>() {
                        @Override
                        public void accept(TeleportResult result) {
                            //move
                            WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(sender.getName()));
                            if(result == TeleportResult.TELEPORTED) {
                                if(recipients.length == 1)
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
                                player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));

                                WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPlayerToPlayerPacket(player.getName(), sender.getName(), new Callback<Integer>() {
                                    @Override
                                    public void accept(Integer result) {
                                        if(result == 0) {
                                            //teleported
                                        } else {
                                            player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
                                        }
                                    }
                                }).setCosts(TeleportCommandManager.getInstance().getTpaCosts()));
                            } else {
                                if(recipients.length == 1)
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
                                player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
                            }
                        }
                    });

                    WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                } else {
                    //tp other
                    WarpSystem.getInstance().getDataHandler().send(new StartTeleportToPlayerPacket(sender.getName(), player.getName(), player.getName(), sender.getName()));
                }
            }
        }

        TeleportCommandManager.getInstance().checkDestructionOf(this);
    }

    public void deny(Player player) {
        if(!isRecipient(player.getName())) return;
        //to sender
        handled.add(player.getName());

        BungeePlayer sender = new BungeePlayer(this.sender);

        if(recipients.length == 1) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
        player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));

        TeleportCommandManager.getInstance().checkDestructionOf(this);
    }

    public void timeOut(String player) {
        if(!isRecipient(player)) return;
        this.handled.add(player);
        TeleportCommandManager.getInstance().checkDestructionOf(this);
    }

    public boolean canBeDestroyed() {
        return recipients.length == handled.size();
    }

    public void destroy() {
        this.handled.clear();
    }

    public void send() {
        send(null);
    }

    public void send(Callback<Integer> callback) {
        //to receiver
        Value<Integer> handled = new Value<>(0);
        Value<Integer> sent = new Value<>(0);

        for(String s : recipients) {
            Player recipient = Bukkit.getPlayer(s);
            if(recipient != null) {
                //on bukkit
                handled.setValue(handled.getValue() + 1);

                if(TeleportCommandManager.getInstance().deniesTpaRequests(recipient.getName())) continue;

                SimpleMessage m = new SimpleMessage(Lang.getPrefix() + Lang.get("TeleportRequest_tpTo" + (toSender ? "Sender" : "Receiver")).replace("%PLAYER%", ChatColor.stripColor(sender)).replace("%SECONDS%", TeleportCommandManager.getInstance().getExpireDelay() + "").replace("%PLAYER%", sender), WarpSystem.getInstance()) {
                    @Override
                    public void onTimeOut() {
                        timeOut(recipient.getName());
                    }
                };

                m.setTimeOut(TeleportCommandManager.getInstance().getExpireDelay());

                TextComponent accept = new TextComponent(Lang.get("Accept"));
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender));

                TextComponent deny = new TextComponent(Lang.get("Deny"));
                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender));

                m.replace("%ACCEPT%", accept);
                m.replace("%DENY%", deny);

                m.send(recipient);

                sent.setValue(sent.getValue() + 1);
            } else {
                //try on bungee

                //todo
            }
        }

        if(callback != null) {
            if(handled.getValue() == recipients.length) {
                callback.accept(sent.getValue());
            }
        }
    }

    public String getSender() {
        return sender;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public boolean isToSender() {
        return toSender;
    }
}
