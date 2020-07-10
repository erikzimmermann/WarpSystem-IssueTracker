package de.codingair.warpsystem.spigot.features.portals.guis.subgui;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.components.SyncItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.chatinput.ChatInputEvent;
import de.codingair.warpsystem.spigot.api.chatinput.ChatInputGUI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.animationseditor.AnimationHotBarEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.Hologram;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HologramEditor extends HotbarGUI {
    private final PortalEditor fallBack;
    private final Hologram hologram;

    private BukkitRunnable alignRunnable;
    private final List<Location> alignTo = new ArrayList<>();
    private final boolean show = true;

    public HologramEditor(Player player, PortalEditor fallBack, Hologram hologram) {
        super(player, WarpSystem.getInstance(), 2);

        this.fallBack = fallBack;
        this.hologram = hologram;

        this.alignRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!show) return;

                for(Location location : alignTo) {
                    Particle.VILLAGER_HAPPY.send(location, player);
                }
            }
        };

        this.alignRunnable.runTaskTimer(WarpSystem.getInstance(), 5, 5);

        initialize();
    }

    public static Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if(d == (int) d) return (int) d;
        else return d;
    }

    private Location calculateMid() {
        List<Integer> yValues = new ArrayList<>();
        Location l = null;

        for(Location location : this.alignTo) {
            if(l == null) {
                yValues.add(location.getBlockY());
                l = location.clone();
            } else {
                l.add(location);
                if(!yValues.contains(location.getBlockY())) {
                    yValues.add(location.getBlockY());
                } else l.subtract(0, location.getY(), 0);
            }
        }

        if(l == null) return null;

        l.setX(l.getX() / alignTo.size());
        l.setY(l.getY() / yValues.size());
        l.setZ(l.getZ() / alignTo.size());

        yValues.clear();
        return l;
    }

    private void setAlignBlocks(boolean show) {
        List<Location> alignTo = new ArrayList<>(this.alignTo);

        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if(show) {
                for(Location location : alignTo) {
                    changeToAlignmentBlock(getPlayer(), location);
                }
            } else {
                for(Location location : alignTo) {
                    sendBlockChange(getPlayer(), location.getBlock());
                }
            }
        }, 1);
    }

    private void sendBlockChange(Player player, Block b) {
        if(Version.get().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getBlockData", blockDataClass, new Class[0]);

            sendBlockChange.invoke(player, b.getLocation(), getData.invoke(b));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, Material.class, byte.class});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getData", byte.class, new Class[0]);
            sendBlockChange.invoke(player, b.getLocation(), b.getType(), getData.invoke(b));
        }
    }

    private void changeToAlignmentBlock(Player player, Location loc) {
        if(Version.get().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor createBlockData = IReflection.getMethod(Material.class, "createBlockData", blockDataClass, new Class[] {});

            sendBlockChange.invoke(player, loc, createBlockData.invoke(XMaterial.GLASS.parseMaterial()));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, Material.class, byte.class});
            sendBlockChange.invoke(player, loc, XMaterial.GLASS.parseMaterial(), (byte) 1);
        }
    }

    @Override
    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                close(false);

                if(alignRunnable != null) {
                    alignRunnable.cancel();
                    alignRunnable = null;

                    for(Location l : alignTo) {
                        sendBlockChange(player, l.getBlock());
                    }
                    alignTo.clear();
                }

                fallBack.updatePage();
                fallBack.open();
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        String pos = hologram.getLocation() == null ? "§c-" : "x=" + cut(hologram.getLocation().getX()) + ", y=" + cut(hologram.getLocation().getY()) + ", z=" + cut(hologram.getLocation().getZ());

        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.ENDER_EYE).setName("§7" + Lang.get("Position") + ": §e" + pos).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK || clickType == ClickType.SHIFT_LEFT_CLICK) {
                    setAlignBlocks(false);
                    alignTo.clear();

                    hologram.setLocation(new Location(player.getEyeLocation()));
                    hologram.setVisible(true);
                    hologram.update();

                    updateSingle(5);

                    String pos = "x=" + cut(hologram.getLocation().getX()) + ", y=" + cut(hologram.getLocation().getY()) + ", z=" + cut(hologram.getLocation().getZ());
                    updateDisplayName(getItem(2), "§7" + Lang.get("Position") + ": §e" + pos);
                } else if(clickType == ClickType.RIGHT_CLICK || clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    Block b = player.getTargetBlock((Set<Material>) null, 10);
                    if(b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial()) {
                        Location l = new Location(b.getLocation()).add(0.5, 0.5, 0.5);
                        boolean removed = alignTo.remove(l);

                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                            if(!removed) {
                                alignTo.add(l);
                                changeToAlignmentBlock(player, l);
                            } else sendBlockChange(player, l.getBlock());

                            Location newL = calculateMid();
                            if(newL != null) {
                                hologram.setLocation(newL);
                                hologram.setVisible(true);
                                hologram.update();

                                updateSingle(5);

                                String pos = "x=" + cut(hologram.getLocation().getX()) + ", y=" + cut(hologram.getLocation().getY()) + "z=" + cut(hologram.getLocation().getZ());
                                updateDisplayName(getItem(2), "§7" + Lang.get("Position") + ": §e" + pos);
                            }
                        }, 1);
                    }
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), AnimationHotBarEditor.ACTION_BAR(Lang.get("Position"), Lang.get("Set"), Lang.get("Align_to_block")), WarpSystem.getInstance(), Integer.MAX_VALUE);
                setAlignBlocks(true);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
                setAlignBlocks(false);
            }
        }));

        String text = hologram.getText();
        if(text != null && text.length() > 60) text = text.substring(0, 60) + "§f...";
        setItem(3, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7" + Lang.get("Hologram_Text") + ": " + (hologram.getText() == null ? "§c-" : "'§f" + ChatColor.translateAlternateColorCodes('&', text) + "§7'")).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType != ClickType.LEFT_CLICK) return;
                //Start-Name

                MessageAPI.stopSendingActionBar(getPlayer());
                TextComponent tc = new TextComponent(Lang.getPrefix() + "§7" + Lang.get("Hologram_Text") + ": ");
                TextComponent click = new TextComponent("§e" + ChatColor.stripColor(Lang.get("Click_Hover")));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
                click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, hologram.getText()));

                tc.addExtra(click);
                player.spigot().sendMessage(tc);

                new ChatInputGUI(player, WarpSystem.getInstance()) {
                    @Override
                    public void onEnter(ChatInputEvent e) {
                        hologram.setText(e.getText());
                        hologram.update();

                        String text = e.getText();
                        if(text.length() > 60) text = text.substring(0, 60) + "§f...";
                        updateDisplayName(ic, "§7" + Lang.get("Hologram_Text") + ": '§f" + ChatColor.translateAlternateColorCodes('&', text) + "§7'");
                    }

                    @Override
                    public void onClose() {
                        MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                    }
                }.open();
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.STICK).setName("§7" + Lang.get("Hologram_Height") + ": §e" + hologram.getHeight()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    hologram.setHeight(hologram.getHeight() - 0.1);
                } else if(clickType == ClickType.SHIFT_LEFT_CLICK) {
                    hologram.setHeight(hologram.getHeight() - 1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    hologram.setHeight(hologram.getHeight() + 0.1);
                } else if(clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    hologram.setHeight(hologram.getHeight() + 1);
                }

                hologram.update();
                updateDisplayName(getItem(4), "§7" + Lang.get("Hologram_Height") + ": §e" + hologram.getHeight());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(player, AnimationHotBarEditor.MINUS_PLUS_SHIFT(Lang.get("Hologram_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }));

        setItem(5, new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType != ClickType.LEFT_CLICK) return;
                hologram.setVisible(!hologram.isVisible());
                hologram.update();
                updateSingle(5);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Toggle"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(hologram.isVisible() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA)
                        .setName(ChatColor.GRAY + Lang.get("Status") + ": " +
                                (hologram.isVisible() ? ChatColor.GREEN + Lang.get("Enabled") :
                                        ChatColor.RED + Lang.get("Disabled"))).getItem();
            }
        });
    }
}
