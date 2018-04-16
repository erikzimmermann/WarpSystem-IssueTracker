package de.codingair.warpsystem.teleport.portals.menu;

import de.codingair.codingapi.player.gui.hotbar.ClickEvent;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.ItemBuilder;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.teleport.portals.PortalEditor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
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

        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back")).getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK).setName("§7" + Lang.get("Hologram_Height", new Example("ENG", "Hologram-Height"), new Example("GER", "Hologram-Höhe"))+": §e" + menu.getEditor().getPortal().getHologramHeight()).getItem(), (gui, ic, player, clickType) -> {
            //Hologram-Height
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.INCREASE_HOLOGRAM_HEIGHT);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.DECREASE_HOLOGRAM_HEIGHT);
            }

            updateDisplayName(getItem(2), "§7" + Lang.get("Hologram_Height")+": " + menu.getEditor().getPortal().getHologramHeight());
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7"+Lang.get("Start_Name", new Example("ENG", "Start-Name"), new Example("GER", "Start-Name"))+": '" + menu.getEditor().getPortal().getStartName()+"§7'").getItem(), (gui, ic, player, clickType) -> {
            //Start-Name
            menu.getEditor().doAction(PortalEditor.Action.CHANGE_START_NAME, () -> updateDisplayName(getItem(4), "§7"+Lang.get("Start_Name")+": '" + menu.getEditor().getPortal().getStartName()+"§7'"));
        }));

        ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY);
        builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Start", new Example("ENG", "Status of Start-Holo: "), new Example("GER", "Status vom Start-Holo: ")) +
                (menu.getEditor().getPortal().isStartHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled", new Example("ENG", "Enabled"), new Example("GER", "Aktiviert")) :
                        ChatColor.RED + Lang.get("Disabled", new Example("ENG", "Disabled"), new Example("GER", "Deaktiviert"))));

        if(menu.getEditor().getPortal().isStartHoloStatus()) builder.setColor(DyeColor.LIME);
        else builder.setColor(DyeColor.RED);

        setItem(5, new ItemComponent(builder.getItem(), new ClickEvent() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                menu.getEditor().getPortal().setStartHoloStatus(!menu.getEditor().getPortal().isStartHoloStatus());
                menu.getEditor().getPortal().updateHolograms();

                ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY);
                builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Start", new Example("ENG", "Status of Start-Holo: "), new Example("GER", "Status vom Start-Holo: ")) +
                        (menu.getEditor().getPortal().isStartHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled", new Example("ENG", "Enabled"), new Example("GER", "Aktiviert")) :
                                ChatColor.RED + Lang.get("Disabled", new Example("ENG", "Disabled"), new Example("GER", "Deaktiviert"))));

                if(menu.getEditor().getPortal().isStartHoloStatus()) builder.setColor(DyeColor.LIME);
                else builder.setColor(DyeColor.RED);

                setItem(5, new ItemComponent(builder.getItem(), this), false);
                updateSingle(5);
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).getItem()));

        setItem(7, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7"+Lang.get("Goal_Name", new Example("ENG", "Goal-Name"), new Example("GER", "Ziel-Name"))+": '" + menu.getEditor().getPortal().getDestinationName()+"§7'").getItem(), (gui, ic, player, clickType) -> {
            //Goal-Name
            menu.getEditor().doAction(PortalEditor.Action.CHANGE_DESTINATION_NAME, () -> updateDisplayName(getItem(5), "§7"+Lang.get("Goal_Name")+": '" + menu.getEditor().getPortal().getDestinationName()+"§7'"));
        }));


        builder = new ItemBuilder(Material.STAINED_CLAY);
        builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination", new Example("ENG", "Status of Destination-Holo: "), new Example("GER", "Status vom Destination-Holo: ")) +
                (menu.getEditor().getPortal().isDestinationHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled", new Example("ENG", "Enabled"), new Example("GER", "Aktiviert")) :
                        ChatColor.RED + Lang.get("Disabled", new Example("ENG", "Disabled"), new Example("GER", "Deaktiviert"))));

        if(menu.getEditor().getPortal().isDestinationHoloStatus()) builder.setColor(DyeColor.LIME);
        else builder.setColor(DyeColor.RED);

        setItem(8, new ItemComponent(builder.getItem(), new ClickEvent() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                menu.getEditor().getPortal().setDestinationHoloStatus(!menu.getEditor().getPortal().isDestinationHoloStatus());
                menu.getEditor().getPortal().updateHolograms();

                ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY);
                builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination", new Example("ENG", "Status of Destination-Holo: "), new Example("GER", "Status vom Destination-Holo: ")) +
                        (menu.getEditor().getPortal().isDestinationHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled", new Example("ENG", "Enabled"), new Example("GER", "Aktiviert")) :
                                ChatColor.RED + Lang.get("Disabled", new Example("ENG", "Disabled"), new Example("GER", "Deaktiviert"))));

                if(menu.getEditor().getPortal().isDestinationHoloStatus()) builder.setColor(DyeColor.LIME);
                else builder.setColor(DyeColor.RED);

                setItem(8, new ItemComponent(builder.getItem(), this), false);
                updateSingle(8);
            }
        }));
    }
}
