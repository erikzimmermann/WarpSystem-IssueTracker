package de.codingair.warpsystem.spigot.base.ad;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementManager {
    private int last = -1;
    private List<Integer> ids = new ArrayList<>();
    private List<SimpleMessage> messages = new ArrayList<>();

    public AdvertisementManager() {
        messages.add(new MESSAGE_01());
        messages.add(new MESSAGE_02());

        getId();
        runAdvertiser();
    }

    public int getId() {
        if(ids.isEmpty()) {
            for(int i = 0; i < messages.size(); i++) {
                ids.add(i);
            }
        }

        int i = (int) (Math.random() * ids.size());
        while(ids.get(i) == last) {
            i = (int) (Math.random() * ids.size());
        }

        return last = ids.remove(i);
    }

    private void runAdvertiser() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(WarpSystem.getInstance(), () -> {
            int id = getId();

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.isOp()) send(id, player);
                else for(PermissionAttachmentInfo p : player.getEffectivePermissions()) {
                    if(p.getPermission().toLowerCase().startsWith(WarpSystem.PERMISSION_MODIFY.toLowerCase())) {
                        send(id, player);
                        break;
                    }
                }
            }
        }, 20 * 60 * 60, 20 * 60 * 60); //all 60 minutes
    }

    private void send(int id, Player player) {
        messages.get(id).send(player);
    }

    private class MESSAGE_01 extends SimpleMessage {
        public MESSAGE_01() {
            super(""
                            + "§6§m                                                    \n"
                            + " \n"
                            + "    §3§nWarpSystem§7 §8[§bFree§8]\n"
                            + " \n"
                            + "    §7Hey there, did you know, that you\n"
                            + "    §7use the §blimited free §7edition?\n"
                            + " \n"
                            + "    §7» %HERE%§7 «\n"
                            + " \n"
                            + "§6§m                                                    ",
                    WarpSystem.getInstance());

            TextComponent link = new TextComponent("§6Upgrade now!");
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
            link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(""
                    + "  §6Premium features:§r\n"
                    + "  §7» MUCH better editors\n"
                    + "  §7» §6Weekly§7 updates\n"
                    + "  §7» Tons of new settings\n"
                    + "  §7» No ads\n"
                    + "\n"
                    + "  §7Feel free to contact me via §nDiscord§r  \n"
                    + "  §7to get a §6free demo§7!\n"
                    + "\n"
                    + "  §7Thank you for your support!§r    \n"
                    + "\n"
                    + "  §3CodingAir§7§n#0281§r"
            )}));

            replace("%HERE%", link);
        }
    }

    private class MESSAGE_02 extends SimpleMessage {
        public MESSAGE_02() {
            super(""
                            + "§6§m                                                 \n"
                            + " \n"
                            + "    §3§nWarpSystem§7 §8[§bFree§8]\n"
                            + " \n"
                            + "    §7Do you like the free edition?\n"
                            + "    §7Get professional with premium!\n"
                            + " \n"
                            + "    §7» %HERE%§7 «\n"
                            + " \n"
                            + "§6§m                                                 ",
                    WarpSystem.getInstance());

            TextComponent link = new TextComponent("§6Upgrade now!");
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
            link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(""
                    + "  §6Premium features:§r\n"
                    + "  §7» MUCH better editors\n"
                    + "  §7» §6Weekly§7 updates\n"
                    + "  §7» Tons of new settings\n"
                    + "  §7» No ads\n"
                    + "\n"
                    + "  §7Feel free to contact me via §nDiscord§r  \n"
                    + "  §7to get a §6free demo§7!\n"
                    + "\n"
                    + "  §7Thank you for your support!§r    \n"
                    + "\n"
                    + "  §3CodingAir§7§n#0281§r"
            )}));

            replace("%HERE%", link);
        }
    }
}
