package de.codingair.warpsystem.teleport.portals.menu;

import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
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
        setItem(1, new ItemComponent(new ItemBuilder(Material.REDSTONE).setName("§eAnimation").getItem()).setLink(this.animation));
        setItem(2, new ItemComponent(new ItemBuilder(Material.SIGN).setName("§eHologram").getItem()).setLink(this.hologram));
        setItem(3, new ItemComponent(new ItemBuilder(Material.ENDER_PEARL).setName("§eTeleport").getItem()).setLink(this.teleport));

        setItem(5, new ItemComponent(new ItemBuilder(Material.STAINED_CLAY).setColor(DyeColor.LIME).setName("§aSave").getItem(), (gui, ic, player, clickType) -> {
            this.editor.finish();

            if(editor.getBackupPortal() == null) {
                //CREATION
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Created", new Example("ENG", "&7The portal has been created."), new Example("GER", "&7Das Portal wurde erstellt.")));
            } else {
                //Save changes
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Save_Changes", new Example("ENG", "&7The changes have been saved."), new Example("GER", "&7Die Änderungen wurden gespeichert.")));
            }
        }).setCloseOnClick(true));

        setItem(6, new ItemComponent(new ItemBuilder(Material.STAINED_CLAY).setColor(DyeColor.RED).setName("§cCancel").getItem(), (gui, ic, player, clickType) -> {
            this.editor.exit();

            if(editor.getBackupPortal() == null) {
                //NO CREATION
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Not_Created", new Example("ENG", "&7The portal has &cnot &7been created."), new Example("GER", "&7Das Portal wurde &cnicht &7erstellt.")));
            } else {
                //Delete changes
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Delete_Changes", new Example("ENG", "&7The changes have &cnot &7been saved."), new Example("GER", "&7Die Änderungen wurden &cnicht &7gespeichert.")));
            }
        }).setCloseOnClick(true));
    }

    public PortalEditor getEditor() {
        return editor;
    }
}
