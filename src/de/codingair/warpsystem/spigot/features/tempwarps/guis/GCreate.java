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
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.EmptyTempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.Key;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GCreate extends SimpleGUI {
    private TempWarp warp;

    private static Key getKeyWithLowerDuraton(Player player, int duration) {
        Key biggest = null;

        for(String k : TempWarpManager.getManager().getKeys(player)) {
            Key key = TempWarpManager.getManager().getTemplate(k);

            if(key.getTime() >= duration) continue;
            if(biggest == null || key.getTime() > biggest.getTime()) biggest = key;
        }

        return biggest;
    }

    private static Key getKeyWithHigherDuraton(Player player, int duration) {
        Key smallest = null;

        for(String k : TempWarpManager.getManager().getKeys(player)) {
            Key key = TempWarpManager.getManager().getTemplate(k);

            if(key.getTime() <= duration) continue;
            if(smallest == null || key.getTime() < smallest.getTime()) smallest = key;
        }

        return smallest;
    }

    public GCreate(Player p, TempWarp warp, Value<Key> key) {
        super(p, new StandardLayout(), new Page(p, Lang.get(!(warp instanceof EmptyTempWarp) && warp.isExpired() ? "TempWarp_Reactivate" : "TempWarp_Create"), null) {
            @Override
            public void initialize(Player p) {
                boolean expired = !(warp instanceof EmptyTempWarp) && warp.isExpired();
                ItemButtonOption option = new ItemButtonOption();
                option.setCloseOnClick(false);
                option.setClickSound(Sound.CLICK.bukkitSound());
                option.setOnlyLeftClick(true);

                addButton(new SyncButton(4) {
                    @Override
                    public ItemStack craftItem() {
                        int price = warp.getCosts();
                        boolean ready = warp.getName() != null && canPay(p, price);

                        ItemBuilder builder = new ItemBuilder(ready ? XMaterial.LIME_TERRACOTTA : XMaterial.RED_TERRACOTTA);

                        builder.setName("§7" + Lang.get("Status") + ": " + (ready ? "§a" + Lang.get("Ready") : "§c" + Lang.get("Not_Ready")));

                        if(price != 0) builder.addLore("", "§7" + Lang.get("Price") + ": " + (canPay(p, price) ? "§a" : "§c") + price + " " + Lang.get("Coins"));
                        if(ready) builder.addLore("", Lang.get(expired ? "TempWarp_Click_Renew" : "TempWarp_Click_Buy").replace("%PRICE%", price + ""));

                        return builder.getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        int price = warp.getCosts();
                        boolean ready = warp.getName() != null && canPay(p, price);

                        if(ready) {
                            //Save
                            p.closeInventory();
                            Sound.CLICK.playSound(p);

                            if(warp instanceof EmptyTempWarp) {
                                TempWarp finalWarp = ((EmptyTempWarp) warp).convert();
                                if(key != null) finalWarp.setCreatorKey(key.getValue().getName());
                                TempWarpManager.getManager().activate(finalWarp);
                                p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Created").replace("%NAME%", warp.getName()).replace("%PRICE%", price + ""));
                            } else {
                                warp.renew();
                                p.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Renew_Finished").replace("%TEMP_WARP%", warp.getName()).replace("%COINS%", price + ""));
                            }

                            MoneyAdapterType.getActive().withdraw(p, warp.getCosts());

                            if(key != null) {
                                TempWarpManager.getManager().getKeys(p).remove(key.getValue().getStrippedName());
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

                        if(TempWarpManager.getManager().isReserved(warp, warp.isPublic() ? input : p.getName() + "." + input)) {
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

                        return builder.getItem();
                    }

                    @Override
                    public ItemStack craftAnvilItem(ClickType trigger) {
                        return new ItemBuilder(XMaterial.NAME_TAG).setName((warp.getName() == null ? Lang.get("Name") + "..." : warp.getName())).getItem();
                    }
                }.setOption(option));

                addButton(new SyncButton(3, 2) {
                    private int direction = 0;
                    private long last = 0;
                    private double increase = 1;
                    private int clicks = 1;

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(e.isLeftClick()) {
                            if(key != null) {
                                Key k = getKeyWithLowerDuraton(player, key.getValue().getTime());
                                if(k == null) {
                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else {
                                    key.setValue(k);
                                    Sound.CLICK.playSound(p);
                                }
                            } else if(e.isShiftClick()) {
                                if(warp.getDuration() == TempWarpManager.getManager().getMinTime()) {
                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else {
                                    warp.setDuration(TempWarpManager.getManager().getMinTime());
                                    Sound.CLICK.playSound(p);
                                }

                                last = 0;
                            } else {
                                if(direction != 1) direction = 1;

                                if(new Date().getTime() - last < 750L) {
                                    if(clicks >= 4) increase = increaseMultiplier(warp.getDurationExact(), direction, increase, clicks);
                                    clicks++;
                                } else {
                                    increase = 1;
                                    clicks = 1;
                                }

                                last = new Date().getTime();

                                warp.setDuration(warp.getDurationExact() - TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                                if(warp.getDuration() < TempWarpManager.getManager().getMinTime()) {
                                    warp.setDuration(TempWarpManager.getManager().getMinTime());

                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                    last = 0;
                                } else Sound.CLICK.playSound(p);
                            }

                            updatePage();
                        } else if(e.isRightClick()) {
                            if(key != null) {
                                Key k = getKeyWithHigherDuraton(player, key.getValue().getTime());
                                if(k == null) {
                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else {
                                    key.setValue(k);
                                    Sound.CLICK.playSound(p);
                                }
                            } else if(e.isShiftClick()) {
                                if(warp.getDuration() == TempWarpManager.getManager().getMaxTime()) {
                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else {
                                    warp.setDuration(TempWarpManager.getManager().getMaxTime());
                                    Sound.CLICK.playSound(p);
                                }

                                last = 0;
                            } else {
                                if(direction != 2) direction = 2;

                                if(new Date().getTime() - last < 750L) {
                                    if(clicks >= 4) increase = increaseMultiplier(warp.getDurationExact(), direction, increase, clicks);
                                    clicks++;
                                } else {
                                    increase = 1;
                                    clicks = 1;
                                }

                                last = new Date().getTime();

                                warp.setDuration(warp.getDurationExact() + TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                                if(warp.getDuration() > TempWarpManager.getManager().getMaxTime()) {
                                    warp.setDuration(TempWarpManager.getManager().getMaxTime());

                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                    last = 0;
                                } else Sound.CLICK.playSound(p);
                            }

                            updatePage();
                        }
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK).setName("§7" + Lang.get("Active_Time") + ": " + (warp.getDuration() <= 0 ? "§c" + Lang.get("Not_Set") : "§b" + TempWarpManager.getManager().convertInTimeFormat(warp.getDuration(), TempWarpManager.getManager().getConfig().getUnit())
                                + (key != null ? " §7(" + Lang.get("Key") + ": §e" + key.getValue().getName() + "§7)" : (warp.getDuration() == TempWarpManager.getManager().getMinTime() ? " §7(§c" + Lang.get("Minimum") + "§7)" : warp.getDuration() == TempWarpManager.getManager().getMaxTime() ? " §7(§c" + Lang.get("Maximum") + "§7)" : ""))));

                        if(key == null)
                            builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp.getCosts()) ? "§a" : "§c") + (warp.getDuration() * TempWarpManager.getManager().getConfig().getDurationCosts()) + " " + Lang.get("Coins"));

                        if(key == null) builder.addLore("");

                        if((key != null && getKeyWithLowerDuraton(p, key.getValue().getTime()) != null) || (key == null && warp.getDuration() > TempWarpManager.getManager().getMinTime())) {
                            if(builder.getLore().isEmpty()) builder.addLore("");
                            builder.addLore("§3" + (key == null ? "(" + Lang.get("Shift") + ") " : "") + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                        }

                        if((key != null && getKeyWithHigherDuraton(p, key.getValue().getTime()) != null) || (key == null && warp.getDuration() < TempWarpManager.getManager().getMaxTime())) {
                            if(builder.getLore().isEmpty()) builder.addLore("");
                            builder.addLore("§3" + (key == null ? "(" + Lang.get("Shift") + ") " : "") + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));
                        }

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false).setClickSound(null));

                addButton(new SyncButton(4, 2) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(warp.getIdentifier() != null && TempWarpManager.getManager().isReserved(warp, warp.getIdentifier())) warp.setName(null);

                        warp.setPublic(!warp.isPublic());
                        updatePage();
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(warp.isPublic() ? XMaterial.BIRCH_DOOR : XMaterial.DARK_OAK_DOOR).setName("§7" + Lang.get("Status") + ": " +
                                (warp.isPublic() ?
                                        "§b" + Lang.get("Public") :
                                        "§e" + Lang.get("Private")
                                ));

                        if(warp.isPublic() && TempWarpManager.getManager().getConfig().getPublicCosts() > 0)
                            builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp.getCosts()) ? "§a" : "§c") + (warp.isPublic() ? TempWarpManager.getManager().getConfig().getPublicCosts() : 0) + " " + Lang.get("Coins"));
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

                        builder.setText("§7" + Lang.get("Teleport_Message") + ": " + (warp.getMessage() == null ? "§c" + Lang.get("Not_Set") : "\"§f" + ChatColor.translateAlternateColorCodes('&', warp.getMessage()) + "§7\""), 100);

                        if(warp.getMessage() != null && TempWarpManager.getManager().getConfig().getMessageCosts() > 0)
                            builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp.getCosts()) ? "§a" : "§c") + (warp.getMessage() != null ? TempWarpManager.getManager().getConfig().getMessageCosts() : 0) + " " + Lang.get("Coins"));
                        builder.addLore("", Lang.get("Change_Message"));

                        return builder.getItem();
                    }

                    @Override
                    public ItemStack craftAnvilItem(ClickType trigger) {
                        return new ItemBuilder(XMaterial.PAPER)
                                .setName(warp.getMessage() == null ? (Lang.get("Message") + "...") : warp.getMessage())
                                .getItem();
                    }
                }.setOption(option));

                addButton(new SyncButton(6, 2) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(e.isLeftClick()) {
                            if(e.isShiftClick()) {
                                if(warp.getTeleportCosts() > 0) {
                                    warp.setTeleportCosts(0);

                                    Sound.CLICK.playSound(p);
                                    updatePage();
                                } else Sound.CLICK.playSound(p, 1, 0.7F);
                            } else {
                                warp.setTeleportCosts(warp.getTeleportCosts() - TempWarpManager.getManager().getTeleportCostsSteps());
                                if(warp.getTeleportCosts() < 0) {
                                    warp.setTeleportCosts(0);

                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else Sound.CLICK.playSound(p);

                                updatePage();
                            }
                        } else if(e.isRightClick()) {
                            if(e.isShiftClick()) {
                                if(warp.getTeleportCosts() < TempWarpManager.getManager().getMaxTeleportCosts()) {
                                    warp.setTeleportCosts(TempWarpManager.getManager().getMaxTeleportCosts());

                                    Sound.CLICK.playSound(p);
                                    updatePage();
                                } else Sound.CLICK.playSound(p, 1, 0.7F);
                            } else {
                                warp.setTeleportCosts(warp.getTeleportCosts() + TempWarpManager.getManager().getTeleportCostsSteps());
                                if(warp.getTeleportCosts() > TempWarpManager.getManager().getMaxTeleportCosts()) {
                                    warp.setTeleportCosts(TempWarpManager.getManager().getMaxTeleportCosts());

                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else Sound.CLICK.playSound(p);

                                updatePage();
                            }
                        }
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(XMaterial.GOLD_NUGGET).setName("§7" + Lang.get("Teleport_Costs") + ": §b" + warp.getTeleportCosts() + " " + Lang.get("Coins")
                                + (warp.getTeleportCosts() == 0 ? " §7(§c" + Lang.get("Minimum") + "§7)" : warp.getTeleportCosts() == TempWarpManager.getManager().getMaxTeleportCosts() ? " §7(§c" + Lang.get("Maximum") + "§7)" : ""));

                        if(TempWarpManager.getManager().calculateTeleportCosts(warp.getTeleportCosts()) > 0)
                            builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp.getCosts()) ? "§a" : "§c") + ((TempWarpManager.getManager().calculateTeleportCosts(warp.getTeleportCosts()) + "").replace(".0", "")) + " " + Lang.get("Coins"));
                        builder.addLore("");
                        if(warp.getTeleportCosts() > 0) builder.addLore("§3(" + Lang.get("Shift") + ") " + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                        if(warp.getTeleportCosts() < TempWarpManager.getManager().getMaxTeleportCosts())
                            builder.addLore("§3(" + Lang.get("Shift") + ") " + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false).setClickSound(null));
            }
        }, WarpSystem.getInstance());

        this.warp = warp;
    }

    public static double increaseMultiplier(double warpDuration, int direction, double increase, int clicks) {
        TimeUnit unit = TempWarpManager.getManager().getConfig().getUnit();

        if(unit == TimeUnit.DAYS) {
            if(clicks == 4) return 2;
            else if(clicks == 8) return 4;
            else return increase;
        }

        int stepSize = TempWarpManager.getManager().getConfig().getDurationSteps();

        double res;

        if(unit == TimeUnit.MINUTES) {
            if(clicks == 4) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 60)) * 60) / 4) / stepSize) == 0 ? 60 / 4 / stepSize : res;
                else return ((60 - (warpDuration - ((int) (warpDuration / 60)) * 60)) / 4) / stepSize;
            } else if(clicks == 8) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 1440)) * 1440) / 4) / stepSize) == 0 ? 1440 / 4 / stepSize : res;
                else return ((1440 - (warpDuration - ((int) (warpDuration / 1440)) * 1440)) / 4) / stepSize;
            } else if(clicks == 12) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 1440)) * 1440)) / stepSize) == 0 ? 1440 / stepSize : res;
                else return (1440 - (warpDuration - ((int) (warpDuration / 1440)) * 1440)) / stepSize;
            } else if(clicks == 14) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 1440)) * 1440)) / stepSize * 2) == 0 ? 1440 / stepSize * 2 : res;
                else return (1440 - (warpDuration - ((int) (warpDuration / 1440)) * 1440)) / stepSize * 2;
            } else return increase;
        } else {
            if(clicks == 4) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 24)) * 24) / 4) / stepSize) == 0 ? 24 / 4 / stepSize : res;
                else return ((24 - (warpDuration - ((int) (warpDuration / 24)) * 24)) / 4) / stepSize;
            } else if(clicks == 8) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 24)) * 24)) / stepSize) == 0 ? 24 / stepSize : res;
                else return (24 - (warpDuration - ((int) (warpDuration / 24)) * 24)) / stepSize;
            } else if(clicks == 12) {
                if(direction == 1) return (res = ((warpDuration - ((int) (warpDuration / 24)) * 24)) / stepSize * 2) == 0 ? 24 / stepSize * 2 : res;
                else return (24 - (warpDuration - ((int) (warpDuration / 24)) * 24)) / stepSize * 2;
            } else return increase;
        }
    }

    @Override
    public void close(Player p, boolean isClosing) {
        super.close(p, isClosing);

        if(!isClosingForGUI()) TempWarpManager.getManager().getReserved().remove(this.warp);
    }

    private static boolean canPay(Player player, double costs) {
        return MoneyAdapterType.getActive().getMoney(player) >= costs;
    }
}
