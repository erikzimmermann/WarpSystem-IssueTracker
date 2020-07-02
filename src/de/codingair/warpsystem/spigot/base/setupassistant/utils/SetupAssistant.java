package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import com.google.common.collect.EvictingQueue;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.player.data.PacketReader;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.setupassistant.SetupAssistantManager;
import de.codingair.warpsystem.spigot.base.utils.PluginVersion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetupAssistant {
    private static final String CHANGE_PREFIX = "%s: ";
    private final Player player;
    private final HashMap<String, List<Value>> hierarchy = new HashMap<>();
    private final List<String> priority = new ArrayList<>();
    private List<Value> values;
    private final boolean general;
    private PluginVersion filter = null;

    private PacketReader reader;
    private final EvictingQueue<Object> queue = EvictingQueue.create(17);
    private String nav = null;
    private int page = 0, typePage;

    public SetupAssistant(Player player, List<Value> values, boolean general) {
        this.player = player;
        this.values = values;
        this.general = general;
        buildHierarchy();

        Class<?> oPacketClass = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutChat");
        Class<?> iPacketClass = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayInChat");
        IReflection.FieldAccessor<BaseComponent[]> components = IReflection.getField(oPacketClass, "components");
        IReflection.FieldAccessor<String> a = IReflection.getField(iPacketClass, "a");

        reader = new PacketReader(player, "WS-SetupAssistant", WarpSystem.getInstance()) {
            @Override
            public boolean readPacket(Object packet) {
                if(packet.getClass().equals(iPacketClass)) {
                    onChat(a.get(packet));
                    return true;
                }
                return false;
            }

            @Override
            public boolean writePacket(Object packet) {
                if(packet.getClass().equals(oPacketClass)) {
                    BaseComponent[] c = components.get(packet);

                    if(c != null && c.length == 1) {
                        String s = c[0].toLegacyText();
                        if(s.startsWith("§f")) s = s.substring(2);

                        //identifier
                        if(s.startsWith("§§§§")) return false;
                    }

                    //queue for later
                    queue.add(packet);
                    return true;
                } else return false;
            }
        };
        reader.inject();

        process("");
    }

    private void buildHierarchy() {
        hierarchy.values().forEach(List::clear);
        hierarchy.clear();
        priority.clear();

        String general = ChatColor.stripColor(Lang.get("General").trim());
        for(Value v : values) {
            List<Value> l = hierarchy.get(v.getType());
            if(l == null) {
                l = new ArrayList<>();
                hierarchy.put(v.getType(), l);

                if(v.getType().equals(general)) priority.add(0, v.getType());
                else priority.add(v.getType());
            }

            l.add(v);
        }
    }

    private boolean requiresReload() {
        for(Value value : values) {
            if(value.hasBeenChanged()) return true;
        }
        return false;
    }

    public void onQuit() {
        quit(false);
    }

    private void quit(boolean sendMessage) {
        if(reader == null) return;

        reader.unInject();
        reader = null;

        if(sendMessage)
            for(Object o : queue) {
                PacketUtils.sendPacket(player, o);
            }
        queue.clear();

        if(sendMessage) {
            TextComponent base = new TextComponent("\n\n" + Lang.getPrefix() + Lang.get("SetupAssistant_Closed") + " §8[");
            TextComponent extra = new TextComponent("§c" + Lang.get("Back").toLowerCase());
            extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§7» §e" + Lang.get("Start") + " §8(§7/warpsystem§8)")}));
            extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warpsystem setupassistant"));
            base.addExtra(extra);
            base.addExtra(new TextComponent("§8]"));
            player.spigot().sendMessage(base);
        }

        SetupAssistantManager.getInstance().clearAssistant();

        for(List<Value> value : hierarchy.values()) {
            value.clear();
        }
        hierarchy.clear();
        if(requiresReload()) WarpSystem.getInstance().reload(true);
    }

    public void onChat(String message) {
        if(message.contains(":")) {
            String name = message.split(":")[0];
            message = message.replace(name + ":", "");
            if(message.charAt(0) == ' ') message = message.substring(1);

            if(!change(name, message)) process(nav, Lang.getPrefix() + Lang.get("SetupAssistant_Wrong_Format"));
        } else if(message.startsWith("/")) {
            process(nav, Lang.getPrefix() + Lang.get("SetupAssistant_Still_Active"));
        } else process(nav, Lang.getPrefix() + Lang.get("SetupAssistant_Wrong_Format"));
    }

    private boolean change(String name, String value) {
        List<Value> l = hierarchy.get(nav);
        if(l == null) return false;

        Value v = null;
        for(Value value1 : l) {
            if(value1.getName().equalsIgnoreCase(name)) {
                v = value1;
                break;
            }
        }

        if(v == null) return false;
        if(v.set(value)) {
            process(nav);
        } else {
            process(nav, Lang.getPrefix() + Lang.get("SetupAssistant_Wrong_Format"));
        }
        return true;
    }

    protected void process(String arg) {
        process(arg, null);
    }

    protected void process(String arg, String warning) {
        nav = arg;
        List<Value> l = hierarchy.get(arg);

        //identifier: §§§§
        SimpleMessage m = new SimpleMessage("§§§§", WarpSystem.getInstance());

        m.add("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n§8§m                                            §r\n  §6§n§lSetup-Assistant");
        if(general) m.add("§8 - §e" + Lang.get("All"));
        else m.add("§8 - §e" + Lang.get("New"));

        if(general) {
            if(filter == null) {
                m.add("§8 [");
                m.add(new WSChatButton("§e" + Lang.get("Filter"), "§8» §7" + Lang.get("Add_Filter")) {
                    @Override
                    public void onClick(Player player) {
                        filter = PluginVersion.getCurrent();
                        page = 0;
                        typePage = 0;
                        values = SetupAssistantManager.getInstance().getFunction(filter);
                        buildHierarchy();
                        m.destroy();
                        process(nav);
                    }
                });
                m.add("§8]");
            } else {
                m.add("§8 -");
                m.add(new WSChatButton("§e « ", "§8» §e" + Lang.get("Previous_Version")) {
                    @Override
                    public void onClick(Player player) {
                        filter = filter.previous();
                        page = 0;
                        typePage = 0;
                        values = SetupAssistantManager.getInstance().getFunction(filter);
                        buildHierarchy();
                        m.destroy();
                        process(nav);
                    }
                });

                m.add("§7" + Lang.get("Since").toLowerCase() + " ");
                m.add(new WSChatButton("§c§n" + filter.toString(), new ArrayList<String>() {{
                    add("§7" + Lang.get("Comparing_To") + ": §ev" + WarpSystem.getInstance().getDescription().getVersion());
                    add("§8» §c" + Lang.get("Reset_Filter"));
                }}) {
                    @Override
                    public void onClick(Player player) {
                        filter = null;
                        page = 0;
                        typePage = 0;
                        values = SetupAssistantManager.getInstance().getFunction(filter);
                        buildHierarchy();
                        m.destroy();
                        process(nav);
                    }
                });

                m.add(new WSChatButton("§e » ", "§8» §e" + Lang.get("Next_Version")) {
                    @Override
                    public void onClick(Player player) {
                        filter = filter.next();
                        page = 0;
                        typePage = 0;
                        values = SetupAssistantManager.getInstance().getFunction(filter);
                        buildHierarchy();
                        m.destroy();
                        process(nav);
                    }
                });
            }
        }

        m.add("\n\n  §7« ");
        m.add(new WSChatButton(nav.isEmpty() && l == null ? "§a§n" + Lang.get("Close").toLowerCase() : "§c§n" + Lang.get("Back").toLowerCase(), Lang.get("Changes_Auto_Save")) {
            @Override
            public void onClick(Player player) {
                if(nav.isEmpty() && l == null) quit(true);
                else {
                    page = 0;
                    process("");
                }
                m.destroy();
            }
        });
        m.add("§7 | §e" + (nav.isEmpty() && l == null ? Lang.get("Menu") : arg) + "\n\n\n\n\n  §8- §7" + Lang.get("Requires_Plugin_Reload") + ": " + (requiresReload() ? "§c" + Lang.get("Yes") : "§a" + Lang.get("No")) + "\n\n");

        m.add("§8§m           ");
        //prev
        m.add(new WSChatButton(((l == null ? typePage : page) == 0 ? "§7" : "§e") + " « ", ((l == null ? typePage : page) == 0 ? "§7" + Lang.get("No_Page_Available") : "§7» §e" + Lang.get("Previous_Page"))) {
            @Override
            public void onClick(Player player) {
                if(l == null) typePage--;
                else page--;
                process(nav);
            }

            @Override
            public boolean canClick() {
                return (l == null ? typePage : page) > 0;
            }
        });

        int maxPage = ((l == null ? hierarchy.size() : l.size()) - 1) / 6;

        //info
        m.add("§8| §e" + Lang.get("Page") + " " + ((l == null ? typePage : page) + 1) + "§7/§e" + (maxPage + 1) + " §8|");

        //next
        m.add(new WSChatButton(((l == null ? typePage : page) == maxPage ? "§7" : "§e") + " » ", ((l == null ? typePage : page) == maxPage ? "§7" + Lang.get("No_Page_Available") : "§7» §e" + Lang.get("Next_Page"))) {
            @Override
            public void onClick(Player player) {
                if(l == null) typePage++;
                else page++;
                process(nav);
            }

            @Override
            public boolean canClick() {
                return (l == null ? typePage : page) < maxPage;
            }
        });
        m.add("§8§m           \n\n");

        if(l == null) {
            //menu
            if(filter == PluginVersion.getUpcoming()) {
                m.add("  §7- §cComing soon...\n");
            } else if(priority.isEmpty()) {
                m.add("  §7- §c" + Lang.get("No_Values_Available") + "\n");
            } else {
                priority.stream().skip(typePage * 6).limit(6).forEach(s -> {
                    m.add("  §7- §e" + s + " §8[");
                    m.add(new WSChatButton("§c" + Lang.get("Edit").toLowerCase(), "§7» §e" + s) {
                        @Override
                        public void onClick(Player player) {
                            process(s);
                            m.destroy();
                        }
                    });
                    m.add("§8]\n");
                });
            }
        } else {
            l.stream().skip(page * 6).limit(6).forEach(value -> {
                Object v = value.getCurrentValue();
                m.add("  §7- ");

                TextComponent info = new TextComponent((value.getDescription().isEmpty() ? "§e" : "§e§o") + value.getName());
                if(!value.getDescription().isEmpty()) {
                    List<String> description = TextAlignment.lineBreak(value.getDescription(), 200);
                    BaseComponent[] components = new BaseComponent[description.size()];
                    for(int i = 0; i < description.size(); i++) {
                        components[i] = new TextComponent(description.get(i));
                    }
                    info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components));
                }
                m.add(info);

                m.add("§7: '§f" + v + "§7' §8[");

                if(value.getValueClass().equals(Boolean.class)) {
                    m.add(new WSChatButton("§c" + Lang.get("Toggle").toLowerCase(), "§7» §e" + value.getName()) {
                        @Override
                        public void onClick(Player player) {
                            value.set((!(boolean) value.getCurrentValue()) + "");
                            process(nav);
                            m.destroy();
                        }
                    });
                } else {
                    TextComponent extra = new TextComponent("§c" + Lang.get("Change").toLowerCase());
                    extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§7» §e" + value.getName())}));
                    extra.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format(CHANGE_PREFIX, value.getName()) + v));
                    m.add(extra);
                }

                m.add("§8]\n");
            });
        }

        while(m.size() < 58) {
            m.add("\n");
        }

        m.add((warning == null ? "\n" : "") + "§8§m                                            ");
        if(warning != null) m.add("\n" + warning);

        m.send(player);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Value> getValues() {
        return values;
    }
}
