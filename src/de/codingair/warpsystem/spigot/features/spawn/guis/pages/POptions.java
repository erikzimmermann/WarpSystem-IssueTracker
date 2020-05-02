package de.codingair.warpsystem.spigot.features.spawn.guis.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.api.chatinput.ChatInputEvent;
import de.codingair.warpsystem.spigot.api.chatinput.SyncChatInputGUIButton;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.spawn.guis.SpawnEditor;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class POptions extends PageItem {
    private Spawn clone;

    public POptions(Player p, Spawn clone) {
        super(p, SpawnEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);
        this.clone = clone;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new StandardButtonOption();

        addButton(new SyncButton(1, 2) {
            BukkitRunnable runnable = null;
            boolean reset = false;

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.REDSTONE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Spawn"));

                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getSpawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        builder.addLore(Editor.ITEM_SUB_TITLE_WARNING + Lang.get("Already_linked") + " §8(§7" + Lang.get("Server") + ": '" + server + "'§8)");
                        builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": " + (runnable != null ? "§4" + Lang.get("Reset") + " §7(§c" + ChatColor.stripColor(Lang.get("Confirm")) + "§7)" : "§7" + Lang.get("Reset")));
                        return builder.getItem();
                    }
                }


                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §a" + clone.getUsage().getName());

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7«");
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7»");

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getSpawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        if(runnable == null) {
                            runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    runnable = null;
                                    update();
                                }
                            };

                            runnable.runTaskLater(WarpSystem.getInstance(), 20);
                        } else {
                            //reset
                            reset = true;
                            SpawnManager.getInstance().updateGlobalOptions(null, SpawnManager.getInstance().getRespawnServer());
                        }

                        update();
                        return;
                    }
                }

                if(e.isLeftClick()) clone.setUsage(clone.getUsage().previous());
                else clone.setUsage(clone.getUsage().next());

                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getSpawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        return click == ClickType.RIGHT;
                    }
                }

                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            BukkitRunnable runnable = null;
            boolean reset = false;

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_EYE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Use_For_Respawn"));

                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getRespawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        builder.addLore(Editor.ITEM_SUB_TITLE_WARNING + Lang.get("Already_linked") + " §8(§7" + Lang.get("Server") + ": '" + server + "'§8)");
                        builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": " + (runnable != null ? "§4" + Lang.get("Reset") + " §7(§c" + ChatColor.stripColor(Lang.get("Confirm")) + "§7)" : "§7" + Lang.get("Reset")));
                        return builder.getItem();
                    }
                }

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §a" + clone.getRespawnUsage().getName());

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7«");
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7»");

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getRespawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        if(runnable == null) {
                            runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    runnable = null;
                                    update();
                                }
                            };

                            runnable.runTaskLater(WarpSystem.getInstance(), 20);
                        } else {
                            //reset
                            reset = true;
                            SpawnManager.getInstance().updateGlobalOptions(SpawnManager.getInstance().getSpawnServer(), null);
                        }

                        update();
                        return;
                    }
                }

                if(e.isLeftClick()) clone.setRespawnUsage(clone.getRespawnUsage().previous());
                else clone.setRespawnUsage(clone.getRespawnUsage().next());

                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String server = SpawnManager.getInstance().getRespawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        return click == ClickType.RIGHT;
                    }
                }

                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(3, 2, ClickType.LEFT) {
            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }

            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NAME_TAG)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Name"))
                        .setLore(Lang.get("Spawn_Name_Button_hint"))
                        .addLore("", "§3" + Lang.get("Current") + ": " + "§7'§r" + org.bukkit.ChatColor.translateAlternateColorCodes('&', clone.getDisplayName()) + "§7'",
                                "", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name"))
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(Material.PAPER).setName(clone.getDisplayName() == null ? Lang.get("Name") + "..." : clone.getDisplayName().replace("§", "&")).getItem();
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                    return;
                }

                e.setClose(true);
                clone.setDisplayName(input);
                update();
            }
        }.setOption(option));

        addButton(new SyncButton(4, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.FIREWORK_ROCKET).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Fireworks") + "§8 (§7" + Lang.get("First_Join") + "§8)");

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (clone.isRandomFireWorks() ? "§a" + Lang.get("Enabled") : "§c" + Lang.get("Disabled")));
                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                clone.setRandomFireWorks(!clone.isRandomFireWorks());
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new SyncChatInputGUIButton(5, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                List<String> lore = new ArrayList<>();
                for(String s : clone.getBroadCastMessages()) {
                    lore.addAll(TextAlignment.lineBreak("§7- '§r" + Spawn.prepareBroadcastMessage(s, p) + "§7'", 100));
                }

                return new ItemBuilder(XMaterial.PAPER)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Broadcaster") + "§8 (§7" + Lang.get("First_Join") + "§8)")
                        .setLore("§3" + Lang.get("Current") + ": " + (lore == null || lore.isEmpty() ? "§c" + Lang.get("Not_Set") : ""))
                        .addLore(lore)
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add_Line"))
                        .addLore(lore.isEmpty() ? null : "§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"))
                        .getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.isRightClick() && !clone.getBroadCastMessages().isEmpty()) {
                    clone.getBroadCastMessages().remove(clone.getBroadCastMessages().size() - 1);
                    update();
                }
            }

            @Override
            public void onEnter(ChatInputEvent e) {
                clone.getBroadCastMessages().add(e.getText());
                e.setClose(true);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || (!clone.getBroadCastMessages().isEmpty() && click == ClickType.RIGHT);
            }
        }.setOption(option));
    }
}
