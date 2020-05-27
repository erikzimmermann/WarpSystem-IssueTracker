package de.codingair.warpsystem.spigot.features.randomteleports.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.data.PacketReader;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.QueueRTPUsagePacket;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.RandomTPPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnListener implements Listener, PacketListener {
    private static final String PACKET_READER_NAME = "RTP_LISTENER";
    private final List<Class<?>> forwarding = new ArrayList<>();
    private HashMap<String, List<Object>> packetCache = new HashMap<>();
    private HashMap<String, Node<World, String>> teleporting = new HashMap<>();
    private Class<?> chunkPacket = null;

    public SpawnListener() {
        try {
            Class<?> o = IReflection.getSaveClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutChat");
            if(o != null) forwarding.add(o);
        } catch(ClassNotFoundException ignored) {
        }

        try {
            Class<?> o = IReflection.getSaveClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutCustomPayload");
            if(o != null) forwarding.add(o);
        } catch(ClassNotFoundException ignored) {
        }

        try {
            chunkPacket = IReflection.getSaveClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutMapChunk");
        } catch(ClassNotFoundException ignored) {
        }
    }

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        Node<World, String> node = teleporting.get(e.getPlayer().getName());
        if(node != null) triggerRTP(e.getPlayer(), node);
    }

    private void triggerRTP(Player player, Node<World, String> node) {
        if(node.getKey() != null) {
            RandomTeleporterManager.getInstance().search(player, node.getKey(), new Callback<Location>() {
                @Override
                public void accept(Location loc) {
                    if(loc == null) {
                        showPlayer(player);
                        uninject(player);
                        teleporting.remove(player.getName());
                        clearCache(player, true);
                        player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                    } else {
                        WarpSystem.getInstance().getDataHandler().send(new QueueRTPUsagePacket(WarpSystem.getInstance().getUUIDManager().get(player), node.getValue()));

                        org.bukkit.Location l = player.getLocation();
                        boolean discardOldChunk = !l.getWorld().equals(loc.getWorld()) || l.distance(loc) > 250;
                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                            TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(loc)), "");
                            options.setMessage(Lang.get("RandomTP_Teleported"));
                            options.setSkip(true);
                            options.addCallback(new Callback<TeleportResult>() {
                                @Override
                                public void accept(TeleportResult object) {
                                    clearCache(player, !discardOldChunk);
                                    showPlayer(player);
                                }
                            });

                            uninject(player);
                            teleporting.remove(player.getName());
                            WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                        });
                    }
                }
            });
        } else {
            teleporting.remove(player.getName());
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists")), 2L);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        hidePlayer(e.getPlayer());
    }

    private void hidePlayer(Player player) {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.equals(player)) continue;
            onlinePlayer.hidePlayer(player);
        }
    }

    private void showPlayer(Player player) {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.equals(player)) continue;
            onlinePlayer.showPlayer(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        inject(e.getPlayer());

        if(teleporting.containsKey(e.getPlayer().getName())) return;

        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if(injected(e.getPlayer()) && !teleporting.containsKey(e.getPlayer().getName())) {
                //timeOut
                showPlayer(e.getPlayer());
                uninject(e.getPlayer());
                clearCache(e.getPlayer(), true);
            }
        }, 2L);
    }

    private void clearCache(Player player, boolean oldChunk) {
        List<Object> l = packetCache.remove(player.getName());

        if(l != null) {
            for(Object o : l) {
                if(!oldChunk && o.getClass().equals(chunkPacket)) continue;
                PacketUtils.sendPacket(player, o);
            }

            l.clear();
        }
    }

    private void inject(Player player) {
        List<Object> l = new ArrayList<>();
        packetCache.put(player.getName(), l);
        new PacketReader(player, PACKET_READER_NAME, WarpSystem.getInstance()) {
            @Override
            public boolean readPacket(Object packet) {
                return false;
            }

            @Override
            public boolean writePacket(Object packet) {
                if(forwarding.contains(packet.getClass())) return false;
                l.add(packet);
                return true;
            }
        }.inject();
    }

    private void uninject(Player player) {
        PacketReader r = getReader(player);
        if(r != null) r.unInject();
    }

    private PacketReader getReader(Player player) {
        List<PacketReader> l = API.getRemovables(player, PacketReader.class);

        PacketReader found = null;
        for(PacketReader r : l) {
            if(r.getName().equals(PACKET_READER_NAME)) {
                found = r;
                break;
            }
        }

        l.clear();
        return found;
    }

    public boolean injected(Player player) {
        return getReader(player) != null;
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.RandomTPPacket) {
            RandomTPPacket p = (RandomTPPacket) packet;
            World w = Bukkit.getWorld(p.getWorld());

            Player player = Bukkit.getPlayer(p.getPlayer());
            Node<World, String> node = new Node<>(w, p.getServer());

            if(player == null || !WarpSystem.getInstance().getUUIDManager().isRegistered(player)) teleporting.put(p.getPlayer(), node);
            else triggerRTP(player, node);
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
