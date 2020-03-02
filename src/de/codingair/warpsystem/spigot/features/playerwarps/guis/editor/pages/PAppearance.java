package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.LoreButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PAppearance extends PageItem {
    private final PlayerWarp warp, original;
    private final String startName;
    private final boolean editing;

    public PAppearance(Player p, PlayerWarp warp, PlayerWarp original, boolean editing) {
        super(p, PWEditor.getMainTitle(), new ItemBuilder(XMaterial.PAINTING).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Appearance")).getItem(), false);

        this.warp = warp;
        this.original = original;
        this.editing = editing;
        this.startName = this.warp.getName();
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        int slot = 1;

        if(!PlayerWarpManager.getManager().isForcePlayerHead())
            addButton(new SyncButton(slot++, 2) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = new ItemBuilder(XMaterial.ITEM_FRAME).setName("§6§n" + Lang.get("Item"));

                    if(!warp.isStandardItem()) {
                        if(original.isStandardItem()) builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getItemCosts()));
                        else if(!warp.isSameItem(original.getItem())) builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getItemChangeCosts()));
                    }

                    if(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR)
                        builder.addLore("§c" + Lang.get("No_Item_In_Hand"));
                    else if(warp.getItem().getType() != p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType())
                        builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Item"));

                    if(!warp.isStandardItem()) {
                        if(builder.getLore() == null || builder.getLore().size() <= 1) builder.addLore("");
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                    } else if(!warp.isSameItem(original.getItem())) {
                        if(builder.getLore() == null || builder.getLore().size() <= 1) builder.addLore("");
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset"));
                    }

                    return builder.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) {
                        warp.changeItem(new ItemBuilder(player.getInventory().getItem(player.getInventory().getHeldItemSlot())));

                        getLast().updateShowIcon();
                        update();
                        updateCosts();
                    } else if(e.isRightClick()) {
                        if(!warp.isStandardItem()) warp.resetItem();
                        else if(!warp.isSameItem(original.getItem())) warp.changeItem(original.getItem());

                        getLast().updateShowIcon();
                        update();
                        updateCosts();
                    }
                }

                @Override
                public boolean canClick(ClickType click) {
                    if(click == ClickType.LEFT) {
                        return !(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR
                                || warp.getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType());
                    } else if(click == ClickType.RIGHT) {
                        return !warp.isStandardItem() || !warp.isSameItem(original.getItem());
                    }

                    return false;
                }
            }.setOption(option));

        addButton(new SyncAnvilGUIButton(slot++, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                if(warp.getName() == null) return new ItemStack(Material.AIR);

                return new ItemBuilder(XMaterial.NAME_TAG)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Name"))
                        .setLore(PWEditor.getCostsMessage(editing && !original.getName().equals(warp.getName()) ? PlayerWarpManager.getManager().getNameChangeCosts() : 0))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§7'§r" + org.bukkit.ChatColor.translateAlternateColorCodes('&', warp.getName()) + "§7'")
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name"),
                                (warp.getName().equals(startName) ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset")))
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                if(warp.getName() == null) return new ItemStack(Material.AIR);
                return new ItemBuilder(Material.PAPER).setName(warp.getName() == null ? Lang.get("Name") + "..." : warp.getName().replace("§", "&")).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    warp.setName(startName);
                    getLast().updateShowIcon();
                    update();
                    updateCosts();
                }
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                    return;
                }

                if(!startName.equalsIgnoreCase(warp.getName()) && !warp.getName(false).equalsIgnoreCase(warp.getName()) && PlayerWarpManager.getManager().exists(warp.getName())) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                    return;
                }

                warp.setName(e.getInput());
                getLast().updateShowIcon();
                updateCosts();
                update();
                e.setClose(true);
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || (click == ClickType.RIGHT && !warp.getName().equals(startName));
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }
        }.setOption(option));

        addButton(new LoreButton(slot++, 2, warp.getItem()) {
            @Override
            public boolean canClick(ClickType click) {
                if(click == ClickType.LEFT) {
                    return warp.getItem().getLore() == null || warp.getItem().getLore().size() < PlayerWarpManager.getManager().getDescriptionMaxLines();
                } else if(click == ClickType.RIGHT) {
                    boolean empty0 = warp.getItem().getLore() == null || warp.getItem().getLore().isEmpty();
                    boolean empty1 = original.getItem().getLore() == null || original.getItem().getLore().isEmpty();
                    return !empty0 || !empty1;
                }

                return false;
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    if(!warp.getItem().getLore().isEmpty()) {
                        warp.getItem().getLore().remove(warp.getItem().getLore().size() - 1);
                    } else warp.getItem().setLore(original.getItem().getLore());

                    updatingLore(warp.getItem());
                    update();
                }
            }

            @Override
            public ItemStack craftItem() {
                List<String> loreOfItem = warp.getItem().getLore();
                List<String> lore = new ArrayList<>();
                if(loreOfItem == null) lore = null;
                else {
                    for(String s : loreOfItem) {
                        lore.add("§7- '§r" + s + "§7'");
                    }
                }

                int length = 0;
                if(original.getItem().getLore() != null)
                    for(String s : original.getItem().getLore()) {
                        length += s.replaceFirst("§f", "").length();
                    }

                length = -length;
                if(warp.getItem().getLore() != null) {
                    for(String s : warp.getItem().getLore()) {
                        length += s.replaceFirst("§f", "").length();
                    }
                }

                ItemBuilder builder = new ItemBuilder(XMaterial.PAPER).setName("§6§n" + Lang.get("Description"));

                if(length < 0) builder.addLore(PWEditor.getFreeMessage(-length + " " + Lang.get("Characters")));
                else builder.setLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getDescriptionCosts() * length));

                builder.addLore("§3" + Lang.get("Current") + ": " + (lore == null || lore.isEmpty() ? "§c" + Lang.get("Not_Set") : ""))
                        .addLore(lore).addLore("");

                if(lore == null || lore.size() < PlayerWarpManager.getManager().getDescriptionMaxLines())
                    builder.addLore("§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add_Line"));

                boolean empty0 = warp.getItem().getLore() == null || warp.getItem().getLore().isEmpty();
                boolean empty1 = original.getItem().getLore() == null || original.getItem().getLore().isEmpty();

                if(!empty0 || !empty1)
                    builder.addLore("§3" + Lang.get("Rightclick") + ": §c" + (!empty0 ? Lang.get("Remove") : Lang.get("Reset")));

                return builder.getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Lore"));
                    return;
                }

                if(input.length() < PlayerWarpManager.getManager().getDescriptionLineMinLength() || input.length() > PlayerWarpManager.getManager().getDescriptionLineMaxLength()) {
                    p.sendMessage(Lang.getPrefix() +
                            Lang.get("Message_Too_Long_Too_Short")
                                    .replace("%MIN%", PlayerWarpManager.getManager().getDescriptionLineMinLength() + "")
                                    .replace("%MAX%", PlayerWarpManager.getManager().getDescriptionLineMaxLength() + "")
                    );
                    return;
                }

                e.setClose(true);

                warp.getItem().addLore(org.bukkit.ChatColor.WHITE + org.bukkit.ChatColor.translateAlternateColorCodes('&', input));
                updatingLore(warp.getItem());
                update();
            }

            @Override
            public boolean canTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
                return warp.getItem().getLore() == null || warp.getItem().getLore().size() < PlayerWarpManager.getManager().getDescriptionMaxLines();
            }

            @Override
            public void updatingLore(ItemBuilder toChange) {
                getLast().updateShowIcon();
                updateCosts();
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(slot++, 2, ClickType.LEFT) {
            @Override
            public boolean canClick(ClickType click) {
                if(click == ClickType.RIGHT) {
                    return warp.getTeleportMessage() != null || !Objects.equals(warp.getTeleportMessage(), original.getTeleportMessage());
                }

                return click == ClickType.LEFT;
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Teleport_Message"));

                List<String> msg = TextAlignment.lineBreak((warp.getTeleportMessage() == null ? "§c" + Lang.get("Not_Set") : "§7\"§f" + ChatColor.translateAlternateColorCodes('&', warp.getTeleportMessage()) + "§7\""), 100);

                int length = (warp.getTeleportMessage() == null ? 0 : warp.getTeleportMessage().length()) - (original.getTeleportMessage() == null ? 0 : original.getTeleportMessage().length());
                if(length < 0) builder.addLore(PWEditor.getFreeMessage(-length + " " + Lang.get("Characters")));
                else builder.addLore(PWEditor.getCostsMessage(length * PlayerWarpManager.getManager().getMessageCosts()));

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + msg.remove(0));
                if(!msg.isEmpty()) builder.addLore(msg);

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change"));
                if(warp.getTeleportMessage() != null || !Objects.equals(warp.getTeleportMessage(), original.getTeleportMessage()))
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + (Objects.equals(warp.getTeleportMessage(), original.getTeleportMessage()) ? Lang.get("Remove") : Lang.get("Reset")));

                return builder.getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                String input = e.getInput();

                if(input == null || input.length() < PlayerWarpManager.getManager().getMessageMinLength() || input.length() > PlayerWarpManager.getManager().getMessageMaxLength()) {
                    p.sendMessage(Lang.getPrefix() +
                            Lang.get("Message_Too_Long_Too_Short")
                                    .replace("%MIN%", PlayerWarpManager.getManager().getMessageMinLength() + "")
                                    .replace("%MAX%", PlayerWarpManager.getManager().getMessageMaxLength() + "")
                    );
                    return;
                }

                warp.setTeleportMessage(input);

                updateCosts();
                e.setClose(true);
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
                updatePage();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER)
                        .setName(warp.getTeleportMessage() == null ? (Lang.get("Message") + "...") : warp.getTeleportMessage())
                        .getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    if(!Objects.equals(warp.getTeleportMessage(), original.getTeleportMessage())) {
                        warp.setTeleportMessage(original.getTeleportMessage());
                    } else warp.setTeleportMessage(null);

                    update();
                    updateCosts();
                }
            }
        }.setOption(option));
    }

    public void updateCosts() {
        ((PWEditor) getLast()).updateCosts();
    }
}
