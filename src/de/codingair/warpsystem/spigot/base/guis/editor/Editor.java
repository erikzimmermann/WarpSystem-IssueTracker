package de.codingair.warpsystem.spigot.base.guis.editor;

import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Editor<C> extends SimpleGUI {
    public static final String ITEM_TITLE_COLOR = "§6§n";
    public static final String ITEM_SUB_TITLE_COLOR = "§3";
    public static final String TITLE_COLOR = "§c§n";
    public static final String ITEM_SUB_TITLE_WARNING = "§c";

    private SoundData successSound = null;
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

    public void updatePage() {
        if(getCurrent() != null) {
            getCurrent().initialize(getPlayer());

            for(int i = 10; i < 16; i++) {
                removeButton(i);
                Button b = getCurrent().getButton(i);
                if(b != null) addButton(b);
                else setItem(i, null);
            }
            for(int i = 19; i < 25; i++) {
                removeButton(i);
                Button b = getCurrent().getButton(i);
                if(b != null) addButton(b);
                else setItem(i, null);
            }

            GUI.updateInventory(getPlayer());
        }
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

        addButton(new SyncButton(8) {
            @Override
            public ItemStack craftItem() {
                boolean cancel = canCancel();
                return new ItemBuilder(cancel ? XMaterial.RED_TERRACOTTA : XMaterial.LIGHT_GRAY_TERRACOTTA).setName((cancel ? "§c" : "§7") + Lang.get("Cancel")).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                backup.cancel(clone);
                if(getCancelSound() != null) getCancelSound().play(player);
            }

            @Override
            public boolean canClick(ClickType click) {
                return canCancel();
            }
        }.setOption(option).setCloseOnClick(true));

        addButton(new SyncButton(8, 2) {
            @Override
            public ItemStack craftItem() {
                boolean finish = canFinish();
                return new ItemBuilder(finish ? XMaterial.LIME_TERRACOTTA : XMaterial.LIGHT_GRAY_TERRACOTTA).setName((finish ? "§a" : "§7") + Lang.get("Finish") + finishButtonNameAddition()).addLore(finishButtonLoreAddition()).getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                backup.applyTo(clone);

                SoundData sound = getSuccessSound();
                if(sound != null) sound.play(player);

                String msg = getSuccessMessage();
                if(msg != null) getPlayer().sendMessage(msg);
            }

            @Override
            public boolean canClick(ClickType click) {
                return canFinish();
            }
        }.setOption(option).setCloseOnClick(true));
    }

    public String getSuccessMessage() {
        return Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved");
    }

    public void updatePageItems() {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1));

        int slot = 1;

        for(PageItem page : pages) {
            Page link = null;

            Button b = page.getPageButton().setOption(option);

            if(page == getCurrent()) {
                b.getItem().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                ItemMeta m = b.getItem().getItemMeta();
                m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                b.getItem().setItemMeta(m);
            } else {
                for(Enchantment enchantment : b.getItem().getEnchantments().keySet()) {
                    b.getItem().removeEnchantment(enchantment);
                }
                link = page;
            }

            b.setSlot(slot++);
            b.setLink(link);
            addButton(b);
        }
    }

    public String finishButtonNameAddition() {
        return "";
    }

    public List<String> finishButtonLoreAddition() {
        return null;
    }

    public boolean canFinish() {
        return true;
    }

    public boolean canCancel() {
        return true;
    }

    public void updateShowIcon() {
        setItem(8, 1, this.showIcon.buildIcon());
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

    public SoundData getSuccessSound() {
        return successSound;
    }

    public void setSuccessSound(SoundData successSound) {
        this.successSound = successSound;
    }
}
