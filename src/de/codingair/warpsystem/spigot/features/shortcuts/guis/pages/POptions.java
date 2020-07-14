package de.codingair.warpsystem.spigot.features.shortcuts.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CooldownButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CostsButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class POptions extends PageItem {
    private final Shortcut shortcut;

    public POptions(Player p, Shortcut shortcut) {
        super(p, Editor.TITLE_COLOR + Lang.get("Shortcuts"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.shortcut = shortcut;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));

        addButton(new NameButton(1, 2, false, new Value<>(shortcut.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                return null;
            }

            @Override
            public String onChange(String old, String name) {
                name = name.replace(" ", "_").toLowerCase();
                shortcut.setDisplayName(name);
                return name;
            }
        }.setOption(option));

        addButton(new CommandButton(2, 2, shortcut).setOption(option));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_EYE)
                        .setName("§6§n" + Lang.get("Permission") + Lang.PREMIUM_LORE)
                        .addLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Lang.PREMIUM_CHAT(player);
            }
        }.setOption(option).setOnlyLeftClick(true));

        addButton(new CooldownButton(4, 2, shortcut).setOption(option));

        addButton(new SyncButton(5, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.GOLD_NUGGET)
                        .setName("§6§n" + Lang.get("Costs") + Lang.PREMIUM_LORE)
                        .addLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Lang.PREMIUM_CHAT(player);
            }
        }.setOption(option).setOnlyLeftClick(true));
    }
}
