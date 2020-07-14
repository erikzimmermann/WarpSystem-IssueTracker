package de.codingair.warpsystem.spigot.base.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.api.PAPI;
import de.codingair.warpsystem.spigot.api.chatinput.ChatInputEvent;
import de.codingair.warpsystem.spigot.api.chatinput.SyncChatInputGUIButton;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.guis.GSimpleWarpList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DestinationPage extends PageItem {
    private final Destination destination;
    private final Origin origin;
    private final Button[] extra;
    private boolean showOptions = false;

    public DestinationPage(Player player, String title, Destination destination, Origin origin, Button... extra) {
        super(player, title, null, false);
        this.destination = destination;
        this.origin = origin;
        this.extra = extra;
        initialize(player);
    }

    @Override
    public Button getPageButton() {
        return new SyncButton(0) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Destination") + (showOptions ? "§8 (§7" + Lang.get("Options") + "§8)" : ""));
                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": " + (showOptions ? "§c" + Lang.get("Back") : "§7" + Lang.get("Options")));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isRightClick()) {
                    toggle(player);
                    update();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setLinkTrigger(ClickType.LEFT, ClickType.RIGHT);
    }

    private void toggle(Player player) {
        showOptions = !showOptions;
        for(int i = 1; i < 8; i++) {
            removeButton(i, 2);
        }

        if(getLast() != null && getLast().getCurrent() == this) getLast().updatePage();
        else initialize(player);
    }

    @Override
    public boolean initialize(SimpleGUI gui) {
        boolean result = super.initialize(gui);
        updateDestinationButtons();
        return result;
    }

    @Override
    public void initialize(Player p) {
        if(showOptions) new Options().setup(p);
        else new Normal().setup(p);
    }

    public void updateDestinationButtons() {
        for(int i = 1; i < 8; i++) {
            Button button = getButton(i, 2);
            if(button instanceof SyncButton) {
                ((SyncButton) button).update();
            }
        }
    }

    public Destination getDestination() {
        return destination;
    }

    private double trim(double d) {
        return ((double) (int) (d * 100)) / 100;
    }

    private class Options {
        protected void setup(Player p) {
            ItemButtonOption option = new StandardButtonOption();

            addButton(new SyncChatInputGUIButton(1, 2, ClickType.LEFT) {
                @Override
                public void onEnter(ChatInputEvent e) {
                    destination.getCustomOptions().setCustomMessage(e.getText());

                    if(!e.getText().isEmpty()) {
                        Boolean send = destination.getCustomOptions().getMessage();
                        boolean sending = origin.sendTeleportMessage();
                        if(send != null && !send) {
                            if(send == sending) destination.getCustomOptions().setMessage(true);
                            else destination.getCustomOptions().setMessage(null);
                        } else if(send == null && !sending) destination.getCustomOptions().setMessage(true);
                    }
                    e.setClose(true);
                }

                @Override
                public ItemStack craftItem() {
                    Boolean send = destination.getCustomOptions().getMessage();
                    boolean sending = send != null ? send : origin.sendTeleportMessage();
                    ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Teleport_Message") + "§8 (" + (sending ? "§7" + Lang.get("Enabled") : "§c" + Lang.get("Disabled")) + "§8)");

                    String message = destination.getCustomOptions().getCustomMessage();
                    if(message != null) message = PAPI.convert(message, p).replace("%player%", p.getName()).replace("%PLAYER%", p.getName());


                    List<String> msg = TextAlignment.lineBreak(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (message == null ? "§e" + Lang.get("Default") : "§7\"§f" + de.codingair.codingapi.utils.ChatColor.translateAlternateColorCodes('&', message) + "§7\""), 100);

                    builder.addLore(msg.remove(0));
                    if(!msg.isEmpty()) builder.addLore(msg);

                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change") + " §8(§7" + Lang.get("Toggle") + "§8)");
                    if(message != null) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return builder.getItem();
                }

                @Override
                public boolean canTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
                    if(trigger == ClickType.LEFT && destination.getCustomOptions().getCustomMessage() != null) {
                        TextComponent tc = new TextComponent(Lang.getPrefix() + "§7" + Lang.get("Teleport_Message") + ": ");
                        TextComponent click = new TextComponent("§e" + ChatColor.stripColor(Lang.get("Click_Hover")));
                        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, destination.getCustomOptions().getCustomMessage().replace('§', '&')));

                        tc.addExtra(click);
                        player.spigot().sendMessage(tc);
                    }

                    return super.canTrigger(e, trigger, player);
                }

                @Override
                public boolean canClick(ClickType click) {
                    if(click == ClickType.RIGHT) {
                        return destination.getCustomOptions().getCustomMessage() != null;
                    }

                    return click == ClickType.LEFT || click == ClickType.SHIFT_LEFT;
                }

                @Override
                public void onOtherClick(InventoryClickEvent e) {
                    if(e.getClick() == ClickType.RIGHT) {
                        destination.getCustomOptions().setCustomMessage(null);
                        update();
                    } else if(e.getClick() == ClickType.SHIFT_LEFT) {
                        Boolean send = destination.getCustomOptions().getMessage();
                        boolean sending = origin.sendTeleportMessage();

                        if(send == null) destination.getCustomOptions().setMessage(!sending);
                        else if(send != sending) destination.getCustomOptions().setMessage(null);
                        else if(send == sending) destination.getCustomOptions().setMessage(!send);
                        update();
                    }
                }
            }.setOption(option));

            addButton(new SyncButton(2, 2) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = new ItemBuilder(XMaterial.ARMOR_STAND).setName("§6§n" + Lang.get("Rotation") + "§8 (§7Yaw + Pitch§8)");
                    boolean b = destination.getCustomOptions().isRotation();

                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (b ? "§a" + Lang.get("Enabled") : "§c" + Lang.get("Disabled")));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"));
                    return builder.getItem();
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT;
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    destination.getCustomOptions().setRotation(!destination.getCustomOptions().isRotation());
                    update();
                }
            }.setOption(option));

            addButton(new SyncButton(3, 2) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK).setName("§6§n" + Lang.get("Teleport_Delay") + Lang.PREMIUM_LORE);

                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (destination.getCustomOptions().getDelay(-1) == -1 ? "§7" + WarpSystem.opt().getTeleportDelay() + " §8(§e" + Lang.get("Default") + "§8)" : "§7" + destination.getCustomOptions().getDelay(-1)));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c- §8(§7" + Lang.get("Shift") + "§8)");
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a+ §8(§7" + Lang.get("Shift") + "§8)");
                    return builder.getItem();
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT || click == ClickType.SHIFT_LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT;
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    Lang.PREMIUM_CHAT(player);
                }
            }.setOption(option));
        }
    }

    private class Normal {
        protected void setup(Player p) {
            ItemButtonOption option = new StandardButtonOption();

            int slot = 1;
            if(extra != null && extra.length > 0) {
                for(Button button : extra) {
                    if(slot == 7) break;
                    button.setSlot(slot++ + 18);
                    button.setOption(option);
                    addButton(button);
                }
            }

            addButton(new SyncButton(slot++, 2) {
                private int editingOffset = 0;

                @Override
                public ItemStack craftItem() {
                    String name = null;
                    if(destination.getType() == DestinationType.SimpleWarp) name = destination.getId();

                    List<String> lore = new ArrayList<>();
                    if(name != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                    else lore.add("§3" + Lang.get("Shift_Leftclick") + ": §a" + Lang.get("Create") + Lang.PREMIUM_LORE);

                    ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("SimpleWarps"))
                            .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§f" + ChatColor.translateAlternateColorCodes('&', name.replace("_", " ")) + "§7'"),
                                    "", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore);

                    builder.addLore(" ");
                    builder.addLore("§6" + Lang.get("Max_Random_Offset") + Lang.PREMIUM_LORE);
                    builder.addLore("§3" + Lang.get("Shift_Rightclick") + ": §b" + Lang.get("Edit"));
                    builder.addLore("  §8» §7X: §e" + (destination.getOffsetX() == 0 ? "0" : "-" + destination.getOffsetX() + " - " + destination.getOffsetX()));
                    builder.addLore("  §8» §7Y: §e" + (destination.getOffsetY() == 0 ? "0" : "0 - " + destination.getOffsetY()));
                    builder.addLore("  §8» §7Z: §e" + (destination.getOffsetZ() == 0 ? "0" : "-" + destination.getOffsetZ() + " - " + destination.getOffsetZ()));

                    return builder.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) {
                        if(e.isShiftClick()) {
                            Lang.PREMIUM_CHAT(player);
                        } else {

                            getLast().changeGUI(new GSimpleWarpList(p) {
                                @Override
                                public void onClick(SimpleWarp value, ClickType clickType) {
                                    destination.setId(value.getName());
                                    destination.setType(DestinationType.SimpleWarp);
                                    destination.setAdapter(DestinationType.SimpleWarp.getInstance());
                                    updateDestinationButtons();

                                    fallBack();
                                }

                                @Override
                                public void onClose() {
                                    fallBack();
                                }

                                @Override
                                public void buildItemDescription(List<String> lore) {
                                    lore.add("");
                                    lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                                }
                            }, true);
                        }
                    } else if(e.isRightClick()) {
                        if(e.isShiftClick()) {
                            Lang.PREMIUM_CHAT(player);
                        } else {
                            destination.setId(null);
                            destination.setAdapter(null);
                            destination.setType(null);

                            updateDestinationButtons();
                        }
                    }
                }
            }.setOption(option));

            if(WarpSystem.getInstance().isOnBungeeCord()) {
                addButton(new SyncButton(slot++, 2) {
                    @Override
                    public ItemStack craftItem() {
                        String name = null;
                        if(destination.getType() == DestinationType.GlobalWarp) name = destination.getId();

                        List<String> lore = name == null ? null : new ArrayList<>();
                        if(lore != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                        return new ItemBuilder(XMaterial.ENDER_EYE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("GlobalWarps"))
                                .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§f" + ChatColor.translateAlternateColorCodes('&', name) + "§7'"),
                                        "", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                                .addLore(lore)
                                .getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(e.isLeftClick()) {
                            getLast().setClosingForGUI(true);
                            getLast().changeGUI(new GGlobalWarpList(player) {
                                @Override
                                public void onClick(String warp, ClickType clickType) {
                                    destination.setId(warp);
                                    destination.setType(DestinationType.GlobalWarp);
                                    destination.setAdapter(DestinationType.GlobalWarp.getInstance());
                                    updateDestinationButtons();

                                    this.setClosingForGUI(true);
                                    getLast().open();
                                }

                                @Override
                                public void onClose() {
                                }

                                @Override
                                public void buildItemDescription(List<String> lore) {
                                    lore.add("");
                                    lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                                }
                            }, true);
                        } else if(e.isRightClick()) {
                            destination.setId(null);
                            destination.setAdapter(null);
                            destination.setType(null);

                            updateDestinationButtons();
                        }
                    }
                }.setOption(option));

                addButton(new SyncButton(slot++, 2) {
                    @Override
                    public ItemStack craftItem() {
                        if(destination.getType() == DestinationType.Server) ;

                        return new ItemBuilder(XMaterial.ENDER_CHEST).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Server") + Lang.PREMIUM_LORE)
                                .setLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (Lang.get("Set")))
                                .getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        Lang.PREMIUM_CHAT(player);
                    }
                }.setOption(option));
            }
        }

        public void updateDestinationButtons() {
            for(int i = 1; i < 8; i++) {
                Button button = getButton(i, 2);
                if(button instanceof SyncButton) {
                    ((SyncButton) button).update();
                }
            }
        }

        public Destination getDestination() {
            return destination;
        }

        private double trim(double d) {
            return ((double) (int) (d * 100)) / 100;
        }
    }
}
