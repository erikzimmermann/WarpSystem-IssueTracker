package de.codingair.warpsystem.spigot.features.effectportals.menu;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Menu extends HotbarGUI {
    private PortalEditor editor;
    private Animation animation = new Animation(getPlayer(), this);
    private Hologram hologram = new Hologram(getPlayer(), this);
    private Teleport teleport = new Teleport(getPlayer(), this);

    public Menu(Player player, PortalEditor editor) {
        super(player, WarpSystem.getInstance());
        this.editor = editor;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.animation.init();
        this.hologram.init();
        this.teleport.init();
        init();
    }

    private void init() {
        setItem(0, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(1, new ItemComponent(new ItemBuilder(Material.REDSTONE).setName("§7» §e" + Lang.get("Animation") + "§7 «").getItem()).setLink(this.animation));
        setItem(2, new ItemComponent(new ItemBuilder(Material.SIGN).setName("§7» §e" + Lang.get("Hologram") + "§7 «").getItem()).setLink(this.hologram));
        setItem(3, new ItemComponent(new ItemBuilder(Material.ENDER_PEARL).setName("§7» §e" + Lang.get("Teleport") + "§7 «").getItem()).setLink(this.teleport));

        String destination = null;
        if(editor.getPortal().getDestination().getId() != null && editor.getPortal().getDestination().getAdapter() != null) {
            if(editor.getPortal().getDestination().getAdapter() instanceof PortalDestinationAdapter) {
                destination = Lang.get("Effect_Portal");
            } else {
                switch(editor.getPortal().getDestination().getType()) {
                    case WarpIcon:
                        destination = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("Warp") + "§8)";
                        break;

                    case SimpleWarp:
                        destination = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                        break;

                    case GlobalWarp:
                    case GlobalWarpIcon:
                        destination = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                        break;
                }
            }
        }

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.REDSTONE).setName("§7" + Lang.get("Destination") + ": §e" + (destination == null ? "§c-" : destination)).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType.name().contains("LEFT")) {
                    //Set destination portal

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

                                editor.getPortal().getDestination().setId(Location.getByLocation(player.getLocation()).toJSONString(4));
                                editor.getPortal().getDestination().setAdapter(new PortalDestinationAdapter());
                                editor.getPortal().getDestination().setType(DestinationType.EffectPortal);
                                editor.getPortal().setDestinationName(input);

                                editor.getPortal().update();

                                updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §e" + Lang.get("Effect_Portal"));

                                e.setClose(true);
                            }
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                        }
                    }, new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Name") + "...").getItem());
                } else if(clickType.name().contains("RIGHT")) {
                    new ChooseDestinationGUI(player, Lang.get("WarpSign_Choose_Warp_GUI"), new Callback<Destination>() {
                        @Override
                        public void accept(Destination destination) {
                            if(destination == null) return;
                            editor.getPortal().getDestination().apply(destination);
                            editor.getPortal().update();

                            String dest = null;
                            switch(editor.getPortal().getDestination().getType()) {
                                case WarpIcon:
                                    dest = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("Warp") + "§8)";
                                    break;

                                case SimpleWarp:
                                    dest = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("SimpleWarp") + "§8)";
                                    break;

                                case GlobalWarp:
                                case GlobalWarpIcon:
                                    dest = editor.getPortal().getDestination().getId() + " §8(§b" + Lang.get("GlobalWarp") + "§8)";
                                    break;
                            }

                            updateDisplayName(ic, "§7" + Lang.get("Destination") + ": §e" + (dest == null ? "§c-" : dest));
                        }
                    }).open();
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(),
                        PortalEditor.ACTION_BAR(Lang.get("Destination"), Lang.get("Effect_Portal"), Lang.get("Existing_Warps"))
                        , WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§7» §a" + Lang.get("Save") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                editor.finish();

                if(editor.getBackupPortal() == null) {
                    //CREATION
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Created"));
                } else {
                    //Save changes
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Save_Changes"));
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }).setCloseOnClick(true));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§7» §c" + Lang.get("Cancel") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                editor.exit();

                if(editor.getBackupPortal() == null) {
                    //NO CREATION
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Not_Created"));
                } else {
                    //Delete changes
                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Delete_Changes"));
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }).setCloseOnClick(true));
    }

    public PortalEditor getEditor() {
        return editor;
    }
}
