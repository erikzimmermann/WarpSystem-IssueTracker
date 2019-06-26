package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.particles.animations.customanimations.CircleAnimation;
import de.codingair.codingapi.particles.animations.movables.PlayerMid;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.InterfaceListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class GConfig extends GUI {
    private Category category;

    private boolean editMode;
    private boolean close = false;

    public GConfig(Player p, Category category, boolean editMode) {
        super(p, "§c§l§nWarps§r §7- §6" + Lang.get("Config"), 9, WarpSystem.getInstance(), false);

        this.category = category;
        this.editMode = editMode;

        addListener(new InterfaceListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {
            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {
            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!close) {
                    Sound.ITEM_BREAK.playSound(p);
                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new GWarps(p, category, editMode).open(), 1L);
                    close = true;
                }
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {
            }
        });

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(Sound.CLICK.bukkitSound());
        option.setOnlyLeftClick(true);

        ItemStack leaves = new ItemBuilder(XMaterial.OAK_LEAVES).setName("§0").getItem();
        ItemStack glass = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("§0").getItem();

        setItem(1, leaves);
        setItem(4, glass);
        setItem(8, leaves);

        addButton(new ItemButton(0, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                close = true;
                p.closeInventory();
                new GWarps(p, category, editMode).open();
            }
        }.setOption(option));

        ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance"));

        if(!WarpSystem.maintenance)
            maintenance.setLore("", Lang.get("Leftclick_Enable"));
        else
            maintenance.setLore("", Lang.get("Leftclick_Disable"));

        addButton(new ItemButton(2, maintenance.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                WarpSystem.maintenance = !WarpSystem.maintenance;

                if(WarpSystem.maintenance) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Enabled"));
                } else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Disabled"));
                }

                ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance"));

                if(!WarpSystem.maintenance)
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_On"));
                else
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_Off"));

                setItem(maintenance.getItem());
            }
        }.setOption(option));

        ItemBuilder lang = new ItemBuilder(Material.BOOK).setName("§c§n" + Lang.get("Language"));
        lang.setLore("§8" + Lang.get("Current") + ": §7" + Lang.getCurrentLanguage());
        lang.addLore("", Lang.get("Leftclick_Prev_Lang"));
        lang.addLore(Lang.get("Rightclick_Next_Lang"));

        addButton(new ItemButton(3, lang.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(e.isLeftClick()) {
                    int prevId = Lang.getLanguageId(Lang.getCurrentLanguage()) - 1;
                    if(prevId < 0) prevId = Lang.getLanguages().size() - 1;

                    Lang.setCurrentLanguage(Lang.getLanguage(prevId));
                } else {
                    int nextId = Lang.getLanguageId(Lang.getCurrentLanguage()) + 1;
                    if(nextId >= Lang.getLanguages().size()) nextId = 0;

                    Lang.setCurrentLanguage(Lang.getLanguage(nextId));
                }

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Lang").replace("%lang%", Lang.getCurrentLanguage()));

                reinitialize();
                setTitle("§c§l§nWarps§r §7- §6" + Lang.get("Config"));
            }
        }.setOption(option).setOnlyLeftClick(false));

        ItemBuilder anim = new ItemBuilder(Material.GLOWSTONE_DUST).setName("§c§n" + Lang.get("Animation"));
        anim.setLore("§8" + Lang.get("Current") + ": §7" + WarpSystem.getInstance().getTeleportManager().getParticle().name());
        anim.addLore("", Lang.get("Shift_Leftclick_Show_Animation"));
        anim.addLore(Lang.get("Leftclick_Prev_Animation"));
        anim.addLore(Lang.get("Rightclick_Next_Animation"));

        addButton(new ItemButton(5, anim.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(e.isLeftClick()) {

                    if(e.isShiftClick()) {
                        close = true;
                        p.closeInventory();

                        CircleAnimation circleAnimation = new CircleAnimation(WarpSystem.getInstance().getTeleportManager().getParticle(), new PlayerMid(p), WarpSystem.getInstance().getTeleportManager().getRadius(), 1, 0);
                        circleAnimation.setRunning(true);

                        MessageAPI.sendActionBar(p, Lang.get("Move_To_Open_Editor"),
                                WarpSystem.getInstance(), Integer.MAX_VALUE);

                        Listener listener = new Listener() {
                            @EventHandler
                            public void onMove(PlayerMoveEvent e) {
                                if(!e.getPlayer().getName().equals(p.getName())) return;

                                double x = e.getFrom().getX() - e.getTo().getX();
                                double y = e.getFrom().getY() - e.getTo().getY();
                                double z = e.getFrom().getZ() - e.getTo().getZ();

                                if(x < 0) x *= -1;
                                if(y < 0) y *= -1;
                                if(z < 0) z *= -1;

                                double result = x + y + z;

                                if(result > 0.05) {
                                    HandlerList.unregisterAll(this);
                                    MessageAPI.sendActionBar(p, null, WarpSystem.getInstance(), 0);
                                    circleAnimation.setRunning(false);
                                    open();
                                    close = false;
                                }
                            }
                        };

                        Bukkit.getPluginManager().registerEvents(listener, WarpSystem.getInstance());
                        return;
                    } else {
                        int id = WarpSystem.getInstance().getTeleportManager().getParticleId() - 1;
                        if(id < 0) id = WarpSystem.getInstance().getTeleportManager().getParticles().size() - 1;

                        WarpSystem.getInstance().getTeleportManager().setParticleId(id);
                        WarpSystem.getInstance().getTeleportManager().save(false);
                        reinitialize();
                    }

                } else {
                    int id = WarpSystem.getInstance().getTeleportManager().getParticleId() + 1;
                    if(id >= WarpSystem.getInstance().getTeleportManager().getParticles().size()) id = 0;

                    WarpSystem.getInstance().getTeleportManager().setParticleId(id);
                    reinitialize();
                }

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Animation").replace("%anim%", WarpSystem.getInstance().getTeleportManager().getParticle().name()));
            }
        }.setOption(option).setOnlyLeftClick(false));

        ItemBuilder movingAllowed = new ItemBuilder(Material.LEATHER_BOOTS).setHideStandardLore(true).setName("§c§n" + Lang.get("Walking_During_Teleports"));

        if(!WarpSystem.getInstance().getTeleportManager().isCanMove())
            movingAllowed.setLore("", Lang.get("Leftclick_Enable"));
        else
            movingAllowed.setLore("", Lang.get("Leftclick_Disable"));

        addButton(new ItemButton(6, movingAllowed.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                WarpSystem.getInstance().getTeleportManager().setCanMove(!WarpSystem.getInstance().getTeleportManager().isCanMove());
                WarpSystem.getInstance().getTeleportManager().save(false);

                if(WarpSystem.getInstance().getTeleportManager().isCanMove()) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Walking_Enabled"));
                } else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Walking_Disabled"));
                }

                ItemBuilder movingAllowed = new ItemBuilder(Material.LEATHER_BOOTS).setHideStandardLore(true).setName("§c§n" + Lang.get("Walking_During_Teleports"));

                if(!WarpSystem.getInstance().getTeleportManager().isCanMove())
                    movingAllowed.setLore("", Lang.get("Leftclick_Enable"));
                else
                    movingAllowed.setLore("", Lang.get("Leftclick_Disable"));

                setItem(movingAllowed.getItem());
            }
        }.setOption(option));

        ItemBuilder delay = new ItemBuilder(XMaterial.CLOCK).setName("§c§n" + Lang.get("Teleport_Delay"));
        delay.setLore("§8" + Lang.get("Current") + ": §7" + WarpSystem.getInstance().getTeleportManager().getSeconds());
        delay.addLore("", Lang.get("Leftclick_Reduce"));
        delay.addLore(Lang.get("Rightclick_Enlarge"));

        addButton(new ItemButton(7, delay.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {

                if(e.isLeftClick()) {
                    int seconds = WarpSystem.getInstance().getTeleportManager().getSeconds() - 1;
                    if(seconds < 0) seconds = 0;

                    WarpSystem.getInstance().getTeleportManager().setSeconds(seconds);
                } else {
                    int seconds = WarpSystem.getInstance().getTeleportManager().getSeconds() + 1;

                    WarpSystem.getInstance().getTeleportManager().setSeconds(seconds);
                }

                WarpSystem.getInstance().getTeleportManager().save(false);

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Teleport_Delay").replace("%delay%", WarpSystem.getInstance().getTeleportManager().getSeconds() + ""));

                ItemBuilder delay = new ItemBuilder(XMaterial.CLOCK).setName("§c§n" + Lang.get("Teleport_Delay"));
                delay.setLore("§8" + Lang.get("Current") + ": §7" + WarpSystem.getInstance().getTeleportManager().getSeconds());
                delay.addLore("", Lang.get("Leftclick_Reduce"));
                delay.addLore(Lang.get("Rightclick_Enlarge"));
                setItem(delay.getItem());
            }
        }.setOption(option).setOnlyLeftClick(false));
    }
}
