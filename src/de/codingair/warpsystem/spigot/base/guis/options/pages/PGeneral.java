package de.codingair.warpsystem.spigot.base.guis.options.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.utils.options.GeneralOptions;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.editor.Menu;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PGeneral extends PageItem {
    private GeneralOptions options;

    public PGeneral(Player p, GeneralOptions options) {
        super(p, Editor.TITLE_COLOR + "WarpSystem§r §7- §6" + Lang.get("Config"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("General")).getItem(), false);

        this.options = options;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        List<String> languages = options.getLanguages();
        Value<Integer> id = new Value<>(0);
        for(String language : languages) {
            if(!language.equals(options.getLang())) id.setValue(id.getValue() + 1);
            else break;
        }

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.BOOKSHELF)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Language"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7'§e" + options.getLang() + "§7'")
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7«")
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7»");

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    id.setValue(id.getValue() - 1);
                    if(id.getValue() < 0) id.setValue(languages.size() - 1);

                    options.setLang(languages.get(id.getValue()));
                    update();
                } else if(e.isRightClick()) {
                    id.setValue(id.getValue() + 1);
                    if(id.getValue() == languages.size()) id.setValue(0);

                    options.setLang(languages.get(id.getValue()));
                    update();
                }
            }
        }.setOption(option));

        //Teleport delay
        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Delay"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7'§e" + options.getTeleportDelay() + "§7'")
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Reduce"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Enlarge"));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    options.setTeleportDelay(options.getTeleportDelay() - 1);
                    if(options.getTeleportDelay() < 0) options.setTeleportDelay(0);

                    options.setLang(languages.get(id.getValue()));
                    update();
                } else if(e.isRightClick()) {
                    options.setTeleportDelay(options.getTeleportDelay() + 1);
                    if(options.getTeleportDelay() > 60) options.setTeleportDelay(60);

                    options.setLang(languages.get(id.getValue()));
                    update();
                }
            }
        }.setOption(option));

        //Allow move
        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.LEATHER_BOOTS)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Walking_During_Teleports"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7'" + (options.isAllowMove() ? "§a" + Lang.get("Enabled") : "§c" + Lang.get("Disabled")) + "§7'")
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"))
                        .setHideStandardLore(true)
                        ;

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    options.setAllowMove(!options.isAllowMove());

                    options.setLang(languages.get(id.getValue()));
                    update();
                }
            }
        }.setOption(option));

        //PreChunkLoading (coming soon...)
        addButton(new SyncButton(4, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.END_PORTAL_FRAME)
                        .setName(Editor.ITEM_TITLE_COLOR + "Pre chunk loading")
                        .setLore("§7coming soon...");
                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {

            }
        });
    }
}
