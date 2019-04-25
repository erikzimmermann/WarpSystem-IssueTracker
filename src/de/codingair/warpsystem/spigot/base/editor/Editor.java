package de.codingair.warpsystem.spigot.base.editor;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Editor<C> extends SimpleGUI {
    private PageItem[] pages;
    private C clone;
    private Backup<C> backup;
    private ShowIcon showIcon;

    public Editor(Player p, JavaPlugin plugin, C clone, Backup<C> backup, ShowIcon showIcon, PageItem... pages) {
        super(p, new Layout(), pages[0], plugin);

        this.pages = pages;
        this.clone = clone;
        this.backup = backup;
        this.showIcon = showIcon;

        updatePageItems();
        updateShowIcon();
        initControllButtons();
    }

    public void initControllButtons() {
        ItemButtonOption option = new ItemButtonOption();
        option.setOnlyLeftClick(true);
        option.setCloseOnClick(true);
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1));

        addButton(new ItemButton(8, new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§c" + Lang.get("Cancel")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
            }
        }.setOption(option));

        addButton(new ItemButton(8, 2, new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§a" + Lang.get("Finish")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                backup.applyTo(clone);
                getPlayer().sendMessage(Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved"));
            }
        }.setOption(option));
    }

    public void updatePageItems() {
        int slot = 1;

        for(PageItem page : pages) {
            ItemBuilder item = new ItemBuilder(page.getPageItem());
            if(page == getCurrent()) {
                item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                item.setHideEnchantments(true);
            }

            setItem(slot++, item.getItem());
        }
    }

    public void updateShowIcon() {
        setItem(8,1, this.showIcon.buildIcon());
    }

    @Override
    public void changePage(Page page, boolean update) {
        super.changePage(page, false);
        updatePageItems();
        updateShowIcon();
        if(update) getPlayer().updateInventory();
    }

    public Backup<C> getBackup() {
        return backup;
    }

    public C getClone() {
        return clone;
    }

    public ShowIcon getShowIcon() {
        return showIcon;
    }

    public PageItem[] getPages() {
        return pages;
    }
}
