package de.codingair.warpsystem.spigot.features.effectportals.menu;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.effectportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.utils.PortalDestinationAdapter;
import de.codingair.warpsystem.spigot.features.utils.guis.choosedestination.ChooseDestinationGUI;
import de.codingair.warpsystem.transfer.packets.spigot.RequestServerStatusPacket;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Teleport extends HotbarGUI {
    private Menu menu;

    public Teleport(Player player, Menu menu) {
        super(player, WarpSystem.getInstance());
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    public void init() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STICK)
                .setName("§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getEffectPortal().getPermission() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType != ClickType.LEFT_CLICK) return;
                menu.getEditor().doAction(PortalEditor.Action.CHANGE_PERMISSION, () -> updateDisplayName(getItem(2), "§7" + Lang.get("Permission") + ": '§e" + menu.getEditor().getEffectPortal().getPermission() + "§7'"));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change_Permission"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
        
        String destination = null;
        if(menu.getEditor().getEffectPortal().getDestination().getId() != null && menu.getEditor().getEffectPortal().getDestination().getAdapter() != null) {
            if(menu.getEditor().getEffectPortal().getDestination().getAdapter() instanceof PortalDestinationAdapter) {
                destination = Lang.get("Effect_Portal");
            } else {
                switch(menu.getEditor().getEffectPortal().getDestination().getType()) {
                    case SimpleWarp:
                        destination = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                        break;

                    case GlobalWarp:
                        destination = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                        break;
                }
            }
        }

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.REDSTONE).setName("§7" + Lang.get("Destination") + ": §e" + (destination == null ? "§c-" : destination)).getItem(), new ItemListener() {
            private boolean removed = false;

            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(API.getRemovable(player, AnvilGUI.class) != null) return;
                if(clickType.name().contains("LEFT")) {
                    //Set destination portal

                    if(menu.getEditor().getEffectPortal().getDestination().getType() == DestinationType.EffectPortal) {
                        menu.getEditor().getEffectPortal().getDestination().setId(null);
                        menu.getEditor().getEffectPortal().getDestination().setAdapter(null);
                        menu.getEditor().getEffectPortal().getDestination().setType(DestinationType.UNKNOWN);
                        menu.getEditor().getEffectPortal().setDestinationName(null);

                        menu.getEditor().getEffectPortal().update();
                        updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §c-");
                        onUnhover(gui, ic, ic, player);
                        onHover(gui, ic, ic, player);
                    } else {
                        AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                            @Override
                            public void onClick(AnvilClickEvent e) {
                                e.setClose(false);
                                e.setCancelled(true);

                                if(e.getSlot() == AnvilSlot.OUTPUT) {
                                    if(e.getInput() == null) {
                                        player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                        return;
                                    }

                                    String input = e.getInput().replace(" ", "_");

                                    menu.getEditor().getEffectPortal().getDestination().setId(Location.getByLocation(player.getLocation()).toJSONString(4));
                                    menu.getEditor().getEffectPortal().getDestination().setAdapter(new PortalDestinationAdapter());
                                    menu.getEditor().getEffectPortal().getDestination().setType(DestinationType.EffectPortal);
                                    menu.getEditor().getEffectPortal().setDestinationName(input);

                                    menu.getEditor().getEffectPortal().update();
                                    onUnhover(gui, ic, ic, player);
                                    onHover(gui, ic, ic, player);

                                    removed = true;
                                    updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §e" + Lang.get("Effect_Portal"));

                                    e.setClose(true);
                                }
                            }

                            @Override
                            public void onClose(AnvilCloseEvent e) {
                            }
                        }, new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Name") + "...").getItem());
                    }
                } else if(clickType.name().contains("RIGHT")) {
                    if(menu.getEditor().getEffectPortal().getDestination().getType() != DestinationType.EffectPortal && menu.getEditor().getEffectPortal().getDestination().getAdapter() != null) {
                        menu.getEditor().getEffectPortal().getDestination().setId(null);
                        menu.getEditor().getEffectPortal().getDestination().setAdapter(null);
                        menu.getEditor().getEffectPortal().getDestination().setType(DestinationType.UNKNOWN);
                        menu.getEditor().getEffectPortal().setDestinationName(null);

                        menu.getEditor().getEffectPortal().update();
                        removed = true;
                        updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §c-");
                        onUnhover(gui, ic, ic, player);
                        onHover(gui, ic, ic, player);
                    } else {
                        new ChooseDestinationGUI(player, new Callback<Destination>() {
                            @Override
                            public void accept(Destination destination) {
                                if(destination == null) return;
                                menu.getEditor().getEffectPortal().getDestination().apply(destination);
                                menu.getEditor().getEffectPortal().update();
                                onUnhover(gui, ic, ic, player);
                                onHover(gui, ic, ic, player);

                                String dest = null;
                                switch(menu.getEditor().getEffectPortal().getDestination().getType()) {
                                    case SimpleWarp:
                                        dest = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                                        break;

                                    case GlobalWarp:
                                        dest = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                                        break;

                                    case Server:
                                        removed = false;
                                        dest = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("Server") + " §7- " + Lang.get("Pinging") + "...§8)";
                                        WarpSystem.getInstance().getDataHandler().send(new RequestServerStatusPacket(menu.getEditor().getEffectPortal().getDestination().getId(), new Callback<Boolean>() {
                                            @Override
                                            public void accept(Boolean online) {
                                                if(removed) {
                                                    removed = false;
                                                    return;
                                                }

                                                String newName = menu.getEditor().getEffectPortal().getDestination().getId() + " §8(§b" + Lang.get("Server") + " §7- " + (online ? "§a" + Lang.get("Online") : "§c" + Lang.get("Offline")) + "§8)";
                                                updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §e" + newName);
                                            }
                                        }));
                                        break;
                                }

                                updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §e" + (dest == null ? "§c-" : dest));
                            }
                        }).open();
                    }
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.ACTION_BAR(
                        Lang.get("Destination"),
                        menu.getEditor().getEffectPortal().getDestination().getType() == DestinationType.EffectPortal ?
                                "§c" + Lang.get("Remove") :
                                Lang.get("Effect_Portal"),
                        menu.getEditor().getEffectPortal().getDestination().getType() != DestinationType.EffectPortal && menu.getEditor().getEffectPortal().getDestination().getAdapter() != null ?
                                "§c" + Lang.get("Remove") :
                                Lang.get("Existing_Warps"))
                        , WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }
}
