package de.codingair.warpsystem.spigot.features.effectportals.guis.editor;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.components.SyncItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.effectportals.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import de.codingair.warpsystem.spigot.features.effectportals.utils.PortalDestinationAdapter;
import de.codingair.warpsystem.spigot.features.utils.guis.choosedestination.ChooseDestinationGUI;
import de.codingair.warpsystem.transfer.packets.spigot.RequestServerStatusPacket;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Teleport extends HotbarGUI {
    private Menu menu;

    public Teleport(Player player, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        String destination = null;
        if(getPortal().getDestination().getId() != null && getPortal().getDestination().getAdapter() != null) {
            if(getPortal().getDestination().getAdapter() instanceof PortalDestinationAdapter) {
                destination = Lang.get("Effect_Portal");
            } else {
                switch(getPortal().getDestination().getType()) {
                    case SimpleWarp:
                        destination = getPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                        break;

                    case GlobalWarp:
                        destination = getPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                        break;
                }
            }
        }

        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.ENDER_EYE).setName("§7" + Lang.get("Destination") + ": §e" + (destination == null ? "§c-" : destination)).getItem(), new ItemListener() {
            private boolean removed = false;

            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(API.getRemovable(player, AnvilGUI.class) != null) return;
                if(clickType.name().contains("LEFT")) {
                    //Set destination portal

                    if(getPortal().getDestination().getType() == DestinationType.EffectPortal) {
                        EffectPortal link = getPortal().getLink();
                        getPortal().setLink(null);
                        getPortal().getDestination().setId(null);
                        getPortal().getDestination().setAdapter(null);
                        getPortal().getDestination().setType(DestinationType.UNKNOWN);
                        link.destroy();

                        getPortal().update();
                        updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §c-");
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

                                    Location start = new Location(player.getLocation());
                                    start.trim(4);
                                    getPortal().getDestination().setId(start.toJSONString());
                                    getPortal().getDestination().setAdapter(new PortalDestinationAdapter());
                                    getPortal().getDestination().setType(DestinationType.EffectPortal);
                                    getPortal().setLink(new EffectPortal());

                                    getPortal().getLink().setUseLink(true);

                                    getPortal().getLink().setHoloStatus(getPortal().isHoloStatus());
                                    getPortal().getLink().setHoloPos(start.clone().add(0, getPortal().getRelHoloHeight(), 0));
                                    getPortal().getLink().setHoloText(input);

                                    getPortal().getLink().setName(input);
                                    getPortal().getLink().setLocation(start);
                                    getPortal().getLink().setRunning(true);

                                    Destination d = new Destination();
                                    d.setId(getPortal().getLocation().toJSONString(4));
                                    d.setAdapter(new PortalDestinationAdapter());
                                    d.setType(DestinationType.EffectPortal);

                                    getPortal().getLink().addAction(new WarpAction(d));

                                    menu.getHologram().update();

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
                    if(getPortal().getDestination().getType() != DestinationType.EffectPortal && getPortal().getDestination().getAdapter() != null) {
                        getPortal().getDestination().setId(null);
                        getPortal().getDestination().setAdapter(null);
                        getPortal().getDestination().setType(DestinationType.UNKNOWN);
                        getPortal().setLink(null);

                        getPortal().update();
                        removed = true;
                        updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §c-");
                        onHover(gui, ic, ic, player);
                    } else {
                        new ChooseDestinationGUI(player, new Callback<Destination>() {
                            @Override
                            public void accept(Destination destination) {
                                if(destination == null) return;
                                if(getPortal().getDestination().getType() == DestinationType.EffectPortal) {
                                    getPortal().getLink().destroy();
                                }

                                getPortal().getDestination().apply(destination);
                                getPortal().update();
                                onUnhover(gui, ic, ic, player);
                                onHover(gui, ic, ic, player);

                                String dest = null;
                                switch(getPortal().getDestination().getType()) {
                                    case SimpleWarp:
                                        dest = getPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                                        break;

                                    case GlobalWarp:
                                        dest = getPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                                        break;

                                    case Server:
                                        removed = false;
                                        dest = getPortal().getDestination().getId() + " §8(§b" + Lang.get("Server") + " §7- " + Lang.get("Pinging") + "...§8)";
                                        WarpSystem.getInstance().getDataHandler().send(new RequestServerStatusPacket(getPortal().getDestination().getId(), new Callback<Boolean>() {
                                            @Override
                                            public void accept(Boolean online) {
                                                if(removed) {
                                                    removed = false;
                                                    return;
                                                }

                                                String newName = getPortal().getDestination().getId() + " §8(§b" + Lang.get("Server") + " §7- " + (online ? "§a" + Lang.get("Online") : "§c" + Lang.get("Offline")) + "§8)";
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
                MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.ACTION_BAR(
                        Lang.get("Destination"),
                        getPortal().getDestination().getType() == DestinationType.EffectPortal ?
                                "§c" + Lang.get("Remove") :
                                Lang.get("Effect_Portal"),
                        getPortal().getDestination().getType() != DestinationType.EffectPortal && getPortal().getDestination().getAdapter() != null ?
                                "§c" + Lang.get("Remove") :
                                Lang.get("Existing_Warps"))
                        , WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK || clickType == ClickType.RIGHT_CLICK) {
                    AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            if(e.getSlot() == AnvilSlot.OUTPUT) {

                                if(e.getInput() == null) {
                                    player.sendMessage(Lang.getPrefix() + "§c" + Lang.get("Enter_Something"));
                                    return;
                                }

                                if(clickType == ClickType.LEFT_CLICK || !getPortal().hasDestinationPortal()) getPortal().setName(e.getInput());
                                else getPortal().getLink().setName(e.getInput());

                                onHover(gui, ic, ic, player);
                                e.setClose(true);
                            }
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                        }
                    }, new ItemBuilder(XMaterial.NAME_TAG).setName(clickType == ClickType.LEFT_CLICK || !getPortal().hasDestinationPortal() ? getPortal().getName() : getPortal().getLink().getName()).getItem());
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                if(getPortal().hasDestinationPortal())
                    MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.ACTION_BAR(Lang.get("Name"), "§7(§e" + getPortal().getName() + "§7) §e1. " + Lang.get("Effect_Portal"), "2. " + Lang.get("Effect_Portal") + " §7(§e" + getPortal().getLink().getName() + "§7)"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                else
                    MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NAME_TAG).setName("§7" + Lang.get("EffectPortal_ChangeName_Hint")).getItem();
            }
        });

        setItem(5, new ItemComponent(new ItemBuilder(XMaterial.REDSTONE)
                .setName("§7" + Lang.get("Permission") + ": '§e" + getPortal().getPermission() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            if(e.getSlot() == AnvilSlot.OUTPUT) {

                                if(e.getInput() == null) {
                                    player.sendMessage(Lang.getPrefix() + "§c" + Lang.get("Enter_Permission"));
                                    return;
                                }

                                getPortal().setPermission(e.getInput());
                                updateDisplayName(ic, "§7" + Lang.get("Permission") + ": '§r" + getPortal().getName() + "§7'");
                                e.setClose(true);
                            }
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                        }
                    }, new ItemBuilder(XMaterial.PAPER).setName(getPortal().getPermission() != null ? getPortal().getPermission() : Lang.get("Permission") + "...").getItem());
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getPortal().setPermission(null);
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.ACTION_BAR(Lang.get("Permission"), Lang.get("Set"), Lang.get("Remove")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.MUSIC_DISC_WAIT)
                .setName("§7" + Lang.get("Teleport_Sound") + ": '§e" + getTeleportSound().getSound().name() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Sound
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTeleportSound().setSound(previous(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    getTeleportSound().setSound(shiftPrevious(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTeleportSound().setSound(next(getTeleportSound().getSound()));
                } else if(clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    getTeleportSound().setSound(shiftNext(getTeleportSound().getSound()));
                }

                getTeleportSound().play(player);
                updateDisplayName(ic, "§7" + Lang.get("Teleport_Sound") + ": '§e" + getTeleportSound().getSound().name() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.PREVIOUS_NEXT_SHIFT(Lang.get("Teleport_Sound")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(8, new ItemComponent(new ItemBuilder(XMaterial.NOTE_BLOCK)
                .setName("§e" + getTeleportSound().getVolume() + " §7« " + Lang.get("Volume") + " §e| §7" + Lang.get("Pitch") + " » §e" + getTeleportSound().getPitch())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Volume
                if(clickType.equals(ClickType.LEFT_CLICK)) {
                    getTeleportSound().setVolume(round(getTeleportSound().getVolume() - 0.1F));
                    if(getTeleportSound().getVolume() < 0) getTeleportSound().setVolume(0);
                } else if(clickType.equals(ClickType.RIGHT_CLICK)) {
                    getTeleportSound().setVolume(round(getTeleportSound().getVolume() + 0.1F));
                    if(getTeleportSound().getVolume() > 1) getTeleportSound().setVolume(1);
                } else if(clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    getTeleportSound().setPitch(round(getTeleportSound().getPitch() - 0.1F));
                    if(getTeleportSound().getPitch() < 0) getTeleportSound().setPitch(0);
                } else if(clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    getTeleportSound().setPitch(round(getTeleportSound().getPitch() + 0.1F));
                    if(getTeleportSound().getPitch() > 1) getTeleportSound().setPitch(1);
                }

                getTeleportSound().play(player);
                updateDisplayName(ic, "§e" + getTeleportSound().getVolume() + " §7« " + Lang.get("Volume") + " §e| §7" + Lang.get("Pitch") + " » §e" + getTeleportSound().getPitch());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.MINUS_PLUS_SHIFT(Lang.get("Volume"), Lang.get("Pitch")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }

    private float round(float d) {
        return ((float) Math.round(d * 10)) / 10;
    }

    public static Sound next(Sound sound) {
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) return i + 1 == Sound.values().length ? Sound.values()[0] : Sound.values()[i + 1];
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    public static Sound shiftNext(Sound sound) {
        int id = -1;
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) {
                id = i;
            } else if(id >= 0 && sound.name().charAt(0) != Sound.values()[i].name().charAt(0)) {
                return Sound.values()[i];
            }
        }

        return Sound.values()[0];
    }

    public static Sound previous(Sound sound) {
        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].equals(sound)) {
                return i - 1 < 0 ? Sound.values()[Sound.values().length - 1] : Sound.values()[i - 1];
            }
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    public static Sound shiftPrevious(Sound sound) {
        int id = -1;

        for(int i = 0; i < Sound.values().length; i++) {
            if(Sound.values()[i].name().charAt(0) == sound.name().charAt(0)) {
                return id == -1 ? Sound.values()[Sound.values().length - 1] : Sound.values()[id];
            } else {
                id = i;
            }
        }

        throw new IllegalArgumentException("Couldn't found Sound with nanme=" + sound.name());
    }

    private SoundData getTeleportSound() {
        return getPortal().getTeleportSound();
    }

    private EffectPortal getPortal() {
        return menu.getEditor().getEffectPortal();
    }
}
