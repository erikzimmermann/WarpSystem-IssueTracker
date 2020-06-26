package de.codingair.warpsystem.spigot.features.animations.guis.editor;

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

public class ParticleRotation extends HotbarGUI {
    private final Menu menu;
    private final int slot;

    public ParticleRotation(Player player, int slot, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.5F, 1F));

        this.menu = menu;
        this.slot = slot;
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu.getParticles().getAnimations()[slot]), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7" + Lang.get("Rotation") + " (X): '§e" + getXRotation() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setxRotation(getPart().getxRotation() - 5);
                } else if(clickType == ClickType.SHIFT_LEFT_CLICK) {
                    getPart().setxRotation(getPart().getxRotation() - 45);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setxRotation(getPart().getxRotation() + 5);
                } else if(clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    getPart().setxRotation(getPart().getxRotation() + 45);
                }

                if(getPart().getxRotation() >= 360) getPart().setxRotation(getPart().getxRotation() - 360);
                else if(getPart().getxRotation() < 0) getPart().setxRotation(getPart().getxRotation() + 360);

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Rotation") + " (X): '§e" + getXRotation() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS_SHIFT(Lang.get("Rotation") + " (X)"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }));

        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7" + Lang.get("Rotation") + " (Y): '§e" + getYRotation() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setyRotation(getPart().getyRotation() - 5);
                } else if(clickType == ClickType.SHIFT_LEFT_CLICK) {
                    getPart().setyRotation(getPart().getyRotation() - 45);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setyRotation(getPart().getyRotation() + 5);
                } else if(clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    getPart().setyRotation(getPart().getyRotation() + 45);
                }

                if(getPart().getyRotation() >= 360) getPart().setyRotation(getPart().getyRotation() - 360);
                else if(getPart().getyRotation() < 0) getPart().setyRotation(getPart().getyRotation() + 360);

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Rotation") + " (Y): '§e" + getYRotation() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS_SHIFT(Lang.get("Rotation") + " (Y)"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7" + Lang.get("Rotation") + " (Z): '§e" + getZRotation() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setzRotation(getPart().getzRotation() - 5);
                } else if(clickType == ClickType.SHIFT_LEFT_CLICK) {
                    getPart().setzRotation(getPart().getzRotation() - 45);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPart().setzRotation(getPart().getzRotation() + 5);
                } else if(clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    getPart().setzRotation(getPart().getzRotation() + 45);
                }

                if(getPart().getzRotation() >= 360) getPart().setzRotation(getPart().getzRotation() - 360);
                else if(getPart().getzRotation() < 0) getPart().setzRotation(getPart().getzRotation() + 360);

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Rotation") + " (Z): '§e" + getZRotation() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS_SHIFT(Lang.get("Rotation") + " (Z)"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.BARRIER).setName("§7" + Lang.get("Leftclick") + ": §c" + Lang.get("Reset")).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getPart().setxRotation(0);
                    getPart().setyRotation(0);
                    getPart().setzRotation(0);

                    menu.getAnimPlayer().update();
                    updateDisplayName(getItem(2), "§7" + Lang.get("Rotation") + " (X): '§e" + getXRotation() + "§7'");
                    updateDisplayName(getItem(3), "§7" + Lang.get("Rotation") + " (Y): '§e" + getYRotation() + "§7'");
                    updateDisplayName(getItem(4), "§7" + Lang.get("Rotation") + " (Z): '§e" + getZRotation() + "§7'");
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }));
    }

    private int getXRotation() {
        return getPart() == null ? null : getPart().getxRotation();
    }

    private int getYRotation() {
        return getPart() == null ? null : getPart().getyRotation();
    }

    private int getZRotation() {
        return getPart() == null ? null : getPart().getzRotation();
    }

    private ParticlePart getPart() {
        return menu.getClone().getParticleParts().size() <= slot ? null : menu.getClone().getParticleParts().get(slot);
    }

}
