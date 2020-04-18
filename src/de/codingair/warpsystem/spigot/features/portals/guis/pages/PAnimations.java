package de.codingair.warpsystem.spigot.features.portals.guis.pages;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.customanimations.AnimationType;
import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.animationseditor.AnimationHotBarEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.Animation;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PAnimations extends PageItem {
    private Portal clone;

    public PAnimations(Player p, Portal clone) {
        super(p, PortalEditor.getMainTitle(), new ItemBuilder(XMaterial.BLAZE_POWDER).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Animations")).getItem(), false);

        this.clone = clone;
        initialize(p);
    }

    @Override
    public boolean initialize(SimpleGUI gui) {
        boolean b = super.initialize(gui);
        updatePage();
        return b;
    }

    @Override
    public void initialize(Player p) {
        getButtons().clear();
        StandardButtonOption option = new StandardButtonOption();
        int slot = 1;

        for(Animation animation : clone.getAnimations()) {
            addButton(new SyncButton(slot++, 2) {
                private BukkitRunnable runnable = null;

                @Override
                public ItemStack craftItem() {
                    return new ItemBuilder(XMaterial.NETHER_STAR)
                            .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Animation") + ":§7 " + animation.getEffect().getAnimation().getDisplayName())
                            .addText(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Particle_Effect") + ": §7" + animation.getEffect().getParticle().name())
                            .addText("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Edit"))
                            .addText(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + (runnable != null ? "§4" : "§7") + ChatColor.stripColor(Lang.get("Delete")) + (runnable != null ? " §7(§c" + ChatColor.stripColor(Lang.get("Confirm")) + "§7)" : ""))
                            .getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) {
                        //edit
                        getLast().setClosingForGUI(true);
                        getLast().close();

                        new AnimationHotBarEditor(player, (PortalEditor) getLast(), animation).open(false);
                    } else if(e.isRightClick()) {
                        //delete
                        if(runnable != null) {
                            //delete
                            animation.setVisible(false);
                            clone.getAnimations().remove(animation);

                            runnable.cancel();
                            runnable = null;

                            getLast().updatePage();
                        } else {
                            runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    runnable = null;
                                    update();
                                }
                            };

                            runnable.runTaskLater(WarpSystem.getInstance(), 20);
                            update();
                        }
                    }
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT || click == ClickType.RIGHT;
                }
            }.setOption(option));
        }

        if(slot < 7) {
            addButton(new SyncButton(slot, 2) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder b = new ItemBuilder(XMaterial.BARRIER)
                            .setName(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Add"));

                    if(!canFinish()) {
                        b.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        b.setHideEnchantments(true);
                    }

                    return b.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    getLast().setClosingForGUI(true);
                    getLast().close();

                    Animation anim = new Animation(new ParticlePart(AnimationType.CIRCLE, Particle.FLAME, 1, 1, CustomAnimation.MAX_SPEED), new Location(player.getLocation()));

                    clone.getAnimations().add(anim);
                    getLast().updateControllButtons();
                    new AnimationHotBarEditor(player, (PortalEditor) getLast(), anim).open(false);
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT;
                }
            }.setOption(option));
        }
    }

    public boolean canFinish() {
        return getLast() == null || getLast().canFinish();
    }
}
