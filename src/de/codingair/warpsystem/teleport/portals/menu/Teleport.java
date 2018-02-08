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

public class Teleport extends HotbarGUI {
    private Menu menu;

    public Teleport(Player player, Menu menu) {
        super(player, WarpSystem.getInstance());
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    public void init() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back")).getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK)
                .setName("§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getPortal().getPermission() + "§7'")
                .getItem(), (gui, ic, player, clickType) -> {
            //Hologram-Height
            menu.getEditor().doAction(PortalEditor.Action.CHANGE_PERMISSION, () -> updateDisplayName(getItem(2), "§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getPortal().getPermission() + "§7'"));
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.RECORD_5)
                .setName("§7" + Lang.get("Sound", new Example("ENG", "Sound"), new Example("GER", "Ton")) + ": '§e" + menu.getEditor().getPortal().getTeleportSound().getSound().name()+"§7'")
                .getItem(), (gui, ic, player, clickType) -> {
            //Sound
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.NEXT_SOUND);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_SOUND);
            }

            updateDisplayName(getItem(4), "§7" + Lang.get("Sound") + ": '§e" + menu.getEditor().getPortal().getTeleportSound().getSound().name()+"§7'");
        }));

        setItem(5, new ItemComponent(new ItemBuilder(Material.NOTE_BLOCK)
                .setName("§7" + Lang.get("Volume", new Example("ENG", "Volume"), new Example("GER", "Lautstärke")) + ": §e" + menu.getEditor().getPortal().getTeleportSound().getVolume())
                .getItem(), (gui, ic, player, clickType) -> {
            //Volume
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.INCREASE_VOLUME);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.DECREASE_VOLUME);
            }

            updateDisplayName(getItem(5), "§7" + Lang.get("Volume") + ": §e" + menu.getEditor().getPortal().getTeleportSound().getVolume());
        }));

        setItem(6, new ItemComponent(new ItemBuilder(Material.BLAZE_ROD)
                .setName("§7" + Lang.get("Pitch", new Example("ENG", "Pitch"), new Example("GER", "Tonhöhe")) + ": §e" + menu.getEditor().getPortal().getTeleportSound().getPitch())
                .getItem(), (gui, ic, player, clickType) -> {
            //Pitch
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.INCREASE_PITCH);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.DECREASE_PITCH);
            }

            updateDisplayName(getItem(6), "§7" + Lang.get("Pitch") + ":§e " + menu.getEditor().getPortal().getTeleportSound().getPitch());
        }));
    }
}
