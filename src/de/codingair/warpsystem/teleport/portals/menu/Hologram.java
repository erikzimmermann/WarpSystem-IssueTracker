package de.codingair.warpsystem.teleport.portals.menu;

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

        setItem(4, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7"+Lang.get("Start_Name", new Example("ENG", "Start-Name"), new Example("GER", "Start-Name"))+": '§e" + menu.getEditor().getPortal().getStartName()+"§7'").getItem(), (gui, ic, player, clickType) -> {
            //Start-Name
            menu.getEditor().doAction(PortalEditor.Action.CHANGE_START_NAME, () -> updateDisplayName(getItem(4), "§7"+Lang.get("Start_Name")+": '§e" + menu.getEditor().getPortal().getStartName()+"§7'"));
        }));

        setItem(5, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7"+Lang.get("Goal_Name", new Example("ENG", "Goal-Name"), new Example("GER", "Ziel-Name"))+": '§e" + menu.getEditor().getPortal().getDestinationName()+"§7'").getItem(), (gui, ic, player, clickType) -> {
            //Goal-Name
            menu.getEditor().doAction(PortalEditor.Action.CHANGE_DESTINATION_NAME, () -> updateDisplayName(getItem(5), "§7"+Lang.get("Goal_Name")+": '§e" + menu.getEditor().getPortal().getDestinationName()+"§7'"));
        }));
    }
}
