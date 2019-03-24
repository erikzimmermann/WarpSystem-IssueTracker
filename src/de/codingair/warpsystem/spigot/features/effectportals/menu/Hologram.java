package de.codingair.warpsystem.spigot.features.effectportals.menu;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.effectportals.PortalEditor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Hologram extends HotbarGUI {
    private Menu menu;

    public Hologram(Player player, Menu menu) {
        super(player, WarpSystem.getInstance());
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    public void init() {

        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK).setName("§7" + Lang.get("Hologram_Height") + ": §e" + menu.getEditor().getPortal().getHologramHeight()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Hologram-Height
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.INCREASE_HOLOGRAM_HEIGHT);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.DECREASE_HOLOGRAM_HEIGHT);
                }

                updateDisplayName(getItem(2), "§7" + Lang.get("Hologram_Height") + ": §e" + menu.getEditor().getPortal().getHologramHeight());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.PLUS_MINUS(Lang.get("Hologram_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7" + Lang.get("Start_Name") + ": '§r" + menu.getEditor().getPortal().getStartName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Start-Name
                menu.getEditor().doAction(PortalEditor.Action.CHANGE_START_NAME, () -> updateDisplayName(getItem(4), "§7" + Lang.get("Start_Name") + ": '§r" + menu.getEditor().getPortal().getStartName() + "§7'"));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Lang.get("Portal_Editor_Change_Name"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        ItemBuilder builder = new ItemBuilder(menu.getEditor().getPortal().isStartHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
        builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Start") +
                (menu.getEditor().getPortal().isStartHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                        ChatColor.RED + Lang.get("Disabled")));

        setItem(5, new ItemComponent(builder.getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                menu.getEditor().getPortal().setStartHoloStatus(!menu.getEditor().getPortal().isStartHoloStatus());
                menu.getEditor().getPortal().updateHolograms();

                ItemBuilder builder = new ItemBuilder(menu.getEditor().getPortal().isStartHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
                builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Start") +
                        (menu.getEditor().getPortal().isStartHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                                ChatColor.RED + Lang.get("Disabled")));

                setItem(5, new ItemComponent(builder.getItem(), this), false);
                updateSingle(5);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(7, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7" + Lang.get("Goal_Name") + ": '§r" + menu.getEditor().getPortal().getDestinationName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Goal-Name
                menu.getEditor().doAction(PortalEditor.Action.CHANGE_DESTINATION_NAME, () -> updateDisplayName(getItem(7), "§7" + Lang.get("Goal_Name") + ": '§r" + menu.getEditor().getPortal().getDestinationName() + "§7'"));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Lang.get("Portal_Editor_Change_Name"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));


        builder = new ItemBuilder(menu.getEditor().getPortal().isDestinationHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
        builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination") +
                (menu.getEditor().getPortal().isDestinationHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                        ChatColor.RED + Lang.get("Disabled")));

        setItem(8, new ItemComponent(builder.getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                menu.getEditor().getPortal().setDestinationHoloStatus(!menu.getEditor().getPortal().isDestinationHoloStatus());
                menu.getEditor().getPortal().updateHolograms();

                ItemBuilder builder = new ItemBuilder(menu.getEditor().getPortal().isDestinationHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
                builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination") +
                        (menu.getEditor().getPortal().isDestinationHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                                ChatColor.RED + Lang.get("Disabled")));

                setItem(8, new ItemComponent(builder.getItem(), this), false);
                updateSingle(8);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }
}
