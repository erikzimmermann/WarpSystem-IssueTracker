package de.codingair.warpsystem.spigot.features.tempwarps.guis;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.EmptyTempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class GEditor extends SimpleGUI {

    public GEditor(Player p, TempWarp warp) {
        super(p, new StandardLayout(), new Page(p, Lang.get("TempWarp_Editor"), null) {
            @Override
            public void onExitByPlayer() {
                warp.restore();
            }

            @Override
            public void initialize(Player p) {
                if(warp instanceof EmptyTempWarp) throw new IllegalArgumentException("GEditor just can edit filled TempWarps!");
                warp.backup();
                
                ItemButtonOption option = new ItemButtonOption();
                option.setCloseOnClick(false);
                option.setClickSound(Sound.CLICK.bukkitSound());
                option.setOnlyLeftClick(true);

                addButton(new SyncButton(4) {
                    @Override
                    public ItemStack craftItem() {
                        boolean ready = warp.getName() != null && canPay(p, warp);

                        ItemBuilder builder = new ItemBuilder(ready ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);

                        builder.setName("§7" + Lang.get("Status") + ": " + (ready ? "§a" + Lang.get("Ready") : "§c" + Lang.get("Not_Ready")));

                        int costs = warp.getCosts() - warp.backupped().getCosts();
                        builder.addLore("", "§7" + Lang.get("Price") + ": " + (canPay(p, warp) ? "§a" : "§c") + costs + " " + Lang.get("Coins"));
                        if(ready) builder.addLore("", Lang.get("TempWarp_Click_Buy").replace("%PRICE%", costs + ""));

                        return builder.getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        boolean ready = warp.getName() != null && canPay(p, warp);

                        if(ready) {
                            //Save
                            close();
                            Sound.CLICK.playSound(p);

                            int costs = warp.getCosts() - warp.backupped().getCosts();
                            if(costs != 0) AdapterType.getActive().setMoney(p, AdapterType.getActive().getMoney(p) - warp.getCosts());
                            warp.apply();
                            
                            if(costs > 0) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Edited_Pay").replace("%TEMP_WARP%", warp.getName()).replace("%COINS%", costs + ""));
                            } else if(costs < 0) {
                                p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Edited_Refund").replace("%TEMP_WARP%", warp.getName()).replace("%COINS%", -costs + ""));
                            } else {
                                p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Edited").replace("%TEMP_WARP%", warp.getName()).replace("%COINS%", costs + ""));
                            }
                        } else Sound.CLICK.playSound(p, 1, 0.7F);
                    }
                }.setOption(option).setClickSound(null));

                addButton(new SyncAnvilGUIButton(2, 2) {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        String input = e.getInput();

                        if(input == null) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            Sound.CLICK.playSound(p, 1, 0.7F);
                            return;
                        }

                        if(input.contains("_")) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Correct_Name_Underline"));
                            Sound.CLICK.playSound(p, 1, 0.7F);
                            return;
                        }

                        if(TempWarpManager.getManager().getWarp(warp.isPublic() ? input : p.getName() + "." + input) != null) {
                            p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                            Sound.CLICK.playSound(p, 1, 0.7F);
                            return;
                        }

                        Sound.CLICK.playSound(p);
                        warp.setName(input);
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        updatePage();
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.NAME_TAG)
                                .setName("§7" + Lang.get("Name") + ": " + (warp.getName() == null ? "§c" + Lang.get("Not_Set") : "\"§b" + warp.getName() + "§7\"") + "")
                                .setLore("", Lang.get("Change_Name_Long"));

                        if(warp.getName() == null) {
                            builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            builder.setHideEnchantments(true);
                        }

                        if(TempWarpManager.getManager().getNameChangeCosts() > 0) {
                            builder.addLore(0, "§7" + Lang.get("Costs") + ": " + (canPay(p, warp) ? "§a" : "§c") + (warp.isChangingName() ? TempWarpManager.getManager().getNameChangeCosts() : 0) + " " + Lang.get("Coins"));
                        }

                        return builder.getItem();
                    }

                    @Override
                    public ItemStack craftAnvilItem() {
                        return new ItemBuilder(XMaterial.NAME_TAG).setName((warp.getName() == null ? Lang.get("Name") + "..." : warp.getName())).getItem();
                    }
                }.setOption(option));

                addButton(new SyncButton(3, 2) {
                    private int direction = 0;
                    private long last = 0;
                    private int increase = 1;

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(last == 0) last = new Date().getTime();

                        if(e.isLeftClick()) {
                            if(direction != 1) {
                                increase = 1;
                                direction = 1;
                            } else {
                                if(new Date().getTime() - last < 250L) increase += 2;
                                else increase = 1;

                                last = new Date().getTime();
                            }

                            warp.setDuration(warp.getDuration() - TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                            if(warp.getDuration() < TempWarpManager.getManager().getMinTime()) {
                                warp.setDuration(TempWarpManager.getManager().getMinTime());

                                Sound.CLICK.playSound(p, 1, 0.7F);
                                increase = 1;
                            } else Sound.CLICK.playSound(p);

                            updatePage();
                        } else if(e.isRightClick()) {
                            if(direction != 2) {
                                increase = 1;
                                direction = 2;
                            } else {
                                if(new Date().getTime() - last < 250L) increase += 2;
                                else increase = 1;

                                last = new Date().getTime();
                            }

                            warp.setDuration(warp.getDuration() + TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                            if(warp.getDuration() > TempWarpManager.getManager().getMaxTime()) {
                                warp.setDuration(TempWarpManager.getManager().getMaxTime());

                                Sound.CLICK.playSound(p, 1, 0.7F);
                                increase = 1;
                            } else Sound.CLICK.playSound(p);

                            updatePage();
                        }
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK).setName("§7" + Lang.get("Active_Time") + ": " + (warp.getDuration() <= 0 ? "§c" + Lang.get("Not_Set") : "§b" + TempWarpManager.getManager().convertInTimeFormat(warp.getDuration(), TempWarpManager.getManager().getConfig().getUnit())
                                + (warp.getDuration() == TempWarpManager.getManager().getMinTime() ? " §7(§c" + Lang.get("Minimum") + "§7)" : warp.getDuration() == TempWarpManager.getManager().getMaxTime() ? " §7(§c" + Lang.get("Maximum") + "§7)" : "")));

                        builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp) ? "§a" : "§c") + (warp.getDuration() * TempWarpManager.getManager().getConfig().getDurationCosts()) + " " + Lang.get("Coins"));
                        builder.addLore("");
                        if(warp.getDuration() > TempWarpManager.getManager().getMinTime()) builder.addLore("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                        if(warp.getDuration() < TempWarpManager.getManager().getMaxTime()) builder.addLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false).setClickSound(null));

                addButton(new SyncButton(4, 2) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        warp.setPublic(!warp.isPublic());

                        if(warp.getIdentifier() != null) {
                            TempWarp other = TempWarpManager.getManager().getWarp(warp.getIdentifier());
                            if(!other.equals(warp)) warp.setName(null);
                        }

                        updatePage();
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(warp.isPublic() ? XMaterial.BIRCH_DOOR : XMaterial.DARK_OAK_DOOR).setName("§7" + Lang.get("Status") + ": " +
                                (warp.isPublic() ?
                                        "§b" + Lang.get("Public") :
                                        "§c" + Lang.get("Private")
                                ));

                        builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp) ? "§a" : "§c") + (warp.isPublic() ? TempWarpManager.getManager().getConfig().getPublicCosts() : 0) + " " + Lang.get("Coins"));
                        builder.addLore("", Lang.get("Click_Toggle"));

                        return builder.getItem();
                    }
                }.setOption(option));

                addButton(new SyncAnvilGUIButton(5, 2) {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        String input = e.getInput();

                        if(input == null || input.toCharArray().length < TempWarpManager.getManager().getMinMessageCharLength() || input.toCharArray().length > TempWarpManager.getManager().getMaxMessageCharLength()) {
                            p.sendMessage(Lang.getPrefix() +
                                    Lang.get("TempWarp_Message_Too_Long_Too_Short")
                                            .replace("%MIN%", TempWarpManager.getManager().getMinMessageCharLength() + "")
                                            .replace("%MAX%", TempWarpManager.getManager().getMaxMessageCharLength() + "")
                            );

                            Sound.CLICK.playSound(p, 1, 0.7F);
                            return;
                        }

                        Sound.CLICK.playSound(p);

                        warp.setMessage(input);
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        updatePage();
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.PAPER);

                        builder.setText("§7" + Lang.get("Teleport_Message") + ": " + (warp.getMessage() == null ? "§c" + (warp.backupped().getMessage() == null ? Lang.get("Not_Set") : Lang.get("Removed")) : "\"§f" + ChatColor.translateAlternateColorCodes('&', warp.getMessage()) + "§7\""), 100);

                        builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp) ? "§a" : "§c") + (warp.getMessage() != null ? ((TempWarpManager.getManager().getMessageChangeCosts() > 0 && warp.isChangingMessage() ? TempWarpManager.getManager().getMessageChangeCosts() : 0) + TempWarpManager.getManager().getConfig().getMessageCosts()) : (warp.backupped().getMessage() == null ? 0 : TempWarpManager.getManager().getRefundByRemovingMessage())) + " " + Lang.get("Coins"));
                        builder.addLore("", Lang.get("Change_Message"));

                        return builder.getItem();
                    }

                    @Override
                    public ItemStack craftAnvilItem() {
                        return new ItemBuilder(XMaterial.PAPER)
                                .setName(warp.getMessage() == null ? (Lang.get("Message") + "...") : warp.getMessage())
                                .getItem();
                    }
                }.setOption(option));

                addButton(new SyncButton(6, 2) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(e.isLeftClick()) {
                            warp.setTeleportCosts(warp.getTeleportCosts() - TempWarpManager.getManager().getTeleportCostsSteps());
                            if(warp.getTeleportCosts() < 0) {
                                warp.setTeleportCosts(0);

                                Sound.CLICK.playSound(p, 1, 0.7F);
                            } else Sound.CLICK.playSound(p);

                            updatePage();
                        } else if(e.isRightClick()) {
                            warp.setTeleportCosts(warp.getTeleportCosts() + TempWarpManager.getManager().getTeleportCostsSteps());
                            if(warp.getTeleportCosts() > TempWarpManager.getManager().getMaxTeleportCosts()) {
                                warp.setTeleportCosts(TempWarpManager.getManager().getMaxTeleportCosts());

                                Sound.CLICK.playSound(p, 1, 0.7F);
                            } else Sound.CLICK.playSound(p);

                            updatePage();
                        }
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.GOLD_NUGGET).setName("§7" + Lang.get("Teleport_Costs") + ": §b" + warp.getTeleportCosts() + " " + Lang.get("Coins")
                                + (warp.getTeleportCosts() == 0 ? " §7(§c" + Lang.get("Minimum") + "§7)" : warp.getTeleportCosts() == TempWarpManager.getManager().getMaxTeleportCosts() ? " §7(§c" + Lang.get("Maximum") + "§7)" : ""));

                        builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp) ? "§a" : "§c") + (((
                                TempWarpManager.getManager().calculateTeleportCosts(warp.getTeleportCosts()) - TempWarpManager.getManager().calculateTeleportCosts(warp.backupped().getTeleportCosts())
                                ) + "").replace(".0", "")) + " " + Lang.get("Coins"));
                        builder.addLore("");
                        if(warp.getTeleportCosts() > 0) builder.addLore("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                        if(warp.getTeleportCosts() < TempWarpManager.getManager().getMaxTeleportCosts()) builder.addLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false));
            }
        }, WarpSystem.getInstance());
    }

    private static boolean canPay(Player player, TempWarp warp) {
        int costs = warp.getCosts() - warp.backupped().getCosts();
        return costs < 0 || AdapterType.getActive().getMoney(player) >= costs;
    }
}