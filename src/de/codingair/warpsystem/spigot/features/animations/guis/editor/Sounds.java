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
import org.bukkit.entity.Player;

public class Sounds extends HotbarGUI {
    private Menu menu;

    public Sounds(Player player, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.menu = menu;

        initialize();
    }

    public static Sound next(Sound sound) {
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) return i + 1 == Sound.values().length ? Sound.values()[0] : Sound.values()[i + 1];
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    public static Sound shiftNext(Sound sound) {
        int id = -1;
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) {
                id = i;
            } else if(id >= 0 && sound.name().charAt(0) != Sound.values()[i].name().charAt(0)) {
                return Sound.values()[i];
            }
        }

        return Sound.values()[0];
    }

    public static Sound previous(Sound sound) {
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) {
                return i - 1 < 0 ? Sound.values()[Sound.values().length - 1] : Sound.values()[i - 1];
            }
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    public static Sound shiftPrevious(Sound sound) {
        int id = -1;

        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].name().charAt(0) == sound.name().charAt(0)) {
                return id == -1 ? Sound.values()[Sound.values().length - 1] : Sound.values()[id];
            } else {
                id = i;
            }
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(menu), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.MUSIC_DISC_WAIT)
                .setName("§7" + Lang.get("Tick_Sound") + ": '§e" + getTickSound().getSound().name() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Sound
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTickSound().setSound(previous(getTickSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    getTickSound().setSound(shiftPrevious(getTickSound().getSound()));
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTickSound().setSound(next(getTickSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    getTickSound().setSound(shiftNext(getTickSound().getSound()));
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Tick_Sound") + ": '§e" + getTickSound().getSound().name() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT_SHIFT(Lang.get("Tick_Sound")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.NOTE_BLOCK)
                .setName("§7" + Lang.get("Volume") + ": §e" + getTickSound().getVolume())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Volume
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTickSound().setVolume(round(getTickSound().getVolume() - 0.1F));
                    if(getTickSound().getVolume() < 0) getTickSound().setVolume(0);
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTickSound().setVolume(round(getTickSound().getVolume() + 0.1F));
                    if(getTickSound().getVolume() > 1) getTickSound().setVolume(1);
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Volume") + ": §e" + getTickSound().getVolume());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Volume")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD)
                .setName("§7" + Lang.get("Pitch") + ": §e" + getTickSound().getPitch())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Pitch
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTickSound().setPitch(round(getTickSound().getPitch() - 0.1F));
                    if(getTickSound().getPitch() < 0) getTickSound().setPitch(0);
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTickSound().setPitch(round(getTickSound().getPitch() + 0.1F));
                    if(getTickSound().getPitch() > 1) getTickSound().setPitch(1);
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Pitch") + ": §e" + getTickSound().getPitch());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Pitch")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.MUSIC_DISC_CHIRP)
                .setName("§7" + Lang.get("Teleport_Sound") + ": '§e" + getTeleportSound().getSound().name() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Sound
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTeleportSound().setSound(previous(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    getTeleportSound().setSound(shiftPrevious(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTeleportSound().setSound(next(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    getTeleportSound().setSound(shiftNext(getTeleportSound().getSound()));
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Teleport_Sound") + ": '§e" + getTeleportSound().getSound().name() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT_SHIFT(Lang.get("Teleport_Sound")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.NOTE_BLOCK)
                .setName("§7" + Lang.get("Volume") + ": §e" + getTeleportSound().getVolume())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Volume
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTeleportSound().setVolume(round(getTeleportSound().getVolume() - 0.1F));
                    if(getTeleportSound().getVolume() < 0) getTeleportSound().setVolume(0);
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTeleportSound().setVolume(round(getTeleportSound().getVolume() + 0.1F));
                    if(getTeleportSound().getVolume() > 1) getTeleportSound().setVolume(1);
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Volume") + ": §e" + getTeleportSound().getVolume());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Volume")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(8, new ItemComponent(new ItemBuilder(XMaterial.BLAZE_ROD)
                .setName("§7" + Lang.get("Pitch") + ": §e" + getTeleportSound().getPitch())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Pitch
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTeleportSound().setPitch(round(getTeleportSound().getPitch() - 0.1F));
                    if(getTeleportSound().getPitch() < 0) getTeleportSound().setPitch(0);
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTeleportSound().setPitch(round(getTeleportSound().getPitch() + 0.1F));
                    if(getTeleportSound().getPitch() > 1) getTeleportSound().setPitch(1);
                }

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Pitch") + ": §e" + getTeleportSound().getPitch());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Pitch")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }

    private float round(float d) {
        return ((float) Math.round(d * 10)) / 10;
    }

    private SoundData getTickSound() {
        return menu.getClone().getTickSound();
    }

    private SoundData getTeleportSound() {
        return menu.getClone().getTeleportSound();
    }

    public Menu getMenuGUI() {
        return menu;
    }
}