package de.codingair.warpsystem.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.InterfaceListener;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButtonOption;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.Language.Example;
import de.codingair.warpsystem.Language.Lang;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Category;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class GConfig extends GUI {
    private Category category;

    private boolean editMode;
    private boolean close = false;

    public GConfig(Player p, Category category, boolean editMode) {
        super(p, "§c§l§nWarps§r §7- §6" + Lang.get("Config", new Example("ENG", "Config"), new Example("GER", "Config")), 9, WarpSystem.getInstance(), false);

        this.category = category;
        this.editMode = editMode;

        addListener(new InterfaceListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {
            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {
            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!close) {
                    Sound.ITEM_BREAK.playSound(p);
                    new GWarps(p, category, editMode).open();
                    close = true;
                }
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {
            }
        });

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        ItemStack leaves = new ItemBuilder(Material.LEAVES).setName("§0").getItem();
        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setName("§0").getItem();

        setItem(1, leaves);
        setItem(4, glass);
        setItem(8, leaves);

        addButton(new ItemButton(0, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back", new Example("ENG", "Back"), new Example("GER", "Zurück"))).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                close = true;
                new GWarps(p, category, editMode).open();
            }
        }.setOption(option).setCloseOnClick(true));

        ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance", new Example("ENG", "Maintenance"), new Example("GER", "Wartung")));

        if(!WarpSystem.maintenance)
            maintenance.setLore("", Lang.get("Leftclick_Maintenance_On", new Example("ENG", "&3Leftclick: &bEnable"), new Example("GER", "&3Leftclick: &bAktivieren")));
        else
            maintenance.setLore("", Lang.get("Leftclick_Maintenance_Off", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Leftclick: &cDeaktivieren")));

        addButton(new ItemButton(2, maintenance.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                WarpSystem.maintenance = !WarpSystem.maintenance;

                if(WarpSystem.maintenance) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Enabled", new Example("ENG", "&4The Maintenance-Mode was enabled."), new Example("GER", "&4Der Wartungs-Modus wurde aktiviert.")));
                } else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Disabled", new Example("ENG", "&4The Maintenance-Mode was disabled."), new Example("GER", "&4Der Wartungs-Modus wurde deaktiviert.")));
                }

                ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance", new Example("ENG", "Maintenance"), new Example("GER", "Wartung")));

                if(!WarpSystem.maintenance)
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_On", new Example("ENG", "&3Leftclick: &bEnable"), new Example("GER", "&3Linksklick: &bAktivieren")));
                else
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_Off", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")));

                setItem(maintenance.getItem());
            }
        }.setOption(option));

        ItemBuilder lang = new ItemBuilder(Material.BOOK).setName("§3§n" + Lang.get("Language", new Example("ENG", "Language"), new Example("GER", "Sprache")));
        lang.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + Lang.getCurrentLanguage());
        lang.addLore("", Lang.get("Leftclick_Prev_Lang", new Example("ENG", "&3Leftclick: &bPrevious language"), new Example("GER", "&3Linksklick: &bVorherige Sprache")));
        lang.addLore(Lang.get("Rightclick_Next_Lang", new Example("ENG", "&3Rightclick: &bNext language"), new Example("GER", "&3Rechtsklick: &bNächste Sprache")));

        addButton(new ItemButton(3, lang.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                close = true;

                if(e.isLeftClick()) {
                    int prevId = Lang.getLanguageId(Lang.getCurrentLanguage()) - 1;
                    if(prevId < 0) prevId = Lang.getLanguages().size() - 1;

                    Lang.setCurrentLanguage(Lang.getLanguage(prevId));
                } else {
                    int nextId = Lang.getLanguageId(Lang.getCurrentLanguage()) + 1;
                    if(nextId >= Lang.getLanguages().size()) nextId = 0;

                    Lang.setCurrentLanguage(Lang.getLanguage(nextId));
                }

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Lang", new Example("ENG", "&aThe language was changed to '%lang%'."),
                        new Example("GER", "&aDie Sprache wurde auf '%lang%' geändert.")).replace("%lang%", Lang.getCurrentLanguage()));

                reinitialize();
                setTitle("§c§l§nWarps§r §7- §6" + Lang.get("Config", new Example("ENG", "Config"), new Example("GER", "Config")));
            }
        }.setOption(option).setOnlyLeftClick(false));

        //TODO: Add other optionButtons
    }
}
