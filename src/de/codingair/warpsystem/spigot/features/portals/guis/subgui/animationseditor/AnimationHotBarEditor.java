package de.codingair.warpsystem.spigot.features.portals.guis.subgui.animationseditor;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.utils.Color;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.Animation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnimationHotBarEditor extends HotbarGUI {
    private final PortalEditor fallBack;
    private final Animation animation;
    private final ParticleOptions options;

    private BukkitRunnable alignRunnable;
    private final List<Location> alignTo = new ArrayList<>();
    private boolean show = true;

    public AnimationHotBarEditor(Player player, PortalEditor fallBack, Animation animation) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F));
        setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.5F, 1F));

        this.animation = animation;
        this.animation.setVisible(true);
        this.fallBack = fallBack;
        options = new ParticleOptions(player, this);

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

    public static String MINUS_PLUS(String s) {
        return ACTION_BAR(s, "-", "+");
    }

    public static String MINUS_PLUS_SHIFT(String s) {
        return ACTION_BAR(s, "§7(§e" + Lang.get("Shift") + "§7) §e-", "+ §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String PREVIOUS_NEXT(String s) {
        return ACTION_BAR(s, "«", "»");
    }

    public static String PREVIOUS_NEXT_SHIFT(String s) {
        return ACTION_BAR(s, "§7(§e" + Lang.get("Shift") + "§7) §e«", "» §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String ACTION_BAR(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    public static Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if(d == (int) d) return (int) d;
        else return d;
    }

    @Override
    public void open(boolean sound) {
        super.open(sound);
        setStartSlot(2);
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
        this.show = show;
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

        String pos = animation.getLocation() == null ? "§c-" : "x=" + cut(animation.getLocation().getX()) + ", y=" + cut(animation.getLocation().getY()) + "z=" + cut(animation.getLocation().getZ());
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.ENDER_EYE).setName("§7" + Lang.get("Position") + ": §e" + pos).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK || clickType == ClickType.SHIFT_LEFT_CLICK) {
                    setAlignBlocks(false);
                    alignTo.clear();

                    if(animation.getEffect().getHeight() == 0) animation.getEffect().setHeight(1);
                    animation.setLocation(new Location(player.getLocation()));
                    animation.update();

                    String pos = animation.getLocation() == null ? "§c-" : "x=" + cut(animation.getLocation().getX()) + ", y=" + cut(animation.getLocation().getY()) + "z=" + cut(animation.getLocation().getZ());
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
                                animation.getEffect().setHeight(0);
                                animation.setLocation(newL);
                                animation.update();

                                String pos = animation.getLocation() == null ? "§c-" : "x=" + cut(animation.getLocation().getX()) + ", y=" + cut(animation.getLocation().getY()) + "z=" + cut(animation.getLocation().getZ());
                                updateDisplayName(getItem(2), "§7" + Lang.get("Position") + ": §e" + pos);
                            }
                        }, 1);
                    }
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), ACTION_BAR(Lang.get("Position"), Lang.get("Set"), Lang.get("Align_to_block")), WarpSystem.getInstance(), Integer.MAX_VALUE);
                setAlignBlocks(true);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
                setAlignBlocks(false);
            }
        }));

        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.NETHER_STAR).setName("§7" + Lang.get("Particle_Effect") + ": '§e" + getParticleName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setParticle(getPart().getParticle().previous(true));
                } else if(clickType == ClickType.SHIFT_LEFT_CLICK) {
                    getPart().setParticle(getPart().getParticle().previous(true, true));
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setParticle(getPart().getParticle().next(true));
                } else if(clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    getPart().setParticle(getPart().getParticle().next(true, true));
                } else return;

                animation.update();
                updateDisplayName(ic, "§7" + Lang.get("Particle_Effect") + ": '§e" + getParticleName() + "§7'" + (getPart().getParticle().isColorable() ? " §7(§a" + Lang.get("Colorable") + "§7)" : ""));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PREVIOUS_NEXT_SHIFT(Lang.get("Particle_Effect")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.COMMAND_BLOCK).setName("§7» §c" + Lang.get("Options") + "§7 «").getItem()).setLink(this.options), false);
        setItem(5, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7» §c" + Lang.get("Rotation") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                Lang.PREMIUM_CHAT(player);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(player, Lang.PREMIUM_HOTBAR, WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }), false);

        this.options.initialize();
    }

    private Color getColor() {
        return getPart() == null ? null : getPart().getColor();
    }

    private String getParticleName() {
        String s = getPart() == null ? null : getPart().getParticle() == null ? null : getPart().getParticle().name();
        if(s == null) return null;

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    ParticlePart getPart() {
        return animation.getEffect();
    }

    public Animation getAnimation() {
        return animation;
    }
}
