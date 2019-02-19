package de.codingair.warpsystem.spigot.features.effectportals.menu;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.effectportals.PortalEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Animation extends HotbarGUI {
    private Menu menu;

    public Animation(Player player, Menu menu) {
        super(player, WarpSystem.getInstance());
        this.menu = menu;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));
    }

    public void init() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STRING)
                .setName("§7" + Lang.get("Teleport_Radius") + ": §e" + menu.getEditor().getPortal().getTeleportRadius())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Teleport-Radius
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.INCREASE_TELEPORT_RADIUS);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.DECREASE_TELEPORT_RADIUS);
                }

                updateDisplayName(getItem(2), "§7" + Lang.get("Teleport_Radius") + ": §e" + menu.getEditor().getPortal().getTeleportRadius());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.PLUS_MINUS(Lang.get("Animation_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(3, new ItemComponent(new ItemBuilder(Material.STICK)
                .setName("§7" + Lang.get("Animation_Height") + ": §e" + menu.getEditor().getPortal().getAnimationHeight())
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Animation-Height
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.INCREASE_ANIMATION_HEIGHT);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.DECREASE_ANIMATION_HEIGHT);
                }

                updateDisplayName(getItem(3), "§7" + Lang.get("Animation_Height") + ": §e" + menu.getEditor().getPortal().getAnimationHeight());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.PLUS_MINUS(Lang.get("Animation_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.BEACON)
                .setName("§7" + Lang.get("Animation_Type") + ": '§e" + menu.getEditor().getPortal().getAnimationType().name() + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Animation-Type
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_ANIMATION_TYPE);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.NEXT_ANIMATION_TYPE);
                }

                updateDisplayName(getItem(4), "§7" + Lang.get("Animation_Type") + ": '§e" + menu.getEditor().getPortal().getAnimationType().name() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.NEXT_PREVIOUS(Lang.get("Animation_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        String s = menu.getEditor().getPortal().getParticle().getName();
        s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());

        setItem(5, new ItemComponent(new ItemBuilder(Material.NETHER_STAR)
                .setName("§7" + Lang.get("Particle_Effect") + ": '§e" + s + "§7'")
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                //Particle-Effect
                if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.NEXT_PARTICLE);
                } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                    menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_PARTICLE);
                }

                String s_ = menu.getEditor().getPortal().getParticle().getName();
                s_ = s_.substring(0, 1).toUpperCase() + s_.substring(1, s_.length());
                updateDisplayName(getItem(5), "§7" + Lang.get("Particle_Effect") + ": '§e" + s_ + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), PortalEditor.NEXT_PREVIOUS(Lang.get("Animation_Height")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }
}
