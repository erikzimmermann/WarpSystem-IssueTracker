package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CostsAction;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PFunctions extends PMain {
    public PFunctions(Player player, PMain main) {
        super(player, main);
    }

    public PFunctions(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, item, name, slot, category, isCategory, true);
        super.setFunctions(this);
        super.initialize(p);
        initialize(p);
    }

    public PFunctions(Player p, Icon icon) {
        super(p, icon, true);
        super.setFunctions(this);
        super.initialize(p);
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        Button aB = getButton(1);
        Button fB = getButton(2);
        Button dB = getButton(3);

        aB.setItem(new ItemBuilder(aB.getItem()).removeEnchantments().getItem());
        fB.setItem(new ItemBuilder(fB.getItem()).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem());
        dB.setItem(new ItemBuilder(dB.getItem()).removeEnchantments().getItem());

        if(aB.getLink() == null) aB.setLink(getAppearance());
        if(fB.getLink() != null) fB.setLink(null);
        if(dB.getLink() == null) dB.setLink(getDestination());

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(getIcon().isDisabled() ? XMaterial.ROSE_RED : XMaterial.LIME_DYE)
                        .setName("§6§n" + Lang.get("Status"))
                        .setLore("§3" + Lang.get("Current") + ": " + (getIcon().isDisabled() ?
                                        "§c" + Lang.get("Disabled") :
                                        "§a" + Lang.get("Enabled")), "",
                                getIcon().isDisabled() ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Enable_This_Icon") :
                                        "§3" + Lang.get("Leftclick") + ": §c" + Lang.get("Disable_This_Icon"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    getIcon().setDisabled(!getIcon().isDisabled());
                    update();
                }
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                CommandAction action = getIcon().getAction(Action.COMMAND);
                List<String> commands = action == null ? null : action.getValue();
                List<String> commandInfo = new ArrayList<>();

                if(commands != null) {
                    for(String command : commands) {
                        commandInfo.add("§7- '§r" + command + "§7'");
                    }
                }

                List<String> lore = new ArrayList<>();
                if(commands != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return new ItemBuilder(XMaterial.REDSTONE)
                        .setName("§6§n" + Lang.get("Command"))
                        .setLore("§3" + Lang.get("Current") + ": " + (commands == null ? "§c" + Lang.get("Not_Set") : ""))
                        .addLore(commandInfo)
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                        .addLore(lore)
                        .getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Command"));
                    return;
                }

                if(!input.startsWith("/")) input = "/" + input;

                e.setClose(true);

                CommandAction action = getIcon().getAction(Action.COMMAND);
                if(action == null) {
                    action = new CommandAction(input);
                    getIcon().addAction(action);
                } else {
                    action.getValue().add(input);
                }

                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {

            }

            @Override
            public ItemStack craftAnvilItem() {
                return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Command") + "...").getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    getIcon().removeAction(Action.COMMAND);
                    update();
                }
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(3, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                String permission = getIcon().getPermission();

                List<String> lore = new ArrayList<>();
                if(permission != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return new ItemBuilder(XMaterial.ENDER_EYE)
                        .setName("§6§n" + Lang.get("Permission"))
                        .setLore("§3" + Lang.get("Current") + ": " + (permission == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + permission + "§7'"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (permission == null ? Lang.get("Set") : Lang.get("Change")))
                        .addLore(lore)
                        .getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Permission"));
                    return;
                }

                e.setClose(true);
                getIcon().setPermission(e.getInput());
                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {

            }

            @Override
            public ItemStack craftAnvilItem() {
                return new ItemBuilder(XMaterial.PAPER).setName(getIcon().getPermission() == null ? Lang.get("Permission") + "..." : getIcon().getPermission()).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    getIcon().setPermission(null);
                    update();
                }
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(4, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                CostsAction action = getIcon().getAction(Action.COSTS);
                double costs = action == null ? 0 : action.getValue();
                String costsPrint = costs + "";
                if(costsPrint.endsWith(".0")) costsPrint = costsPrint.substring(0, costsPrint.length() - 2);

                List<String> lore = new ArrayList<>();
                if(costs != 0) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return new ItemBuilder(XMaterial.GOLD_NUGGET)
                        .setName("§6§n" + Lang.get("Costs"))
                        .setLore("§3" + Lang.get("Current") + ": " + (costs == 0 ? "§c" + Lang.get("Not_Set") : "§7" + costsPrint + " " + Lang.get("Coins")))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (costs == 0 ? Lang.get("Set") : Lang.get("Change")))
                        .addLore(lore)
                        .getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                    return;
                }

                double costs;
                try {
                    costs = Double.parseDouble(input);
                } catch(NumberFormatException ex) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                    return;
                }

                if(costs < 0) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                    return;
                }

                e.setClose(true);

                if(costs == 0) {
                    getIcon().removeAction(Action.COSTS);
                } else {
                    getIcon().addAction(new CostsAction(costs));
                }

                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {

            }

            @Override
            public ItemStack craftAnvilItem() {
                CostsAction action = getIcon().getAction(Action.COSTS);
                double costs = action == null ? 0 : action.getValue();
                String costsPrint = costs + "";
                return new ItemBuilder(XMaterial.PAPER).setName(costsPrint).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    getIcon().removeAction(Action.COSTS);
                    update();
                }
            }
        }.setOption(option));
    }
}
