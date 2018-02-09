package de.codingair.warpsystem.teleport.portals.menu;

import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.ItemBuilder;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.teleport.portals.PortalEditor;
import org.bukkit.DyeColor;
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
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back")).getItem()).setLink(this.menu), false);
        setItem(1, new ItemComponent(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setHideName(true).getItem()));

        setItem(2, new ItemComponent(new ItemBuilder(Material.STRING)
                .setName("§7" + Lang.get("Teleport_Radius", new Example("ENG", "Teleport-Radius"), new Example("GER", "Teleport-Radius")) + ": §e" + menu.getEditor().getPortal().getTeleportRadius())
                .getItem(), (gui, ic, player, clickType) -> {
            //Teleport-Radius
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.INCREASE_TELEPORT_RADIUS);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.DECREASE_TELEPORT_RADIUS);
            }

            updateDisplayName(getItem(2), "§7" + Lang.get("Teleport_Radius") + ": §e" + menu.getEditor().getPortal().getTeleportRadius());
        }));

        setItem(3, new ItemComponent(new ItemBuilder(Material.STICK)
                .setName("§7" + Lang.get("Animation_Height", new Example("ENG", "Animation-Height"), new Example("GER", "Animations-Höhe")) + ": §e" + menu.getEditor().getPortal().getAnimationHeight())
                .getItem(), (gui, ic, player, clickType) -> {
            //Animation-Height
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.INCREASE_ANIMATION_HEIGHT);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.DECREASE_ANIMATION_HEIGHT);
            }

            updateDisplayName(getItem(3), "§7" + Lang.get("Animation_Height") + ": §e" + menu.getEditor().getPortal().getAnimationHeight());
        }));

        setItem(4, new ItemComponent(new ItemBuilder(Material.BEACON)
                .setName("§7" + Lang.get("Animation_Type", new Example("ENG", "Animation-Type"), new Example("GER", "Animations-Typ")) + ": '§e" + menu.getEditor().getPortal().getAnimationType().name() + "§7'")
                .getItem(), (gui, ic, player, clickType) -> {
            //Animation-Type
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.NEXT_ANIMATION_TYPE);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_ANIMATION_TYPE);
            }

            updateDisplayName(getItem(4), "§7" + Lang.get("Animation_Type") + ": '§e" + menu.getEditor().getPortal().getAnimationType().name() + "§7'");
        }));

        String s = menu.getEditor().getPortal().getParticle().getName();
        s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());

        setItem(5, new ItemComponent(new ItemBuilder(Material.NETHER_STAR)
                .setName("§7" + Lang.get("Particle_Effect", new Example("ENG", "Particle-Effect"), new Example("GER", "Partikel-Effekt")) + ": '§e" + s + "§7'")
                .getItem(), (gui, ic, player, clickType) -> {
            //Particle-Effect
            if(clickType.equals(ClickType.LEFT_CLICK) || clickType.equals(ClickType.SHIFT_LEFT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.NEXT_PARTICLE);
            } else if(clickType.equals(ClickType.RIGHT_CLICK) || clickType.equals(ClickType.SHIFT_RIGHT_CLICK)) {
                menu.getEditor().doAction(PortalEditor.Action.PREVIOUS_PARTICLE);
            }

            String s_ = menu.getEditor().getPortal().getParticle().getName();
            s_ = s_.substring(0, 1).toUpperCase() + s_.substring(1, s_.length());
            updateDisplayName(getItem(5), "§7" + Lang.get("Particle_Effect") + ": '§e" + s_ + "§7'");
        }));
    }
}
