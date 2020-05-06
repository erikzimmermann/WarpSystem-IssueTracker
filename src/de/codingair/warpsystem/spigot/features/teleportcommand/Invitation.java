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
    private String recipient; //notify if receiver != null | if recipient == null ? ALL
    private int recipients = 1;
    private boolean toSender; //only send if receiver.length == 1
    private List<String> handled = new ArrayList<>();
    private boolean bukkitOnly;

    protected Invitation(String sender, boolean bukkitOnly) {
        this.sender = sender;
        this.recipient = null;
        this.toSender = true;
        this.recipients = Bukkit.getOnlinePlayers().size();
        this.bukkitOnly = bukkitOnly;
    }

    protected Invitation(String sender, boolean toSender, String recipient, boolean bukkitOnly) {
        this.sender = sender;
        this.toSender = toSender;
        this.recipient = recipient;
        this.bukkitOnly = bukkitOnly;
    }

    public boolean isRecipient(String player) {
        return !sender.equalsIgnoreCase(player) && (recipient == null || recipient.equalsIgnoreCase(player));
    }

    public void handle(String recipient) {
        handled.add(recipient);
        TeleportCommandManager.getInstance().checkDestructionOf(this);
    }

    public void accept(Player player) {
        if(!isRecipient(player.getName())) return;
        //to sender
        handle(player.getName());

        BungeePlayer sender = new BungeePlayer(this.sender);

        if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
        } else {
            if(sender.onSpigot()) {
                if(sender.getSpigotPlayer().isOnline()) {
                    if(recipient != null)
                        sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
                    player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_accepted_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));

                    TeleportOptions options = new TeleportOptions(toSender ? sender.getSpigotPlayer().getLocation() : player.getLocation(), toSender ? sender.getName() : player.getName());
                    options.setOrigin(Origin.CustomTeleportCommands);
                    options.setWaitForTeleport(true);
                    options.setCosts(TeleportCommandManager.getInstance().getTpaCosts());

                    WarpSystem.getInstance().getTeleportManager().teleport(toSender ? player : sender.getSpigotPlayer(), options);
                } else {
                    player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
                }
            }
        }
    }

    public void deny(Player player) {
        if(!isRecipient(player.getName())) return;
        //to sender
        handle(player.getName());

        BungeePlayer sender = new BungeePlayer(this.sender);

        if(recipient != null) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(player.getName())));
        player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_other").replace("%PLAYER%", ChatColor.stripColor(sender.getName())));
    }

    public void timeOut(String player) {
        if(!isRecipient(player)) return;
        handle(player);
    }

    public boolean canBeDestroyed() {
        return handled.size() >= recipients;
    }

    public void destroy() {
        this.handled.clear();
        recipients = 0;
    }

    public void send() {
        send(new Callback<Long>() {
            @Override
            public void accept(Long object) {
            }
        });
    }

    public void send(Callback<Long> callback) {
        //to receiver
        Value<Integer> handled = new Value<>(0);
        Value<Integer> sent = new Value<>(0);

        if(recipient == null) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getName().equalsIgnoreCase(sender)) continue;

                sendInvitation(player.getName(), new Callback<Long>() {
                    @Override
                    public void accept(Long result) {
                        handled.setValue(handled.getValue() + (int) (result >> 32));
                        sent.setValue(sent.getValue() + result.intValue());
                    }
                });
            }

            callback.accept((((long) handled.getValue()) << 32) | (sent.getValue() & 0xffffffffL));
        } else {
            sendInvitation(this.recipient, new Callback<Long>() {
                @Override
                public void accept(Long result) {
                    int handled = (int) (result >> 32);
                    int sent = result.intValue();

                    if(handled == 0 || sent == 0) Invitation.this.handled.add(recipient);
                    callback.accept(result);
                }
            });
        }
    }

    private void sendInvitation(String player, Callback<Long> callback) {
        Player recipient = Bukkit.getPlayerExact(player);
        if(recipient != null) {
            //on bukkit
            if(TeleportCommandManager.getInstance().deniesTpaRequests(recipient.getName())) {
                callback.accept((((long) 1) << 32));
                return;
            }

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

            callback.accept((((long) 1) << 32) | (1 & 0xffffffffL));
        } else callback.accept(0L);
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isToSender() {
        return toSender;
    }
}
