package de.codingair.warpsystem.spigot.features.effectportals.guis.editor;

import de.codingair.codingapi.particles.animations.movables.LocationMid;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.guis.AnimationList;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.effectportals.EffectPortalEditor;
import org.bukkit.entity.Player;

import java.util.List;

public class Menu extends HotbarGUI {
    private EffectPortalEditor editor;
    private Hologram hologram = new Hologram(getPlayer(), this);
    private Teleport teleport = new Teleport(getPlayer(), this);

    public Menu(Player player, EffectPortalEditor editor) {
        super(player, WarpSystem.getInstance(), 1);
        this.editor = editor;

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.hologram.initialize();
        this.teleport.initialize();
        initialize();
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.REDSTONE).setName("§7" + Lang.get("Animation") + ": §e" + (editor.getEffectPortal().getAnimation() == null ? "§c-" : editor.getEffectPortal().getAnimation().getName())).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    if(editor.getEffectPortal().getAnimation() == null) {
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

                                e.setClose(true);
                            }

                            @Override
                            public void onClose(AnvilCloseEvent e) {
                                if(!e.isSubmitted()) return;
                                e.setPost(() -> {
                                    setWaiting(true);
                                    editor.getEffectPortal().setShowAnimation(false);
                                    editor.getEffectPortal().setAnimation(new Animation(e.getSubmittedText()));
                                    updateDisplayName(ic, "§7" + Lang.get("Animation") + ": §e" + (editor.getEffectPortal().getAnimation() == null ? "§c-" : editor.getEffectPortal().getAnimation().getName()));
                                    player.teleport(editor.getEffectPortal().getLocation());
                                    new de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu(player, new LocationMid(editor.getEffectPortal().getLocation()), editor.getEffectPortal().getAnimation(), Menu.this, de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu.MenuParts.PARTICLES).open(false);
                                });
                            }
                        }, new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Name") + "...").getItem());
                    } else {
                        setWaiting(true);
                        editor.getEffectPortal().setShowAnimation(false);
                        player.teleport(editor.getEffectPortal().getLocation());
                        new de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu(player, new LocationMid(editor.getEffectPortal().getLocation()), editor.getEffectPortal().getAnimation(), Menu.this, de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu.MenuParts.PARTICLES).open(false);
                    }
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    if(editor.getEffectPortal().getAnimation() == null) {
                        new AnimationList(player) {
                            @Override
                            public void onClick(Animation value, org.bukkit.event.inventory.ClickType clickType) {
                                if(clickType == org.bukkit.event.inventory.ClickType.LEFT) {
                                    editor.getEffectPortal().setAnimation(value);
                                    updateDisplayName(ic, "§7" + Lang.get("Animation") + ": §e" + (editor.getEffectPortal().getAnimation() == null ? "§c-" : editor.getEffectPortal().getAnimation().getName()));
                                    onHover(Menu.this, ic, ic, player);
                                    player.closeInventory();
                                }
                            }

                            @Override
                            public void onClose() {
                            }

                            @Override
                            public void buildItemDescription(List<String> lore) {
                                lore.add("");
                                lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Choose"));
                            }
                        }.open();
                    } else {
                        editor.getEffectPortal().setAnimation(null);
                        updateDisplayName(ic, "§7" + Lang.get("Animation") + ": §e" + (editor.getEffectPortal().getAnimation() == null ? "§c-" : editor.getEffectPortal().getAnimation().getName()));
                        onHover(Menu.this, ic, ic, player);
                    }
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                if(editor.getEffectPortal().getAnimation() == null) {
                    MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.ACTION_BAR(
                            Lang.get("Animation"),
                            Lang.get("Create"),
                            Lang.get("Choose"))
                            , WarpSystem.getInstance(), Integer.MAX_VALUE);
                } else {
                    MessageAPI.sendActionBar(getPlayer(), EffectPortalEditor.ACTION_BAR(
                            Lang.get("Animation"),
                            Lang.get("Edit"),
                            Lang.get("Remove"))
                            , WarpSystem.getInstance(), Integer.MAX_VALUE);
                }
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.OAK_SIGN).setName("§7» §e" + Lang.get("Hologram") + "§7 «").getItem()).setLink(this.hologram));
        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.ENDER_EYE).setName("§7» §e" + Lang.get("Teleport") + "§7 «").getItem()).setLink(this.teleport));

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§7» §a" + Lang.get("Save") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(editor.getEffectPortal().getAnimation() == null) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("EffectPortal_No_Animation"));
                } else {
                    editor.finish();

                    if(editor.getBackupEffectPortal() == null) {
                        //CREATION
                        getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Created"));
                    } else {
                        //Save changes
                        getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Portal_Save_Changes"));
                    }

                    close(false);
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§7» §c" + Lang.get("Cancel") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                editor.exit();

                if(editor.getBackupEffectPortal() == null) {
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

    @Override
    public void open(boolean sound) {
        if(editor.getEffectPortal().getAnimation() != null && !AnimationManager.getInstance().existsAnimation(editor.getEffectPortal().getAnimation().getName())) {
            editor.getEffectPortal().setAnimation(null);
        }

        editor.getEffectPortal().setShowAnimation(true);

        if(editor.getEffectPortal().isRunning()) {
            editor.getEffectPortal().update();
            if(editor.getEffectPortal().getLink() != null) editor.getEffectPortal().getLink().update();
        } else editor.getEffectPortal().setRunning(true);
        super.open(sound);
        setStartSlot(-1);
    }

    @Override
    public void close(boolean sound) {
        super.close(sound);
    }

    public EffectPortalEditor getEditor() {
        return editor;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public Teleport getTeleport() {
        return teleport;
    }
}
