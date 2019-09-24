package de.codingair.warpsystem.spigot.base.guis.editor;

import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Editor<C> extends SimpleGUI {
    public static final String ITEM_TITLE_COLOR = "§6§n";
    public static final String ITEM_SUB_TITLE_COLOR = "§3";
    public static final String TITLE_COLOR = "§c§n";

    private PageItem[] pages;
    private C clone;
    private Backup<C> backup;
    private ShowIcon showIcon;

    public Editor(Player p, C clone, Backup<C> backup, ShowIcon showIcon, PageItem... pages) {
        super(p, new Layout(), pages[0], WarpSystem.getInstance());

        List<PageItem> temp = new ArrayList<>();
        for(PageItem page : pages) {
            if(page != null) temp.add(page);
        }

        this.pages = temp.toArray(new PageItem[0]);
        temp.clear();

        this.clone = clone;
        this.backup = backup;
        this.showIcon = showIcon;

        update();

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {

            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(isClosingForGUI() || isClosingByButton() || isClosingByOperation()) return;
                backup.cancel(clone);
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {

            }

            @Override
            public void onMoveToTopInventory(ItemStack item, int oldRawSlot, List<Integer> newRawSlots) {

            }

            @Override
            public void onCollectToCursor(ItemStack item, List<Integer> oldRawSlots, int newRawSlot) {

            }
        });
    }

    private void update() {
        updatePageItems();
        updateShowIcon();
        initControllButtons();
    }

    public void updateControllButtons() {
        if(getButtonAt(8) == null) {
            initControllButtons();
            return;
        }

        ((SyncButton) getButtonAt(8)).update();
        ((SyncButton) getButtonAt(8, 2)).update();
    }

    public void initControllButtons() {
        ItemButtonOption option = new ItemButtonOption();
        option.setOnlyLeftClick(true);
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1));

        addButton(new SyncButton(8) {
            @Override
            public ItemStack craftItem() {
                boolean cancel = canCancel();
                return new ItemBuilder(cancel ? XMaterial.RED_TERRACOTTA : XMaterial.LIGHT_GRAY_TERRACOTTA).setName((cancel ? "§c" : "§7") + Lang.get("Cancel")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(canCancel()){
                    setClosingByButton(true);
                    player.closeInventory();
                    backup.cancel(clone);
                }
            }
        }.setOption(option));

        addButton(new SyncButton(8, 2) {
            @Override
            public ItemStack craftItem() {
                boolean finish = canFinish();
                return new ItemBuilder(finish ? XMaterial.LIME_TERRACOTTA : XMaterial.LIGHT_GRAY_TERRACOTTA).setName((finish ? "§a" : "§7") + Lang.get("Finish") + finishButtonNameAddition()).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(canFinish()){
                    setClosingByButton(true);
                    player.closeInventory();
                    backup.applyTo(clone);
                    getPlayer().sendMessage(Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved"));
                }
            }
        }.setOption(option));
    }

    public void updatePageItems() {
        ItemButtonOption option = new ItemButtonOption();
        option.setOnlyLeftClick(true);
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1));

        int slot = 1;

        for(PageItem page : pages) {
            ItemBuilder item = new ItemBuilder(page.getPageItem());
            Page link = null;

            if(page == getCurrent()) {
                item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                item.setHideEnchantments(true);
            } else link = page;

            Button b = new Button(slot++, item.getItem()) {
                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                }
            }.setOption(option);
            b.setLink(link);
            addButton(b);
        }
    }

    public String finishButtonNameAddition() {
        return "";
    }

    public boolean canFinish() {
        return true;
    }

    public boolean canCancel() {
        return true;
    }

    public void updateShowIcon() {
        setItem(8,1, this.showIcon.buildIcon());
    }

    @Override
    public void changePage(Page page, boolean update) {
        super.changePage(page, false);
        update();
        if(update) getPlayer().updateInventory();
    }

    @Override
    public void reinitialize() {
        super.reinitialize();
        update();
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
