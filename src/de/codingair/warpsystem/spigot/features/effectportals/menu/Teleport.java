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
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK)
                .setName("§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getPortal().getPermission() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Hologram-Height
                menu.getEditor().doAction(PortalEditor.Action.CHANGE_PERMISSION, () -> updateDisplayName(getItem(2), "§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getPortal().getPermission() + "§7'"));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change_Permission"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.MUSIC_DISC_CHIRP)
                .setName("§7" + Lang.get("Sound") + ": '§e" + menu.getEditor().getPortal().getTeleportSound().getSound().name() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Sound
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.NEXT_SOUND);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_SOUND);
                }

                updateDisplayName(getItem(4), "§7" + Lang.get("Sound") + ": '§e" + menu.getEditor().getPortal().getTeleportSound().getSound().name()+"§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.NEXT_PREVIOUS(Lang.get("Sound")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(5, new ItemComponent(new ItemBuilder(Material.NOTE_BLOCK)
                .setName("§7" + Lang.get("Volume") + ": §e" + menu.getEditor().getPortal().getTeleportSound().getVolume())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Volume
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.INCREASE_VOLUME);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.DECREASE_VOLUME);
                }

                updateDisplayName(getItem(5), "§7" + Lang.get("Volume") + ": §e" + menu.getEditor().getPortal().getTeleportSound().getVolume());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.PLUS_MINUS(Lang.get("Volume")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(Material.BLAZE_ROD)
                .setName("§7" + Lang.get("Pitch") + ": §e" + menu.getEditor().getPortal().getTeleportSound().getPitch())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Pitch
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.INCREASE_PITCH);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.DECREASE_PITCH);
                }

                updateDisplayName(getItem(6), "§7" + Lang.get("Pitch") + ":§e " + menu.getEditor().getPortal().getTeleportSound().getPitch());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.PLUS_MINUS(Lang.get("Pitch")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }
}
