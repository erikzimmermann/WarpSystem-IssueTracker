package de.codingair.warpsystem.spigot.features.animations.guis.editor;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.utils.Color;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import org.bukkit.entity.Player;

public class AnimationPart extends HotbarGUI {
    private Menu menu;
    private ParticleRotation rotation;
    private int slot;

    public AnimationPart(Player player, int slot, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.menu = menu;
        rotation = new ParticleRotation(player, slot, menu);
        this.slot = slot;
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu.getParticles()), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.BEACON).setName("§7" + Lang.get("Animation_Type") + ": '§e" + getAnimationName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setAnimation(getPart().getAnimation().previous());
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setAnimation(getPart().getAnimation().next());
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Animation_Type") + ": '§e" + getAnimationName() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT(Lang.get("Animation_Type")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
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

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Particle_Effect") + ": '§e" + getParticleName() + "§7'");
                updateDisplayName(getItem(4), "§7" + Lang.get("Color") + ": §e" + (getColor() == null || !getPart().getParticle().isColorable() ? "§c-" : getColor().getName()));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT_SHIFT(Lang.get("Particle_Effect")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.CYAN_DYE).setName("§7" + Lang.get("Color") + ": §e" + (getColor() == null || !getPart().getParticle().isColorable() ? "§c-" : getColorName())).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(!getPart().getParticle().isColorable()) return;

                if(clickType == ClickType.LEFT_CLICK) {
                    if(getPart().getColor() == null) getPart().setColor(Color.RED);
                    else getPart().setColor(getColor().previous());
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    if(getPart().getColor() == null) getPart().setColor(Color.RED);
                    else getPart().setColor(getColor().next());
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Color") + ": §e" + (getColor() == null || !getPart().getParticle().isColorable() ? "§c-" : getColorName()));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                if(getPart().getParticle().isColorable()) {
                    MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT(Lang.get("Particle_Effect")), WarpSystem.getInstance(), Integer.MAX_VALUE);
                } else {
                    MessageAPI.sendActionBar(getPlayer(), "§c" + Lang.get("ParticleType_Doesnt_Support_Colors"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                }
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(5, new ItemComponent(new ItemBuilder(XMaterial.STRING).setName("§7" + Lang.get("Animation_Radius") + ": §e" + getRadius()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setRadius(getPart().getRadius() - 0.1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setRadius(getPart().getRadius() + 0.1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Animation_Radius") + ": §e" + getRadius());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Animation_Radius")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.STICK).setName("§7" + Lang.get("Animation_Height") + ": §e" + getHeight()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setHeight(getPart().getHeight() - 0.1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setHeight(getPart().getHeight() + 0.1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Animation_Height") + ": §e" + getHeight());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Animation_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7» §c" + Lang.get("Rotation") + "§7 «").getItem()).setLink(this.rotation), false);

        setItem(8, new ItemComponent(new ItemBuilder(XMaterial.SUGAR).setName("§7" + Lang.get("Animation_Speed") + ": §e" + getSpeed()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setSpeed(getPart().getSpeed() - 1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setSpeed(getPart().getSpeed() + 1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Animation_Speed") + ": §e" + getSpeed());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Animation_Speed")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        this.rotation.initialize();
    }

    private Color getColor() {
        return getPart() == null ? null : getPart().getColor();
    }

    private String getColorName() {
        return getPart() == null ? null : getPart().getParticle() != Particle.NOTE ? getPart().getColor().getName() : getPart().getColor().getNoteName();
    }

    private double getRadius() {
        return getPart() == null ? null : getPart().getRadius();
    }

    private double getSpeed() {
        return getPart() == null ? null : getPart().getSpeed();
    }

    private double getHeight() {
        return getPart() == null ? null : getPart().getHeight();
    }

    private String getAnimationName() {
        return getPart() == null ? null : getPart().getAnimation() == null ? null : getPart().getAnimation().getDisplayName();
    }

    private String getParticleName() {
        String s = getPart() == null ? null : getPart().getParticle() == null ? null : getPart().getParticle().name();
        if(s == null) return null;

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private ParticlePart getPart() {
        return menu.getClone().getParticleParts().size() <= slot ? null : menu.getClone().getParticleParts().get(slot);
    }

    public Menu getMenuGUI() {
        return menu;
    }
}
