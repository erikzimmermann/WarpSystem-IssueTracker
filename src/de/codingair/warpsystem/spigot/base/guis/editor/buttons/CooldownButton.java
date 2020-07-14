package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CooldownButton extends SyncAnvilGUIButton {
    private final FeatureObject object;

    public CooldownButton(int x, int y, FeatureObject object) {
        super(x, y, ClickType.LEFT);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        long cooldown = object.getCooldown();
        return new ItemBuilder(XMaterial.CLOCK)
                .setName("§6§n" + Lang.get("Cooldown"))
                .setLore("§3" + Lang.get("Current") + ": " + (cooldown == 0 ? "§c" + Lang.get("Not_Set") : "§7" + StringFormatter.convertInTimeFormat(object.getCooldown())))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (cooldown == 0 ? Lang.get("Set") : Lang.get("Change")))
                .addLore(cooldown == 0 ? null : "§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"))
                .getItem();
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        String in = e.getInput(false);
        if(in == null || in.isEmpty()) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Syntax_info").replace("%SYNTAX%", "0s, 5m, 0h, 0d"));
            return;
        }

        long time;
        try {
            time = StringFormatter.convertFromTimeFormat(in);
        } catch(Exception ex) {
            time = -1;
        }

        playSound(e.getClickType(), e.getPlayer());

        if(time < 0) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Syntax_info").replace("%SYNTAX%", "0s, 5m, 0h, 0d"));
            return;
        }

        object.setCooldown(time);
        update();
        e.setClose(true);
    }

    @Override
    public void onClose(AnvilCloseEvent e) {

    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        if(object == null) return new ItemStack(Material.AIR);

        long cooldown = object.getCooldown();
        return new ItemBuilder(XMaterial.PAPER).setName(cooldown == 0 ? "0s, 5m, 0h, 0d" : StringFormatter.convertInTimeFormat(cooldown)).getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            object.removeAction(Action.COSTS);
            update();
        }
    }
}
