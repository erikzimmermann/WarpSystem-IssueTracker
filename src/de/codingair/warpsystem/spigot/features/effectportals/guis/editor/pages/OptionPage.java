package de.codingair.warpsystem.spigot.features.effectportals.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncHotbarGUIButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.hotbars.SetPosition;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OptionPage extends PageItem {
    private EffectPortal clone;

    public OptionPage(Player p, EffectPortal clone) {
        super(p, EffectPortalEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.clone = clone;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        addButton(new SyncAnvilGUIButton(1, 2, ClickType.LEFT, ClickType.RIGHT) {
            @Override
            public ItemStack craftItem() {
                String first = clone.getStartName();
                String second = clone.hasDestinationPortal() ? clone.getStartName() : null;

                ItemBuilder builder = new ItemBuilder(XMaterial.NAME_TAG)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Name"))
                        .setLore("", "§71. " + Lang.get("Portal"), "    §3" + Lang.get("Current") + ": " + (first == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', first) + "§7'"),
                                "    §3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name"));

                if(clone.hasDestinationPortal()) {
                    builder.addLore("", "§72. " + Lang.get("Portal"), "    §3" + Lang.get("Current") + ": " + (second == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', second) + "§7'"),
                            (second == null ? "    §3" + Lang.get("Rightclick") + ": §a" + Lang.get("Set_Name") : "    §3" + Lang.get("Rightclick") + ": §a" + Lang.get("Change_Name")));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                    return;
                }

                e.setClose(true);

                if(getLastTrigger() == ClickType.LEFT) {
                    clone.setStartName(e.getInput());
                } else {
                    clone.setDestinationName(e.getInput());
                }
                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public ItemStack craftAnvilItem() {
                String name = this.getLastTrigger() == ClickType.LEFT ? clone.getStartName() : clone.getDestinationName();
                return new ItemBuilder(Material.PAPER).setName(name == null ? Lang.get("Name") + "..." : name.replace("§", "&")).getItem();
            }
        }.setOption(option));

        addButton(new PermissionButton(2, 2, clone).setOption(option));

        addButton(new SyncHotbarGUIButton(3, 2, new Node<>(ClickType.LEFT, new SetPosition(p, clone.getStart(), new Callback<Location>() {
            @Override
            public void accept(Location object) {
                clone.setStart(de.codingair.codingapi.tools.Location.getByLocation(object));
            }
        })), new Node<>(ClickType.SHIFT_LEFT, clone.hasDestinationPortal() ? null : new SetPosition(p, clone.getDestination().buildLocation(), new Callback<Location>() {
            @Override
            public void accept(Location object) {
                clone.getDestination().setAdapter(new LocationAdapter(object));
            }
        }))) {
            @Override
            public void onFinish(Player player) {
                OptionPage.this.getLast().open();
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Positioning"))
                        .setLore("", "§71. " + Lang.get("Portal"), "    " + Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Set"));

                if(clone.hasDestinationPortal()) {
                    builder.addLore("", "§72. " + Lang.get("Portal"), "    " + Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Leftclick") + ": §a" + Lang.get("Set"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {

            }
        }.setOption(option));
    }
}
