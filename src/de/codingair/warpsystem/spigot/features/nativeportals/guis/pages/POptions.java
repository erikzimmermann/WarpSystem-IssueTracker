package de.codingair.warpsystem.spigot.features.nativeportals.guis.pages;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.DelayButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class POptions extends PageItem {
    private NativePortal nativePortal;

    public POptions(Player p, NativePortal nativePortal) {
        super(p, NPEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.nativePortal = nativePortal;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new NameButton(1, 2, false, new Value<>(nativePortal.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                return null;
            }

            @Override
            public String onChange(String old, String name) {
                nativePortal.setDisplayName(name);
                return name;
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.IRON_PICKAXE)
                        .setHideStandardLore(true)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortals_Set_Blocks"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Portal_Blocks") + ": " + (nativePortal.getBlocks().isEmpty() ? "§c" : "§a") + nativePortal.getBlocks().size())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("NativePortals_Set_Blocks"));

                if(nativePortal.getBlocks().isEmpty()) {
                    itemBuilder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    itemBuilder.setHideEnchantments(true);
                }

                return itemBuilder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                getLast().setClosingForGUI(true);
                player.closeInventory();

                PortalEditor editor = new PortalEditor(p, nativePortal);
                editor.init();

                MessageAPI.sendActionBar(p, Lang.get("Drop_To_Leave"), WarpSystem.getInstance(), Integer.MAX_VALUE);

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onDrop(PlayerDropItemEvent e) {
                        if(!e.getPlayer().getName().equals(player.getName())) return;

                        if(e.getItemDrop().getItemStack().equals(PortalEditor.PORTAL_ITEM.getItem())) {
                            e.setCancelled(true);

                            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                                MessageAPI.stopSendingActionBar(p);
                                HandlerList.unregisterAll(this);

                                editor.end();

                                updatePage();
                                getLast().updateControllButtons();
                                getLast().open();
                            });
                        }
                    }
                }, WarpSystem.getInstance());
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

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
        }.setOption(option));

        addButton(new DelayButton(4, 2, nativePortal).setOption(option));
        addButton(new CommandButton(5, 2, nativePortal).setOption(option));
    }
}
