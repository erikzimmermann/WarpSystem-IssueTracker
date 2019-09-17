package de.codingair.warpsystem.spigot.features.nativeportals.guis.pages;

import de.codingair.codingapi.player.MessageAPI;
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
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.Portal;
import de.codingair.warpsystem.spigot.features.nativeportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PAppearance extends PageItem {
    private Portal portal;

    public PAppearance(Player p, Portal portal) {
        super(p, NPEditor.getMainTitle(), new ItemBuilder(XMaterial.ITEM_FRAME).setName("§6§n" + Lang.get("Appearance")).getItem(), false);

        this.portal = portal;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        addButton(new NameButton(1, 2, false, new Value<>(portal.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                return null;
            }

            @Override
            public String onChange(String old, String name) {
                portal.setDisplayName(name);
                return name;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.IRON_PICKAXE)
                        .setHideStandardLore(true)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortals_Set_Blocks"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Portal_Blocks") + ": " + (portal.getBlocks().isEmpty() ? "§c" : "§a") + portal.getBlocks().size())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("NativePortals_Set_Blocks"));

                if(portal.getBlocks().isEmpty()) {
                    itemBuilder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    itemBuilder.setHideEnchantments(true);
                }

                return itemBuilder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                getLast().setClosingForGUI(true);
                player.closeInventory();

                PortalEditor editor = new PortalEditor(p, portal);
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
        }.setOption(option));

        addButton(new PermissionButton(3, 2, portal).setOption(option));
    }
}
