package de.codingair.warpsystem.bungee.features.randomtp;

import de.codingair.warpsystem.spigot.features.randomteleports.packets.RandomTPWorldsPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class RandomTPListener implements PacketListener, Listener {
    public static final String ID = "§WS-RTP";
    public static final String ID_OTHER = "§WS-RTP-Other";

    private void add(TabCompleteResponseEvent e, String last, String argument) {
        if(last.isEmpty() || argument.startsWith(last)) {
            e.getSuggestions().add(argument);
        }
    }

    private int count(String s, char c) {
        int i = 0;
        for(char c1 : s.toCharArray()) {
            if(c1 == c) i++;
        }
        return i;
    }

    @EventHandler
    public void onResponse(TabCompleteResponseEvent e) {
        if(e.getSuggestions().isEmpty()) return;

        if(e.getSuggestions().remove(ID)) {
            boolean other = e.getSuggestions().remove(ID_OTHER);
            String command = e.getSuggestions().isEmpty() ? "" : e.getSuggestions().remove(0).replace("go ", "");
            if(!RandomTPManager.getInstance().hasRegisteredServers()) return;

            boolean editingLast = !command.endsWith(" ");
            String[] args = command.split(" ", -1);

            int start = command.indexOf('[');
            int semi = command.indexOf(';');
            int end = command.lastIndexOf(']');

            if(start == -1 || end == -1) {
                //data
                if(semi == -1) {
                    String last = args.length == 0 ? "" : args[args.length - 1];

                    //server + local worlds
                    String startSug = start == -1 || args.length <= 1 ? "[" : "";

                    String server = ((ProxiedPlayer) e.getReceiver()).getServer().getInfo().getName();
                    int count = count(command, ',') - (editingLast ? 1 : 0);

                    List<String> worldList = RandomTPManager.getInstance().getWorlds(server);
                    int max = worldList.size();

                    List<String> serverList = RandomTPManager.getInstance().getServer();
                    max = serverList.size();
                    for(String s : serverList) {
                        if(!command.contains(s + ",") && !command.contains(s + ";")) {
                            if(max > 1 && count + 1 < max) {
                                add(e, last, startSug + s + ",");
                            }

                            add(e, last, startSug + s + ";");
                        }
                    }
                    serverList.clear();
                } else if(start != -1) {
                    //worlds of given servers
                    command = command.substring(start + 1).replace(" ", ""); //remove first bracket

                    String[] data = command.split(";");
                    String[] servers = data[0].split(",");
                    String worlds = data.length == 1 ? "" : data[1];

                    args = worlds.split(",", -1);
                    String last = args[args.length - 1];

                    int max = 0;
                    for(String server : servers) {
                        List<String> worldList = RandomTPManager.getInstance().getWorlds(server);
                        max += worldList.size();
                    }

                    for(String server : servers) {
                        List<String> worldList = RandomTPManager.getInstance().getWorlds(server);
                        int count = count(worlds, ',') - (editingLast ? 1 : 0);

                        for(String world : worldList) {
                            String w = server + "@" + world;
                            if(!worlds.contains(w + ",") && !worlds.contains(w + "]")) {
                                if(max > 1 && count + 1 < max) {
                                    add(e, last, w + ",");
                                }

                                add(e, last, w + "]");
                            }
                        }
                    }
                }
            } else if(other && command.length() > end + 1) {
                //player
                String rest = command.substring(end + 2);

                if(rest.contains(" ")) return;
                for(ProxiedPlayer player : ((ProxiedPlayer) e.getReceiver()).getServer().getInfo().getPlayers()) {
                    if(rest.isEmpty() || player.getName().toLowerCase().startsWith(rest)) e.getSuggestions().add(player.getName());
                }
            }
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.RandomTPWorldsPacket) {
            RandomTPWorldsPacket p = (RandomTPWorldsPacket) packet;
            RandomTPManager.getInstance().addWorldData(extra, p.getWorlds());
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
