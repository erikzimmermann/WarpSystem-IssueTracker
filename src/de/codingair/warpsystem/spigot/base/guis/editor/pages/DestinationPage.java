package de.codingair.warpsystem.spigot.base.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.guis.GSimpleWarpList;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.RequestServerStatusPacket;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DestinationPage extends PageItem {
    private String server = null;
    private boolean online = false;
    private boolean pinging = false;
    private Destination destination;

    public DestinationPage(Player player, String title, Destination destination) {
        super(player, title, new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Destination")).getItem(), false);
        this.destination = destination;
        initialize(player);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            private int editingOffset = 0;

            @Override
            public ItemStack craftItem() {
                String name = null;
                if(destination.getType() == DestinationType.SimpleWarp) name = destination.getId();

                List<String> lore = new ArrayList<>();
                if(editingOffset == 0) {
                    lore.add("");
                    lore.add("§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")));
                    if(name != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                    else lore.add("§3" + Lang.get("Shift_Leftclick") + ": §a" + Lang.get("Create"));
                }

                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("SimpleWarps"))
                        .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name.replace("_", " ")) + "§7'"))
                        .addLore(lore);

                builder.addLore(" ");
                builder.addLore("§6" + (editingOffset == 0 ? "" : "§n") + Lang.get("Max_Random_Offset"));

                if(editingOffset != 0) {
                    double value = editingOffset == 1 ? destination.getOffsetX() : editingOffset == 2 ? destination.getOffsetY() : destination.getOffsetZ();

                    builder.addLore(" ");
                    builder.addLore("§3" + Lang.get("Leftclick") + ": §" + (value == 0 ? "c" : "a") + Lang.get("Reduce"));
                    builder.addLore("§3" + Lang.get("Rightclick") + ": §" + (value == 100 ? "c" : "a") + Lang.get("Enlarge"));
                    builder.addLore("§3" + Lang.get("Shift_Leftclick") + ": §a" + Lang.get("Choose"));
                }

                builder.addLore("§3" + Lang.get("Shift_Rightclick") + ": §b" + (editingOffset == 0 ? Lang.get("Edit") : "↓ §8(§7" + Lang.get("Close") + "§8)"));

                if(editingOffset != 0) builder.addLore(" ");

                builder.addLore("  §8» §7X: §e" + destination.getOffsetX() + (editingOffset == 1 ? " §c§l«" : ""));
                builder.addLore("  §8» §7Y: §e" + destination.getOffsetY() + (editingOffset == 2 ? " §c§l«" : ""));
                builder.addLore("  §8» §7Z: §e" + destination.getOffsetZ() + (editingOffset == 3 ? " §c§l«" : ""));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(editingOffset == 0) {
                    if(e.isLeftClick()) {
                        getLast().setClosingForGUI(true);
                        if(e.isShiftClick()) {
                            AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                                @Override
                                public void onClick(AnvilClickEvent e) {
                                    if(e.getSlot() == AnvilSlot.OUTPUT) {
                                        String input = e.getInput();

                                        if(input == null) {
                                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                            return;
                                        }

                                        if(SimpleWarpManager.getInstance().existsWarp(input)) {
                                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                            return;
                                        }

                                        player.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Created").replace("%WARP%", ChatColor.translateAlternateColorCodes('&', input)));
                                        SimpleWarp w;
                                        SimpleWarpManager.getInstance().addWarp(w = new SimpleWarp(player, input, null));

                                        destination.setId(w.getName());
                                        destination.setType(DestinationType.SimpleWarp);
                                        destination.setAdapter(DestinationType.SimpleWarp.getInstance());
                                        updateDestinationButtons();

                                        e.setClose(true);
                                        playSound(e.getClickType(), player);
                                    }
                                }

                                @Override
                                public void onClose(AnvilCloseEvent e) {
                                    e.setPost(() -> getLast().open());
                                }
                            }, new ItemBuilder(XMaterial.NAME_TAG).setName(Lang.get("Name") + "...").getItem());
                        } else {
                            new GSimpleWarpList(p) {
                                @Override
                                public void onClick(SimpleWarp value, ClickType clickType) {
                                    destination.setId(value.getName());
                                    destination.setType(DestinationType.SimpleWarp);
                                    destination.setAdapter(DestinationType.SimpleWarp.getInstance());
                                    updateDestinationButtons();

                                    this.setClosingForGUI(true);
                                    getLast().open();
                                }

                                @Override
                                public void onClose() {
                                    getLast().open();
                                }

                                @Override
                                public void buildItemDescription(List<String> lore) {
                                    lore.add("");
                                    lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                                }
                            }.open();
                        }
                    } else if(e.isRightClick()) {
                        if(e.isShiftClick()) {
                            editingOffset++;
                            if(editingOffset == 4) editingOffset = 0;
                            update();
                        } else {
                            destination.setId(null);
                            destination.setAdapter(null);
                            destination.setType(null);

                            updateDestinationButtons();
                        }
                    }
                } else {
                    if(e.isLeftClick()) {
                        if(e.isShiftClick()) {
                            getLast().setClosingForGUI(true);
                            AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                                @Override
                                public void onClick(AnvilClickEvent e) {
                                    if(e.getSlot() == AnvilSlot.OUTPUT) {
                                        String input = e.getInput();

                                        if(input == null) {
                                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                                            return;
                                        }

                                        input = input.replace(",", ".");
                                        double value;

                                        try {
                                            value = Double.parseDouble(input);
                                        } catch(NumberFormatException ex) {
                                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                                            return;
                                        }

                                        if(value < 0) {
                                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                                            return;
                                        }

                                        if(value > 100) value = 100;
                                        value = trim(value);

                                        if(editingOffset == 1) {
                                            destination.setOffsetX(value);
                                        } else if(editingOffset == 2) {
                                            destination.setOffsetY(value);
                                        } else if(editingOffset == 3) {
                                            destination.setOffsetZ(value);
                                        }

                                        update();

                                        e.setClose(true);
                                        playSound(e.getClickType(), player);
                                    }
                                }

                                @Override
                                public void onClose(AnvilCloseEvent e) {
                                    e.setPost(() -> getLast().open());
                                }
                            }, new ItemBuilder(XMaterial.NAME_TAG).setName("" + (editingOffset == 1 ? destination.getOffsetX() : editingOffset == 2 ? destination.getOffsetY() : destination.getOffsetZ())).getItem());
                        } else {
                            if(editingOffset == 1) {
                                destination.setOffsetX(trim(destination.getOffsetX() - 1));
                                if(destination.getOffsetX() < 0) destination.setOffsetX(0);
                            } else if(editingOffset == 2) {
                                destination.setOffsetY(trim(destination.getOffsetY() - 1));
                                if(destination.getOffsetY() < 0) destination.setOffsetY(0);
                            } else if(editingOffset == 3) {
                                destination.setOffsetZ(trim(destination.getOffsetZ() - 1));
                                if(destination.getOffsetZ() < 0) destination.setOffsetZ(0);
                            }

                            update();
                        }
                    } else if(e.isRightClick()) {
                        if(e.isShiftClick()) {
                            editingOffset++;
                            if(editingOffset == 4) editingOffset = 0;
                            update();
                        } else {
                            if(editingOffset == 1) {
                                destination.setOffsetX(trim(destination.getOffsetX() + 1));
                                if(destination.getOffsetX() > 100) destination.setOffsetX(0);
                            } else if(editingOffset == 2) {
                                destination.setOffsetY(trim(destination.getOffsetY() + 1));
                                if(destination.getOffsetY() > 100) destination.setOffsetY(0);
                            } else if(editingOffset == 3) {
                                destination.setOffsetZ(trim(destination.getOffsetZ() + 1));
                                if(destination.getOffsetZ() > 100) destination.setOffsetZ(0);
                            }

                            update();
                        }
                    }
                }
            }
        }.setOption(option));

        if(WarpSystem.getInstance().isOnBungeeCord()) {
            addButton(new SyncButton(2, 2) {
                @Override
                public ItemStack craftItem() {
                    String name = null;
                    if(destination.getType() == DestinationType.GlobalWarp) name = destination.getId();

                    List<String> lore = name == null ? null : new ArrayList<>();
                    if(lore != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return new ItemBuilder(XMaterial.ENDER_EYE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("GlobalWarps"))
                            .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name) + "§7'"),
                                    "", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore)
                            .getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) {
                        getLast().setClosingForGUI(true);
                        new GGlobalWarpList(player) {
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
                                getLast().open();
                            }

                            @Override
                            public void buildItemDescription(List<String> lore) {
                                lore.add("");
                                lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                            }
                        }.open();
                    } else if(e.isRightClick()) {
                        destination.setId(null);
                        destination.setAdapter(null);
                        destination.setType(null);

                        updateDestinationButtons();
                    }
                }
            }.setOption(option));

            addButton(new SyncAnvilGUIButton(3, 2, ClickType.LEFT) {
                @Override
                public ItemStack craftItem() {
                    String name = null;
                    if(destination.getType() == DestinationType.Server) name = destination.getId();

                    List<String> lore = name == null ? null : new ArrayList<>();
                    if(lore != null) {
                        lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                        lore.add("");
                        lore.add("§3" + Lang.get("Shift_Leftclick") + ": §b" + Lang.get("Refresh"));
                    }

                    List<String> onlineStatus = new ArrayList<>();

                    if(!Objects.equals(server, name)) {
                        server = name;
                        if(server != null) {
                            pinging = true;
                            WarpSystem.getInstance().getDataHandler().send(new RequestServerStatusPacket(server, new Callback<Boolean>() {
                                @Override
                                public void accept(Boolean online) {
                                    DestinationPage.this.pinging = false;
                                    DestinationPage.this.online = online;
                                    ((SyncButton) getButton(3, 2)).update();
                                }
                            }));
                        }
                    }

                    if(server != null) {
                        onlineStatus.add("§3" + Lang.get("Status") + ": " + (pinging ? "§7" + Lang.get("Pinging") + "..." : (online ? "§a" + Lang.get("Online") : "§c" + Lang.get("Offline"))));
                    }

                    ItemStack item = new ItemBuilder(XMaterial.ENDER_CHEST).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Server"))
                            .setLore("§3" + Lang.get("Current") + ": " + (name == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name) + "§7'"))
                            .addLore(onlineStatus)
                            .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (name == null ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore)
                            .getItem();

                    if(lore != null) lore.clear();
                    if(onlineStatus != null) onlineStatus.clear();
                    return item;
                }

                @Override
                public void onClick(AnvilClickEvent e) {
                    if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                    String input = e.getInput();

                    if(input == null) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                        return;
                    }

                    destination.setId(input);
                    destination.setType(DestinationType.Server);
                    destination.setAdapter(DestinationType.Server.getInstance());
                    updateDestinationButtons();
                    e.setClose(true);
                }

                @Override
                public void onClose(AnvilCloseEvent e) {
                }

                @Override
                public ItemStack craftAnvilItem(ClickType trigger) {
                    String name = null;
                    if(destination.getType() == DestinationType.Server) name = destination.getId();

                    return new ItemBuilder(XMaterial.PAPER).setName(name != null ? name : (Lang.get("Server") + "...")).getItem();
                }

                @Override
                public void onOtherClick(InventoryClickEvent e) {
                    if(e.isRightClick()) {
                        destination.setId(null);
                        destination.setAdapter(null);
                        destination.setType(null);

                        updateDestinationButtons();
                    } else if(e.isShiftClick() && e.isLeftClick()) {
                        if(server != null && !pinging) {
                            pinging = true;
                            update();
                            WarpSystem.getInstance().getDataHandler().send(new RequestServerStatusPacket(server, new Callback<Boolean>() {
                                @Override
                                public void accept(Boolean online) {
                                    DestinationPage.this.pinging = false;
                                    DestinationPage.this.online = online;
                                    update();
                                }
                            }));
                        }
                    }
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
        return ((double)(int) (d * 100)) / 100;
    }
}
