package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.MessageAction;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MessageButton extends SyncAnvilGUIButton {
    private final FeatureObject object;

    public MessageButton(int x, int y, FeatureObject object) {
        super(x, y, ClickType.LEFT);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        MessageAction action = object.getAction(Action.MESSAGE);
        List<String> messages = action == null ? null : action.getValue();
        List<String> messageInfo = new ArrayList<>();

        if(messages != null) {
            for(String msg : messages) {
                messageInfo.add("§7- '§f" + msg + "§7'");
            }
        }

        List<String> lore = new ArrayList<>();
        if(messages != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.BOOK)
                .setName("§6§n" + Lang.get("Message"))
                .setLore("§3" + Lang.get("Current") + ": " + (messages == null ? "§c" + Lang.get("Not_Set") : ""))
                .addLore(messageInfo)
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                .addLore(lore)
                .getItem();
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Something"));
            return;
        }

        if(!isOkay(input)) {
            return;
        }

        e.setClose(true);

        MessageAction action = object.getAction(Action.MESSAGE);
        if(action == null) {
            action = new MessageAction(input);
            object.addAction(action);
        } else {
            action.getValue().add(input);
        }

        update();
    }

    @Override
    public void onClose(AnvilCloseEvent e) {

    }

    public boolean isOkay(String command) {
        return true;
    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Message") + "...").getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            if(object.hasAction(Action.MESSAGE)) {
                if(object.getAction(MessageAction.class).getValue().size() == 1) object.removeAction(Action.MESSAGE);
                else object.getAction(MessageAction.class).getValue().remove(object.getAction(MessageAction.class).getValue().size() - 1);
            }
            update();
        }
    }
}
