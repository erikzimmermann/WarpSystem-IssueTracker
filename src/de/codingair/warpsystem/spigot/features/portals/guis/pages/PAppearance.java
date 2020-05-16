package de.codingair.warpsystem.spigot.features.portals.guis.pages;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.HologramEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.SpawnEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class PAppearance extends PageItem {
    private Portal clone;

    public PAppearance(Player p, Portal clone) {
        super(p, PortalEditor.getMainTitle(), new ItemBuilder(XMaterial.ITEM_FRAME).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Appearance")).getItem(), false);

        this.clone = clone;
        initialize(p);
    }

    public static Number cut(double n) {
        double d = ((double) (int) (n * 100)) / 100;
        if(d == (int) d) return (int) d;
        else return d;
    }

    @Override
    public boolean initialize(SimpleGUI gui) {
        boolean b = super.initialize(gui);
        updatePage();
        return b;
    }

    @Override
    public void initialize(Player p) {
        StandardButtonOption option = new StandardButtonOption();

        addButton(new NameButton(1, 2, false, new Value<>(clone.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                if(PortalManager.getInstance().existsPortal(name)) {
                    return Lang.getPrefix() + Lang.get("Name_Already_Exists");
                } else return null;
            }

            @Override
            public String onChange(String old, String name) {
                clone.setDisplayName(name);
                return name;
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.IRON_PICKAXE)
                        .setHideStandardLore(true)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Portals_Set_Blocks"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Portal_Blocks") + ": " + (canFinish() ? "§7" : "§c") + clone.getBlocks().size())
                        .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Portals_Set_Blocks"));

                if(!canFinish()) {
                    itemBuilder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    itemBuilder.setHideEnchantments(true);
                }

                return itemBuilder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                getLast().setClosingForGUI(true);
                getLast().close();

                PortalBlockEditor editor = new PortalBlockEditor(player, clone);
                editor.init();

                MessageAPI.sendActionBar(player, Lang.get("Drop_To_Leave"), WarpSystem.getInstance(), Integer.MAX_VALUE);

                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onDrop(PlayerDropItemEvent e) {
                        if(!e.getPlayer().getName().equals(player.getName())) return;

                        e.setCancelled(true);

                        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                            MessageAPI.stopSendingActionBar(player);
                            HandlerList.unregisterAll(this);

                            editor.end();

                            updatePage();
                            getLast().updateControllButtons();
                            getLast().open();
                        });
                    }
                }, WarpSystem.getInstance());
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new SyncButton(3, 2) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    getLast().setClosingForGUI(true);
                    close();
                    new HologramEditor(p, (PortalEditor) getLast(), clone.getHologram()).open(false);
                } else {
                    clone.getHologram().setVisible(!clone.getHologram().isVisible());
                    update();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || (click == ClickType.RIGHT && clone.getHologram().getLocation() != null);
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder b = new ItemBuilder(XMaterial.OAK_SIGN).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Hologram"));

                b.addLore("§7" + Lang.get("Line_break") + ": '§e\\n§7' §8- §7PlaceholderAPI support", "");
                b.addText(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (clone.getHologram().getText() == null ? "§c-" : "§7'§r" + ChatColor.translateAlternateColorCodes('&', clone.getHologram().getText()) + "§7'"), 100);
                b.addText(b.getLore().size() > 3 ? "" : null, Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Status") + ": §7" + (clone.getHologram().isVisible() ? "§a" + Lang.get("Enabled") : "§c" + Lang.get("Disabled")));

                b.addText("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Change"));
                if(clone.getHologram().getLocation() != null) b.addText(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Toggle"));

                return b.getItem();
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(4, 2, ClickType.LEFT) {
            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }

            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.PAPER)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Name"))
                        .setLore("§3" + Lang.get("Current") + ": " + (clone.getTeleportName() == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + org.bukkit.ChatColor.translateAlternateColorCodes('&', clone.getTeleportName()) + "§7'"),
                                "", (clone.getTeleportName() == null ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set_Name") : "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name")),
                                (clone.getTeleportName() == null ? null : "§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove")))
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(Material.PAPER).setName(clone.getTeleportName() == null ? Lang.get("Name") + "..." : clone.getTeleportName().replace("§", "&")).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    clone.setTeleportName(null);
                    update();
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

                e.setClose(true);
                clone.setTeleportName(input);
                update();
            }
        }.setOption(option));

        addButton(new SyncButton(5, 2) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    getLast().setClosingForGUI(true);
                    close();
                    if(clone.getSpawn() != null) player.teleport(clone.getSpawn(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    new SpawnEditor(p, (PortalEditor) getLast(), clone).open(false);
                } else {
                    clone.setSpawn(null);
                    update();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder b = new ItemBuilder(XMaterial.ENDER_EYE).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Spawn_Position"));

                b.addText(Lang.get("Spawn_Position_Info"), 100);

                String pos = clone.getSpawn() == null ? "§c-" : "x=" + cut(clone.getSpawn().getX()) + ", y=" + cut(clone.getSpawn().getY()) + ", z=" + cut(clone.getSpawn().getZ());

                b.addText("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7" + pos);
                b.addText("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + (clone.getSpawn() == null ? "§a" + Lang.get("Set") : "§7" + Lang.get("Change")));
                if(clone.getSpawn() != null) b.addText(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return b.getItem();
            }
        }.setOption(option));
    }

    public boolean canFinish() {
        return getLast() == null || getLast().canFinish();
    }
}
