package de.codingair.warpsystem.spigot.features.effectportals.guis.editor;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.effectportals.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Hologram extends HotbarGUI {
    private Menu menu;

    public Hologram(Player player, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    private double round(double d) {
        return ((double) (int) Math.round(d * 10)) / 10;
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK).setName("§7" + Lang.get("Hologram_Height") + ": §e" + round(getPortal().getRelHoloHeight())).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Hologram-Height
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    getPortal().getHoloPos().setY(round(getPortal().getHoloPos().getY() - 0.1));
                    if(getPortal().getLink() != null) getPortal().getLink().getHoloPos().setY(getPortal().getLink().getHoloPos().getY() - 0.1);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    getPortal().getHoloPos().setY(round(getPortal().getHoloPos().getY() + 0.1));
                    if(getPortal().getLink() != null) getPortal().getLink().getHoloPos().setY(getPortal().getLink().getHoloPos().getY() + 0.1);
                }

                getPortal().updateHolograms();
                if(getPortal().getLink() != null) getPortal().getLink().updateHolograms();

                updateDisplayName(getItem(2), "§7" + Lang.get("Hologram_Height") + ": §e" + round(getPortal().getRelHoloHeight()));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.MINUS_PLUS(Lang.get("Hologram_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7" + Lang.get("Hologram_Text") + ": '§r" + ChatColor.translateAlternateColorCodes('&', getPortal().getHoloText()) + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType != ClickType.LEFT_CLICK) return;
                //Start-Name

                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        if(e.getSlot() == AnvilSlot.OUTPUT) {

                            if(e.getInput() == null) {
                                player.sendMessage(Lang.getPrefix() + "§c" + Lang.get("Enter_Something"));
                                return;
                            }

                            getPortal().setHoloText(e.getInput());
                            updateDisplayName(ic, "§7" + Lang.get("Hologram_Text") + ": '§r" + ChatColor.translateAlternateColorCodes('&', getPortal().getHoloText()) + "§7'");
                            e.setClose(true);
                        }
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                    }
                }, new ItemBuilder(XMaterial.PAPER).setName(getPortal().getHoloText() != null ? getPortal().getHoloText() : Lang.get("Hologram") + "...").getItem());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        ItemBuilder builder = new ItemBuilder(getPortal().isHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
        builder.setName(ChatColor.GRAY + Lang.get("Status") + ": " +
                (getPortal().isHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                        ChatColor.RED + Lang.get("Disabled")));

        setItem(5, new ItemComponent(builder.getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType != ClickType.LEFT_CLICK) return;
                getPortal().setHoloStatus(!getPortal().isHoloStatus());
                getPortal().updateHolograms();

                ItemBuilder builder = new ItemBuilder(getPortal().isHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
                builder.setName(ChatColor.GRAY + Lang.get("Status") + ": " +
                        (getPortal().isHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                                ChatColor.RED + Lang.get("Disabled")));

                setItem(5, new ItemComponent(builder.getItem(), this), false);
                updateSingle(5);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Toggle"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        if(getPortal().hasDestinationPortal()) {
            setItem(6, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

            setItem(7, new ItemComponent(new ItemBuilder(Material.NAME_TAG).setName("§7" + Lang.get("Hologram") + " §8(§e" + Lang.get("Destination") + "§8)" + ": '§r" + ChatColor.translateAlternateColorCodes('&', getPortal().getLink().getHoloText()) + "§7'").getItem(), new ItemListener() {
                @Override
                public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                    if(clickType != ClickType.LEFT_CLICK) return;
                    //Goal-Name

                    AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            if(e.getSlot() == AnvilSlot.OUTPUT) {

                                if(e.getInput() == null) {
                                    player.sendMessage(Lang.getPrefix() + "§c" + Lang.get("Enter_Something"));
                                    return;
                                }

                                getPortal().getLink().setHoloText(e.getInput());
                                updateDisplayName(ic, "§7" + Lang.get("Hologram") + " §8(§e" + Lang.get("Destination") + "§8)" + ": '§r" + ChatColor.translateAlternateColorCodes('&', getPortal().getLink().getHoloText()) + "§7'");
                                e.setClose(true);
                            }
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                        }
                    }, new ItemBuilder(XMaterial.PAPER).setName(getPortal().getLink().getHoloText() != null ? getPortal().getLink().getHoloText() : Lang.get("Hologram") + "...").getItem());
                }

                @Override
                public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                    MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                }

                @Override
                public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                    MessageAPI.stopSendingActionBar(getPlayer());
                }
            }));

            builder = new ItemBuilder(getPortal().getLink().isHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
            builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination") + ": " +
                    (getPortal().getLink().isHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                            ChatColor.RED + Lang.get("Disabled")));

            setItem(8, new ItemComponent(builder.getItem(), new ItemListener() {
                @Override
                public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                    getPortal().getLink().setHoloStatus(!getPortal().getLink().isHoloStatus());
                    getPortal().getLink().updateHolograms();

                    ItemBuilder builder = new ItemBuilder(getPortal().getLink().isHoloStatus() ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);
                    builder.setName(ChatColor.GRAY + Lang.get("Status_Of_Destination") + ": " +
                            (getPortal().getLink().isHoloStatus() ? ChatColor.GREEN + Lang.get("Enabled") :
                                    ChatColor.RED + Lang.get("Disabled")));

                    setItem(8, new ItemComponent(builder.getItem(), this), false);
                    updateSingle(8);
                }

                @Override
                public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                    MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Toggle"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                }

                @Override
                public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                    MessageAPI.stopSendingActionBar(getPlayer());
                }
            }));
        }
    }

    private EffectPortal getPortal() {
        return menu.getEditor().getEffectPortal();
    }
}
