package de.codingair.warpsystem.teleport.portals.menu;

import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
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

public class Menu extends HotbarGUI {
    private PortalEditor editor;
    private Animation animation = new Animation(getPlayer(), this);
    private Hologram hologram = new Hologram(getPlayer(), this);
    private Teleport teleport = new Teleport(getPlayer(), this);

    public Menu(Player player, PortalEditor editor) {
        super(player, WarpSystem.getInstance());
        this.editor = editor;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.animation.init();
        this.hologram.init();
        this.teleport.init();
        init();
    }

    private void init() {
        setItem(0, new ItemComponent(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).getItem()));
        setItem(1, new ItemComponent(new ItemBuilder(Material.REDSTONE).setName("§7» §e" + Lang.get("Animation", new Example("ENG", "Animation"), new Example("GER", "Animation")) + "§7 «").getItem()).setLink(this.animation));
        setItem(2, new ItemComponent(new ItemBuilder(Material.SIGN).setName("§7» §e" + Lang.get("Hologram", new Example("ENG", "Hologram"), new Example("GER", "Hologram")) + "§7 «").getItem()).setLink(this.hologram));
        setItem(3, new ItemComponent(new ItemBuilder(Material.ENDER_PEARL).setName("§7» §e" + Lang.get("Teleport", new Example("ENG", "Teleport"), new Example("GER", "Teleport")) + "§7 «").getItem()).setLink(this.teleport));

        setItem(5, new ItemComponent(new ItemBuilder(Material.STAINED_CLAY).setColor(DyeColor.LIME).setName("§7» §a" + Lang.get("Save", new Example("ENG", "Save"), new Example("GER", "Speichern")) + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                editor.finish();

                if(editor.getBackupPortal() == null) {
                    //CREATION
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Created", new Example("ENG", "&7The portal has been created."), new Example("GER", "&7Das Portal wurde erstellt.")));
                } else {
                    //Save changes
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Save_Changes", new Example("ENG", "&7The changes have been saved."), new Example("GER", "&7Die Änderungen wurden gespeichert.")));
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }).setCloseOnClick(true));

        setItem(6, new ItemComponent(new ItemBuilder(Material.STAINED_CLAY).setColor(DyeColor.RED).setName("§7» §c" + Lang.get("Cancel", new Example("ENG", "Cancel"), new Example("GER", "Abbrechen")) + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                editor.exit();

                if(editor.getBackupPortal() == null) {
                    //NO CREATION
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Not_Created", new Example("ENG", "&7The portal has &cnot &7been created."), new Example("GER", "&7Das Portal wurde &cnicht &7erstellt.")));
                } else {
                    //Delete changes
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Delete_Changes", new Example("ENG", "&7The changes have &cnot &7been saved."), new Example("GER", "&7Die Änderungen wurden &cnicht &7gespeichert.")));
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }).setCloseOnClick(true));
    }

    public PortalEditor getEditor() {
        return editor;
    }
}
