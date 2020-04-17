package de.codingair.warpsystem.spigot.base.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportSoundPage extends PageItem {
    private SoundData soundData;

    public TeleportSoundPage(Player player, String title, SoundData soundData) {
        super(player, title, new ItemBuilder(XMaterial.NOTE_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Sound")).getItem(), false);
        this.soundData = soundData;
        initialize(player);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new StandardButtonOption();

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.MUSIC_DISC_WAIT)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Sound"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7'§r" + soundData.getSound().name() + "§7'")
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7« (" + Lang.get("Shift") + ")")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7» (" + Lang.get("Shift") + ")")
                        .setHideStandardLore(true)
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                soundData.stop(player);

                if(e.isLeftClick()) {
                    if(e.isShiftClick()) soundData.setSound(shiftPrevious(soundData.getSound()));
                    else soundData.setSound(previous(soundData.getSound()));
                } else {
                    if(e.isShiftClick()) soundData.setSound(shiftNext(soundData.getSound()));
                    else soundData.setSound(next(soundData.getSound()));
                }

                soundData.play(player);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.SHIFT_LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NOTE_BLOCK)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Volume"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §r" + soundData.getVolume())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7-")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7+")
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                soundData.stop(player);

                if(e.isLeftClick()) soundData.setVolume(Math.max(round(soundData.getVolume() - 0.1F), 0F));
                else soundData.setVolume(Math.min(round(soundData.getVolume() + 0.1F), 1F));

                soundData.play(player);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setOption(option));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.BLAZE_ROD)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Pitch"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §r" + soundData.getPitch())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7-")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7+")
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                soundData.stop(player);

                if(e.isLeftClick()) soundData.setPitch(Math.max(round(soundData.getPitch() - 0.1F), 0F));
                else soundData.setPitch(Math.min(round(soundData.getPitch() + 0.1F), 1F));

                soundData.play(player);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setOption(option));
    }

    private float round(float d) {
        return ((float) Math.round(d * 10)) / 10;
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

    public static SoundData createStandard() {
        return new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F);
    }

    public static boolean isStandardSound(SoundData data) {
        return createStandard().equals(data);
    }
}
