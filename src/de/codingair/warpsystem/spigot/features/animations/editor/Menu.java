package de.codingair.warpsystem.spigot.features.animations.editor;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Menu extends HotbarGUI {
    private Animation animation;
    private Animation clone;
    private AnimationPlayer animPlayer;

    private Particles particles;
    private Buffs buffs;

    public static String PLUS_MINUS(String s) {
        return ACTION_BAR(s, "+", "-");
    }

    public static String NEXT_PREVIOUS(String s) {
        return ACTION_BAR(s, "«", "»");
    }

    public static String ACTION_BAR(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    public Menu(Player player, String name) {
        this(player, new Animation(name));
    }

    public Menu(Player player, Animation animation) {
        this(player, animation, animation.clone());
    }

    public Menu(Player player, Animation animation, Animation clone) {
        super(player, WarpSystem.getInstance());

        this.animation = animation;
        this.clone = clone;

        this.animPlayer = new AnimationPlayer(player, clone, 5);
        particles = new Particles(getPlayer(), this);
        buffs = new Buffs(getPlayer(), this);

        this.animPlayer.setLoop(true);
        this.animPlayer.setRunning(true);

        init(player);
    }

    private void init(Player p) {
        p.getInventory().setHeldItemSlot(1);

        setItem(0, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.NAME_TAG).setName("§7" + Lang.get("Name") + ": '§r" + clone.getName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        String input = e.getInput();

                        if(input == null) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        if(AnimationManager.getInstance().existsAnimation(input)) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                            return;
                        }

                        clone.setName(input);
                        updateDisplayName(ic, "§7" + Lang.get("Name") + ": '§r" + clone.getName() + "§7'");
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                    }
                }, new ItemBuilder(Material.PAPER).setName(clone.getName().replace("§", "&")).getItem());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change_Name"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(p);
            }
        }));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.BEACON).setName("§7» §e" + Lang.get("Particle_Effects") + "§7 «").getItem()).setLink(this.particles));
        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.SPLASH_POTION).setName("§7» §e" + Lang.get("Potion_Effects") + "§7 «").getItem()).setLink(this.buffs));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§7» §a" + Lang.get("Save") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
//                if(editor.getBackupPortal() == null) {
//                    //CREATION
//                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Created"));
//                } else {
//                    //Save changes
//                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Save_Changes"));
//                }
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
//                editor.exit();
//
//                if(editor.getBackupPortal() == null) {
//                    //NO CREATION
//                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Not_Created"));
//                } else {
//                    //Delete changes
//                    getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Delete_Changes"));
//                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }).setCloseOnClick(true));
    }

    public Animation getAnimation() {
        return animation;
    }

    public Animation getClone() {
        return clone;
    }

    public AnimationPlayer getAnimPlayer() {
        return animPlayer;
    }

    Particles getParticles() {
        return particles;
    }

    Buffs getBuffs() {
        return buffs;
    }
}
