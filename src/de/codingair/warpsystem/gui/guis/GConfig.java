package de.codingair.warpsystem.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Particles.Animations.CircleAnimation;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.InterfaceListener;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButton;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.ItemButton.ItemButtonOption;
import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.Skull;
import de.CodingAir.v1_6.CodingAPI.Player.MessageAPI;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.CodingAir.v1_6.CodingAPI.Tools.ItemBuilder;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Category;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
        super(p, "§c§l§nWarps§r §7- §6" + Lang.get("Config", new Example("ENG", "Config"), new Example("GER", "Config")), 9, WarpSystem.getInstance(), false);

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

        ItemStack leaves = new ItemBuilder(Material.LEAVES).setName("§0").getItem();
        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.BLACK).setName("§0").getItem();

        setItem(1, leaves);
        setItem(4, glass);
        setItem(8, leaves);

        addButton(new ItemButton(0, new ItemBuilder(Skull.ArrowLeft).setName("§c" + Lang.get("Back", new Example("ENG", "Back"), new Example("GER", "Zurück"))).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                close = true;
                p.closeInventory();
                new GWarps(p, category, editMode).open();
            }
        }.setOption(option));

        ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance", new Example("ENG", "Maintenance"), new Example("GER", "Wartung")));

        if(!WarpSystem.maintenance)
            maintenance.setLore("", Lang.get("Leftclick_Enable", new Example("ENG", "&3Leftclick: &aEnable"), new Example("GER", "&3Linksklick: &aAktivieren")));
        else
            maintenance.setLore("", Lang.get("Leftclick_Disable", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")));

        addButton(new ItemButton(2, maintenance.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                WarpSystem.maintenance = !WarpSystem.maintenance;

                if(WarpSystem.maintenance) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Enabled", new Example("ENG", "&4The Maintenance-Mode was enabled."), new Example("GER", "&4Der Wartungs-Modus wurde aktiviert.")));
                } else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Maintenance_Disabled", new Example("ENG", "&4The Maintenance-Mode was disabled."), new Example("GER", "&4Der Wartungs-Modus wurde deaktiviert.")));
                }

                ItemBuilder maintenance = new ItemBuilder(Material.BEACON).setName("§c§l§n" + Lang.get("Maintenance", new Example("ENG", "Maintenance"), new Example("GER", "Wartung")));

                if(!WarpSystem.maintenance)
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_On", new Example("ENG", "&3Leftclick: &bEnable"), new Example("GER", "&3Linksklick: &aAktivieren")));
                else
                    maintenance.setLore("", Lang.get("Leftclick_Maintenance_Off", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")));

                setItem(maintenance.getItem());
            }
        }.setOption(option));

        ItemBuilder lang = new ItemBuilder(Material.BOOK).setName("§c§n" + Lang.get("Language", new Example("ENG", "Language"), new Example("GER", "Sprache")));
        lang.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + Lang.getCurrentLanguage());
        lang.addLore("", Lang.get("Leftclick_Prev_Lang", new Example("ENG", "&3Leftclick: &bPrevious language"), new Example("GER", "&3Linksklick: &bVorherige Sprache")));
        lang.addLore(Lang.get("Rightclick_Next_Lang", new Example("ENG", "&3Rightclick: &bNext language"), new Example("GER", "&3Rechtsklick: &bNächste Sprache")));

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

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Lang", new Example("ENG", "&aThe language was changed to '%lang%'."),
                        new Example("GER", "&aDie Sprache wurde auf '%lang%' geändert.")).replace("%lang%", Lang.getCurrentLanguage()));

                reinitialize();
                setTitle("§c§l§nWarps§r §7- §6" + Lang.get("Config", new Example("ENG", "Config"), new Example("GER", "Config")));
            }
        }.setOption(option).setOnlyLeftClick(false));

        ItemBuilder anim = new ItemBuilder(Material.GLOWSTONE_DUST).setName("§c§n" + Lang.get("Animation", new Example("ENG", "Animation"), new Example("GER", "Animation")));
        anim.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + WarpSystem.getInstance().getTeleportManager().getParticle().name());
        anim.addLore("", Lang.get("Shift_Leftclick_Show_Animation", new Example("ENG", "&3Shift-Leftclick: &bShow"), new Example("GER", "&3Shift-Linksklick: &bZeigen")));
        anim.addLore(Lang.get("Leftclick_Prev_Animation", new Example("ENG", "&3Leftclick: &bPrevious animation"), new Example("GER", "&3Linksklick: &bVorherige Animation")));
        anim.addLore(Lang.get("Rightclick_Next_Animation", new Example("ENG", "&3Rightclick: &bNext animation"), new Example("GER", "&3Rechtsklick: &bNächste Animation")));

        addButton(new ItemButton(5, anim.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(e.isLeftClick()) {

                    if(e.isShiftClick()) {
                        close = true;
                        p.closeInventory();

                        CircleAnimation circleAnimation = new CircleAnimation(WarpSystem.getInstance().getTeleportManager().getParticle(), p, WarpSystem.getInstance(), WarpSystem.getInstance().getTeleportManager().getRadius());
                        circleAnimation.setRunning(true);

                        MessageAPI.sendActionBar(p, Lang.get("Move_To_Open_Editor", new Example("ENG", "&cMove to open the §nconfig-editor§r"), new Example("GER", "&cBewegen um den §nEditor§c wieder zu öffnen")),
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
                        reinitialize();
                    }

                } else {
                    int id = WarpSystem.getInstance().getTeleportManager().getParticleId() + 1;
                    if(id >= WarpSystem.getInstance().getTeleportManager().getParticles().size()) id = 0;

                    WarpSystem.getInstance().getTeleportManager().setParticleId(id);
                    reinitialize();
                }

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Animation", new Example("ENG", "&aThe animation was changed to '%anim%'."),
                        new Example("GER", "&aDie Animation wurde auf '%anim%' geändert.")).replace("%anim%", WarpSystem.getInstance().getTeleportManager().getParticle().name()));
            }
        }.setOption(option).setOnlyLeftClick(false));

        ItemBuilder movingAllowed = new ItemBuilder(Material.LEATHER_BOOTS).setHideStandardLore(true).setName("§c§n" + Lang.get("Walking_During_Teleports", new Example("ENG", "Walking in teleports"), new Example("GER", "Laufen beim Teleport")));

        if(!WarpSystem.getInstance().getTeleportManager().isCanMove())
            movingAllowed.setLore("", Lang.get("Leftclick_Enable", new Example("ENG", "&3Leftclick: &aEnable"), new Example("GER", "&3Linksklick: &aAktivieren")));
        else
            movingAllowed.setLore("", Lang.get("Leftclick_Disable", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")));

        addButton(new ItemButton(6, movingAllowed.getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                WarpSystem.getInstance().getTeleportManager().setCanMove(!WarpSystem.getInstance().getTeleportManager().isCanMove());

                if(WarpSystem.getInstance().getTeleportManager().isCanMove()) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Walking_Enabled", new Example("ENG", "&7Walking during a teleport is &aallowed &7now."), new Example("GER", "&7Laufen beim Teleport ist nun &aerlaubt&7.")));
                } else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Walking_Disabled", new Example("ENG", "&7Walking during a teleport is &cdisallowed &7now."), new Example("GER", "&7Laufen beim Teleport ist nun &cnicht mehr erlaubt&7.")));
                }

                ItemBuilder movingAllowed = new ItemBuilder(Material.LEATHER_BOOTS).setHideStandardLore(true).setName("§c§n" + Lang.get("Walking_During_Teleports", new Example("ENG", "Walking in teleports"), new Example("GER", "Laufen beim Teleport")));

                if(!WarpSystem.getInstance().getTeleportManager().isCanMove())
                    movingAllowed.setLore("", Lang.get("Leftclick_Enable", new Example("ENG", "&3Leftclick: &aEnable"), new Example("GER", "&3Linksklick: &aAktivieren")));
                else
                    movingAllowed.setLore("", Lang.get("Leftclick_Disable", new Example("ENG", "&3Leftclick: &cDisable"), new Example("GER", "&3Linksklick: &cDeaktivieren")));

                setItem(movingAllowed.getItem());
            }
        }.setOption(option));

        ItemBuilder delay = new ItemBuilder(Material.WATCH).setName("§c§n" + Lang.get("Teleport_Delay", new Example("ENG", "Teleport delay"), new Example("GER", "Teleport-Zeit")));
        delay.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + WarpSystem.getInstance().getTeleportManager().getSeconds());
        delay.addLore("", Lang.get("Leftclick_Reduce", new Example("ENG", "&3Leftclick: &bReduce"), new Example("GER", "&3Linksklick: &bVerringern")));
        delay.addLore(Lang.get("Rightclick_Enlarge", new Example("ENG", "&3Rightclick: &bEnlarge"), new Example("GER", "&3Rechtsklick: &bVergrößern")));

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

                p.sendMessage(Lang.getPrefix() + Lang.get("Success_Changed_Teleport_Delay", new Example("ENG", "&aThe teleport delay was changed to '%delay%'."),
                        new Example("GER", "&aDie Teleport-Zeit wurde auf '%delay%' geändert.")).replace("%delay%", WarpSystem.getInstance().getTeleportManager().getSeconds() + ""));

                ItemBuilder delay = new ItemBuilder(Material.WATCH).setName("§c§n" + Lang.get("Teleport_Delay", new Example("ENG", "Teleport delay"), new Example("GER", "Teleport-Zeit")));
                delay.setLore("§8" + Lang.get("Current", new Example("ENG", "Current"), new Example("GER", "Aktuell")) + ": §7" + WarpSystem.getInstance().getTeleportManager().getSeconds());
                delay.addLore("", Lang.get("Leftclick_Reduce", new Example("ENG", "&3Leftclick: &bReduce"), new Example("GER", "&3Linksklick: &bVerringern")));
                delay.addLore(Lang.get("Rightclick_Enlarge", new Example("ENG", "&3Rightclick: &bEnlarge"), new Example("GER", "&3Rechtsklick: &bVergrößern")));
                setItem(delay.getItem());
            }
        }.setOption(option).setOnlyLeftClick(false));
    }
}
