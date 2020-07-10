package de.codingair.warpsystem.spigot.base.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeleportSoundPage extends PageItem {
    private final SoundData soundData;
    private final Sound[] sounds = Sound.values();

    public TeleportSoundPage(Player player, String title, SoundData soundData) {
        super(player, title, new ItemBuilder(XMaterial.NOTE_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Sound")).getItem(), false);
        this.soundData = soundData;
        initialize(player);
    }

    public static SoundData createStandard() {
        return new SoundData(Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
    }

    public static boolean isStandardSound(SoundData data) {
        return createStandard().equals(data);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new StandardButtonOption();

        addButton(new SyncAnvilGUIButton(1, 2, ClickType.SHIFT_LEFT) {
            private final List<Sound> searched = new ArrayList<>();
            private String searchingFor = null;
            private int id = 0;

            @Override
            public void onClick(AnvilClickEvent e) {
                String input = e.getInput();

                if(input == null) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Something"));
                    return;
                }

                input = input.toUpperCase().replace(" ", "_");
                Integer id = null;

                try {
                    id = Integer.parseInt(input);
                } catch(NumberFormatException ignored) {
                }

                if(id != null) {
                    if(id >= 0 && id < sounds.length) soundData.setSound(sounds[id]);
                    else {
                        p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Amount_between").replace("%X%", "0").replace("%Y%", (sounds.length - 1) + ""));
                        return;
                    }
                } else {
                    searchingFor = input;
                    searched.clear();
                    id = 0;
                    for(Sound sound : sounds) {
                        if(sound.name().contains(searchingFor)) searched.add(sound);
                    }

                    if(!searched.isEmpty()) soundData.setSound(searched.get(0));
                }

                e.setClose(true);
            }

            @Override
            public boolean canTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
                if(trigger == ClickType.SHIFT_LEFT) {
                    option.getClickSound2().play(p);

                    if(searchingFor != null) {
                        searchingFor = null;
                        searched.clear();
                        id = 0;
                        update();
                        return false;
                    } else soundData.stop(p);
                }

                return true;
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
                soundData.play(p);
                update();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.isLeftClick()) {
                    if(e.isShiftClick()) {
                        searchingFor = null;
                        id = 0;
                        searched.clear();
                    } else {
                        soundData.stop(p);

                        if(searchingFor == null) soundData.setSound(previous());
                        else {
                            id--;
                            if(id == -1) id = searched.size() - 1;
                            soundData.setSound(searched.get(id));
                        }

                        soundData.play(p);
                    }
                } else if(e.isRightClick()) {
                    if(e.isShiftClick()) {
                        soundData.stop(p);
                        soundData.play(p);
                        return;
                    } else {
                        soundData.stop(p);

                        if(searchingFor == null) soundData.setSound(next());
                        else {
                            id++;
                            if(id == searched.size()) id = 0;
                            soundData.setSound(searched.get(id));
                        }

                        soundData.play(p);
                    }
                }

                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                if(searchingFor == null || !searched.isEmpty()) return click == ClickType.LEFT || click == ClickType.SHIFT_LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT;
                else return click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
            }

            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.MUSIC_DISC_CAT)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Sound") + "§8 (§7" + (sounds.length - 1) + "§8)")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7'" + (searchingFor == null ? soundData.getSound().name() : ChatColor.highlight(soundData.getSound().name(), searchingFor, "§e", "§7")) + "§7' §8(§7id: " + soundData.getSound().ordinal() + "§8)")
                        .addLore(soundData.getSound().isSupported() ? null : "§8» §7" + Lang.get("Not_Available_in_Version").replace("%VERSION%", Version.get().getShortVersionName()))
                        .addLore(soundData.getSound().isSupported() ? null : "")
                        .addLore(searchingFor == null ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Search_Short") + ": §7'" + searchingFor + "' §8(§7" + searched.size() + "§8)")
                        .addLore("", searchingFor == null || !searched.isEmpty() ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7«" : null)
                        .addLore(searchingFor == null || !searched.isEmpty() ? Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7»" : null)
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Leftclick") + ": " + (searchingFor == null ? "§e" + Lang.get("Search_Short") : "§c" + Lang.get("Reset_Search")))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §7" + Lang.get("Play"))
                        .setHideStandardLore(true)
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(soundData.getSound().name()).getItem();
            }
        }.setOption(option).setClickSound2(null));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NOTE_BLOCK)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Volume"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §f" + soundData.getVolume())
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
        }.setOption(option).setClickSound2(null));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.BLAZE_ROD)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Pitch"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §f" + soundData.getPitch())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7-")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7+")
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                soundData.stop(player);

                if(e.isLeftClick()) soundData.setPitch(Math.max(round(soundData.getPitch() - 0.1F), 0F));
                else soundData.setPitch(Math.min(round(soundData.getPitch() + 0.1F), 2F));

                soundData.play(player);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setOption(option).setClickSound2(null));
    }

    private float round(float d) {
        return ((float) Math.round(d * 10)) / 10;
    }

    public Sound next() {
        int id = soundData.getSound().ordinal() + 1;
        if(id == sounds.length) id = 0;
        return sounds[id];
    }

    public Sound previous() {
        int id = soundData.getSound().ordinal() - 1;
        if(id < 0) id = sounds.length - 1;
        return sounds[id];
    }
}
