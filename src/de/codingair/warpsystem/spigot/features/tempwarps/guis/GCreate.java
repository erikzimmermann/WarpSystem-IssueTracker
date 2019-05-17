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
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.EmptyTempWarp;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.Key;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

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

    public GCreate(Player p, TempWarp warp) {
        this(p, warp, null);
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

                            AdapterType.getActive().setMoney(p, AdapterType.getActive().getMoney(p) - warp.getCosts());

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

                        if(TempWarpManager.getManager().isReserved(warp.isPublic() ? input : p.getName() + "." + input)) {
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
                        if(key == null && last == 0) last = new Date().getTime();

                        if(e.isLeftClick()) {
                            if(key != null) {
                                Key k = getKeyWithLowerDuraton(player, key.getValue().getTime());
                                if(k == null) {
                                    Sound.CLICK.playSound(p, 1, 0.7F);
                                } else {
                                    key.setValue(k);
                                    Sound.CLICK.playSound(p);
                                }
                            } else {
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
                            } else {
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

                        if(key == null || getKeyWithLowerDuraton(p, key.getValue().getTime()) != null) {
                            if(warp.getDuration() > TempWarpManager.getManager().getMinTime()) {
                                if(builder.getLore().isEmpty()) builder.addLore("");
                                builder.addLore("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                            }
                        }

                        if(key == null || getKeyWithHigherDuraton(p, key.getValue().getTime()) != null) {
                            if(warp.getDuration() < TempWarpManager.getManager().getMaxTime()) {
                                if(builder.getLore().isEmpty()) builder.addLore("");
                                builder.addLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));
                            }
                        }

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false).setClickSound(null));

                addButton(new SyncButton(4, 2) {
                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        if(warp.getIdentifier() != null) {
                            if(TempWarpManager.getManager().isReserved(!warp.isPublic() ? warp.getName() : warp.getLastKnownName() + warp.getName())) warp.setName(null);

                            warp.setPublic(!warp.isPublic());
                            updatePage();
                        } else {
                            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarps_Toggle_Without_Name"));
                        }
                    }

                    @Override
                    public ItemStack craftItem() {
                        ItemBuilder builder = new ItemBuilder(warp.isPublic() ? XMaterial.BIRCH_DOOR : XMaterial.DARK_OAK_DOOR).setName("§7" + Lang.get("Status") + ": " +
                                (warp.isPublic() ?
                                        "§b" + Lang.get("Public") :
                                        "§c" + Lang.get("Private")
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

                        if(TempWarpManager.getManager().calculateTeleportCosts(warp.getTeleportCosts()) > 0)
                            builder.addLore("§7" + Lang.get("Costs") + ": " + (canPay(p, warp.getCosts()) ? "§a" : "§c") + ((TempWarpManager.getManager().calculateTeleportCosts(warp.getTeleportCosts()) + "").replace(".0", "")) + " " + Lang.get("Coins"));
                        builder.addLore("");
                        if(warp.getTeleportCosts() > 0) builder.addLore("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                        if(warp.getTeleportCosts() < TempWarpManager.getManager().getMaxTeleportCosts()) builder.addLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));

                        return builder.getItem();
                    }
                }.setOption(option).setOnlyLeftClick(false));
            }
        }, WarpSystem.getInstance());

        this.warp = warp;
    }

    @Override
    public void close(Player p, boolean isClosing) {
        super.close(p, isClosing);

        if(!isClosingForGUI()) TempWarpManager.getManager().getReserved().remove(this.warp);
    }

    private static boolean canPay(Player player, double costs) {
        return AdapterType.getActive().getMoney(player) >= costs;
    }
}
