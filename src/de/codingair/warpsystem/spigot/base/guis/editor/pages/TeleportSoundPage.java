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
    private Sound[] sounds = Sound.values();

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
                return new ItemBuilder(XMaterial.MUSIC_DISC_CAT)
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
                    if(e.isShiftClick()) soundData.setSound(shiftPrevious());
                    else soundData.setSound(previous());
                } else {
                    if(e.isShiftClick()) soundData.setSound(shiftNext());
                    else soundData.setSound(next());
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

    public Sound next() {
        int id = soundData.getSound().ordinal() + 1;
        if(id == sounds.length) id = 0;
        return sounds[id];
    }

    public Sound shiftNext() {
        Sound sound = soundData.getSound();
        for(int i = sound.ordinal(); true; i++) {
            if(i == sounds.length) i = 0;
            if(sound.name().charAt(0) != sounds[i].name().charAt(0)) {
                return sounds[i];
            }
        }
    }

    public Sound previous() {
        int id = soundData.getSound().ordinal() - 1;
        if(id < 0) id = sounds.length;
        return sounds[id];
    }

    public Sound shiftPrevious() {
        Sound sound = soundData.getSound();
        for(int i = sound.ordinal(); true; i--) {
            if(sound.name().charAt(0) != sounds[i].name().charAt(0)) {
                return sounds[i];
            }
            if(i == 0) i = sounds.length;
        }
    }

    public static SoundData createStandard() {
        return new SoundData(Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
    }

    public static boolean isStandardSound(SoundData data) {
        return createStandard().equals(data);
    }
}
