package de.codingair.warpsystem.spigot.features.shortcuts.guis.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class POptions extends PageItem {
    private Shortcut shortcut;

    public POptions(Player p, Shortcut shortcut) {
        super(p, Editor.TITLE_COLOR + Lang.get("Shortcuts"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.shortcut = shortcut;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

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

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                CommandAction action = shortcut.getAction(Action.COMMAND);
                List<String> commands = action == null ? null : action.getValue();
                List<String> commandInfo = new ArrayList<>();

                if(commands != null) {
                    for(String command : commands) {
                        commandInfo.add("§7- '§r" + command + "§7'");
                    }
                }

                List<String> lore = new ArrayList<>();
                if(commands != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return new ItemBuilder(XMaterial.REDSTONE)
                        .setName("§6§n" + Lang.get("Command"))
                        .setLore("§3" + Lang.get("Current") + ": " + (commands == null ? "§c" + Lang.get("Not_Set") : ""))
                        .addLore(commandInfo)
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                        .addLore(lore)
                        .getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Command"));
                    return;
                }

                if(!input.startsWith("/")) input = "/" + input;

                if(ShortcutManager.getInstance().hasCommandLoop(shortcut, input)) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Shortcut_Editor_Loop"));
                    return;
                }

                e.setClose(true);

                CommandAction action = shortcut.getAction(Action.COMMAND);
                if(action == null) {
                    action = new CommandAction(input);
                    shortcut.addAction(action);
                } else {
                    action.getValue().add(input);
                }

                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {

            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Command") + "...").getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    shortcut.removeAction(Action.COMMAND);
                    update();
                }
            }
        });

        addButton(new PermissionButton(3, 2, shortcut).setOption(option));
    }
}
