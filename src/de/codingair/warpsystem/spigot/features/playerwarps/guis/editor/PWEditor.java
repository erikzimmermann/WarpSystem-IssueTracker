package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.MusicData;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Ticker;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.PClasses;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.POptions;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.PTrusted;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.ActiveTimeButton;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PWEditor extends Editor<PlayerWarp> implements Ticker {
    private final PlayerWarp original;
    private final PlayerWarp warp;
    private final boolean creating;
    private Number paid = 0;

    public PWEditor(Player p, String name) {
        this(p, new PlayerWarp(p, name).setPublic(PlayerWarpManager.getManager().isAllowPublicWarps() && PlayerWarpManager.getManager().isFirstPublic()).setTime(PlayerWarpManager.getManager().getTimeStandardValue()));
    }

    public PWEditor(Player p, PlayerWarp warp) {
        this(p, warp, warp.clone().setTime(warp.getLeftTime()).setStarted(0));
    }

    private PWEditor(Player p, PlayerWarp warp, PlayerWarp clone) {
        super(p, clone, new Backup<PlayerWarp>(warp) {
                    @Override
                    public void applyTo(PlayerWarp clone) {
                        boolean isOwner = warp.isOwner(p);
                        boolean creating = isOwner && !PlayerWarpManager.getManager().existsOwn(p, warp.getName());
                        Number costs = calculateCosts(creating, warp, isOwner ? clone : null);
                        if(MoneyAdapterType.canEnable() && PlayerWarpManager.getManager().isEconomy() && costs.doubleValue() > 0) MoneyAdapterType.getActive().withdraw(p, costs.doubleValue());

                        clone.setStarted(System.currentTimeMillis());

                        if(creating || warp.isSource()) {
                            warp.apply(clone);
                            if(creating) PlayerWarpManager.getManager().add(warp);

                            if(PlayerWarpManager.getManager().checkBungeeCord()) {
                                PlayerWarpData data = warp.getData();
                                SendPlayerWarpsPacket packet = new SendPlayerWarpsPacket(new ArrayList<PlayerWarpData>() {{
                                    this.add(data);
                                }});
                                packet.setClearable(true);

                                WarpSystem.getInstance().getDataHandler().send(packet);
                            }
                        } else {
                            PlayerWarpManager.getManager().sync(warp, clone);
                            warp.apply(clone);
                        }

                        clone.destroy();
                    }

                    @Override
                    public void cancel(PlayerWarp clone) {
                        clone.destroy();
                    }
                }, () -> clone.getItem().getItem(),
                new PAppearance(p, clone, warp, !warp.isOwner(p) || PlayerWarpManager.getManager().existsOwn(p, warp.getName())),
                (PlayerWarpManager.getManager().isAllowPublicWarps() ? new POptions(p, clone, warp, !warp.isOwner(p) || PlayerWarpManager.getManager().existsOwn(p, warp.getName())) : null),
                (PlayerWarpManager.getManager().isAllowTrustedMembers() ? new PTrusted(p, clone, warp) : null),
                (p.hasPermission(WarpSystem.PERMISSION_MODIFY_PLAYER_WARPS) ? new PClasses(p, clone, warp) : null)
        );

        this.original = warp;
        this.warp = clone;

        if(clone.getLeftTime() < PlayerWarpManager.getManager().getMinTime()) clone.setTime(PlayerWarpManager.getManager().getMinTime());

        this.creating = warp.isOwner(getPlayer()) && !PlayerWarpManager.getManager().existsOwn(p, warp.getName());

        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.5F));
        setCancelSound(new SoundData(Sound.ENTITY_ITEM_BREAK, 0.7F, 1F));

        MusicData music0 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 0.9F, 0);
        MusicData music1 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.2F, 1);
        music0.setFollower(music1);
        setSuccessSound(music0);

        initControllButtons();
        API.addTicker(this);
    }

    @Override
    public void destroy() {
        API.removeTicker(this);
        super.destroy();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onSecond() {
        updateTime();
    }

    public static String getCostsMessage(double costs, PageItem page) {
        if(costs == 0 || !PlayerWarpManager.getManager().isEconomy() || (page.getLast() != null && !((PWEditor) page.getLast()).getClone().isOwner(page.getLast().getPlayer()))) return null;
        Number n = cut(costs);
        return Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Costs") + ": §7" + (n instanceof Integer ? n.intValue() : n) + " " + Lang.get("Coins");
    }

    public static String getFreeMessage(String free, PageItem page) {
        if(free == null || !PlayerWarpManager.getManager().isEconomy() || (page.getLast() != null && !((PWEditor) page.getLast()).getClone().isOwner(page.getLast().getPlayer()))) return null;
        return Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Free") + ": §7" + free;
    }

    public static Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if(d == (int) d) return (int) d;
        else return d;
    }

    public static Number calculateCosts(boolean creating, PlayerWarp original, PlayerWarp warp) {
        if(warp == null) return 0;
        double[] costs = calculate(creating, original, warp);
        double c = 0;

        for(double cost : costs) {
            c += cost;
        }

        return cut(c);
    }

    private static double[] calculate(boolean creating, PlayerWarp original, PlayerWarp warp) {
        double[] costs = new double[10];
        if(!PlayerWarpManager.getManager().isEconomy() || warp == null) return costs;

        //personal item
        if(!warp.isStandardItem()) {
            if(original.isStandardItem()) costs[0] = PlayerWarpManager.getManager().getItemCosts();
            else if(!warp.isSameItem(original.getItem())) costs[0] = PlayerWarpManager.getManager().getItemChangeCosts();
        } else if(!warp.isSameItem(original.getItem()))
            costs[0] = PlayerWarpManager.getManager().getItemChangeCosts() * PlayerWarpManager.getManager().getPersonalItemRefund() * original.getRefundFactor();

        //name
        if(!creating && !original.getName().equals(warp.getName())) costs[1] = PlayerWarpManager.getManager().getNameChangeCosts();

        //description
        int length = 0;
        if(original.getItem().getLore() != null)
            for(String s : original.getItem().getLore()) {
                length += s.replaceFirst("§f", "").length();
            }

        length = -length;
        if(warp.getItem().getLore() != null)
            for(String s : warp.getItem().getLore()) {
                length += s.replaceFirst("§f", "").length();
            }

        if(length > 0) {
            costs[2] = length * PlayerWarpManager.getManager().getDescriptionCosts();
        } else if(length < 0) {
            costs[2] = length * PlayerWarpManager.getManager().getDescriptionCosts() * PlayerWarpManager.getManager().getDescriptionRefund() * original.getRefundFactor();
        }

        //teleport message
        length = (warp.getTeleportMessage() == null ? 0 : warp.getTeleportMessage().length()) - (original.getTeleportMessage() == null ? 0 : original.getTeleportMessage().length());

        if(length > 0) {
            costs[3] = length * PlayerWarpManager.getManager().getMessageCosts();
        } else if(length < 0) {
            costs[3] = length * PlayerWarpManager.getManager().getMessageCosts() * PlayerWarpManager.getManager().getMessageRefund() * original.getRefundFactor();
        }

        //public state
        if(!original.isPublic() && warp.isPublic()) costs[4] = PlayerWarpManager.getManager().getPublicCosts();
        else if(original.isPublic() && !warp.isPublic()) costs[4] = -PlayerWarpManager.getManager().getPublicCosts() * PlayerWarpManager.getManager().getPublicRefund() * original.getRefundFactor();

        //teleport costs
        double tpCosts = warp.getTeleportCosts() - original.getTeleportCosts();
        if(tpCosts > 0) costs[5] = tpCosts * PlayerWarpManager.getManager().getTeleportCosts();
        else if(tpCosts < 0) costs[5] = tpCosts * PlayerWarpManager.getManager().getTeleportCosts() * PlayerWarpManager.getManager().getTeleportCostsRefund() * original.getRefundFactor();

        //target position
        if(!original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) costs[6] = PlayerWarpManager.getManager().getPositionChangeCosts();


        //active time
        if(creating || original.getLeftTime() <= 500) costs[7] = warp.getTime() / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts();
        else {
            long diff;
            if(warp.getLeftTime() <= 0) diff = -original.getLeftTime();
            else diff = warp.getTime() - original.getLeftTime();

            if(diff > 0) costs[7] = (diff / 60000D) * PlayerWarpManager.getManager().getActiveTimeCosts();
            else if(diff < 0) costs[7] = (diff / 60000D) * PlayerWarpManager.getManager().getActiveTimeCosts() * PlayerWarpManager.getManager().getActiveTimeRefund() * original.getRefundFactor();
        }

        //trusted members
        length = warp.getTrusted().size() - original.getTrusted().size();
        if(length > 0) costs[8] = length * PlayerWarpManager.getManager().getTrustedMemberCosts();
        else if(length < 0) costs[8] = length * PlayerWarpManager.getManager().getTrustedMemberCosts() * PlayerWarpManager.getManager().getTrustedMemberRefund() * original.getRefundFactor();

        //create or edit
        if(creating) {
            costs[9] = PlayerWarpManager.getManager().getCreateCosts();
        } else {
            costs[9] = PlayerWarpManager.getManager().getEditCosts();
        }

        return costs;
    }

    public static boolean canPay(Player player, double costs) {
        return !PlayerWarpManager.getManager().isEconomy() || !MoneyAdapterType.canEnable() || MoneyAdapterType.getActive().getMoney(player) >= costs;
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Player_Warp_Editor");
    }

    @Override
    public void initControllButtons() {
        super.initControllButtons();

        if(warp != null && !warp.isOwner(getPlayer())) {
            ItemButtonOption option = new ItemButtonOption();
            option.setOnlyLeftClick(true);

            addButton(new SyncButton(8, 2) {
                private BukkitRunnable runnable;

                @Override
                public ItemStack craftItem() {
                    boolean finish = canFinish();
                    return new ItemBuilder(finish ? XMaterial.LIME_TERRACOTTA : XMaterial.LIGHT_GRAY_TERRACOTTA).setName((finish ? "§a" : "§7") + Lang.get("Finish") + finishButtonNameAddition()).addLore((runnable != null ? "§7» §c" + ChatColor.stripColor(Lang.get("Confirm")) : null)).addLore(finishButtonLoreAddition()).getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(runnable != null) {
                        //save
                        close();
                        getBackup().applyTo(getClone());

                        SoundData sound = getSuccessSound();
                        if(sound != null) sound.play(player);

                        String msg = getSuccessMessage();
                        if(msg != null) getPlayer().sendMessage(msg);
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

                @Override
                public boolean canClick(ClickType click) {
                    return canFinish();
                }
            }.setOption(option));
        }
    }

    public void updateTime() {
        for(Button button : getCurrent().getButtons()) {
            if(button instanceof ActiveTimeButton) {
                ((SyncButton) button).update();
            }
        }

        updateCosts();
    }

    public void updateCosts() {
        ((SyncButton) getButtonAt(8, 2)).update();
    }

    private Number calculateCosts() {
        return paid = calculateCosts(creating, original, warp != null && warp.isOwner(getPlayer()) ? warp : null);
    }

    @Override
    public void open() {
        super.open();
        setOpenSound(null);
    }

    @Override
    public boolean canFinish() {
        if(this.warp == null || (PlayerWarpManager.getManager().isClasses() && warp.getClasses().size() < PlayerWarpManager.getManager().getClassesMin())) return false;
        if(!warp.isOwner(getPlayer())) return true;
        return canPay(getPlayer(), calculateCosts().doubleValue());
    }

    @Override
    public String finishButtonNameAddition() {
        Number costs = calculateCosts();
        return warp != null && !warp.isOwner(getPlayer()) ? "§7 - §c" + Lang.get("Admin") : costs.doubleValue() == 0 ? "" : "§7 - " + (costs.doubleValue() >= 0 ? (Lang.get("Costs") + ": " + (canPay(getPlayer(), costs.doubleValue()) ? "§a" : "§c") + costs + " " + Lang.get("Coins")) :
                Lang.get("Refund") + ": §a" + cut(-costs.doubleValue()) + " " + Lang.get("Coins"));
    }

    @Override
    public List<String> finishButtonLoreAddition() {
        List<String> lore = new ArrayList<>();

        if(warp != null && PlayerWarpManager.getManager().isClasses() && warp.getClasses().size() < PlayerWarpManager.getManager().getClassesMin()) {
            List<String> l = Lang.getStringList("PlayerWarp_Classes");
            List<String> modified = new ArrayList<>();
            for(String s : l) {
                if(s.trim().isEmpty()) continue;
                modified.add(s.replace("%MIN%", PlayerWarpManager.getManager().getClassesMin() + "").replace("%MAX%", PlayerWarpManager.getManager().getClassesMax() + ""));
            }
            l.clear();

            lore.add("");
            lore.addAll(modified);
            modified.clear();
        }

        double[] costs = calculate(creating, original, warp != null && warp.isOwner(getPlayer()) ? warp : null);
        double[] copy = Arrays.copyOf(costs, costs.length);
        Arrays.sort(copy);

        List<Integer> skip = new ArrayList<>();

        for(double cost : costs) {
            if(cost != 0) {
                lore.add("");
                break;
            }
        }

        for(int j = copy.length - 1; j >= 0; j--) {
            double d = copy[j];
            int i = 0;

            //personal item
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Item") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //name
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Name") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //description
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Description") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //teleport message
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Teleport_Message") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //public state
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Status") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //teleport co sts
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Teleport_Costs") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //target position
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Target_Position") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //active time
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Active_Time") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //trusted members
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                lore.add("§7" + Lang.get("Trusted_members") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                skip.add(i);
            }
            i++;

            //create or edit
            if(!skip.contains(i) && d == costs[i] && costs[i] != 0) {
                if(creating) {
                    lore.add("§7" + Lang.get("Construction_Costs") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                } else {
                    lore.add("§7" + Lang.get("Processing_Costs") + ": " + cut(costs[i]) + " " + Lang.get("Coins"));
                }
                skip.add(i);
            }
        }

        skip.clear();
        return lore;
    }

    @Override
    public String getSuccessMessage() {
        if(creating) {
            if(paid.doubleValue() > 0) return Lang.getPrefix() + Lang.get("Warp_Created").replace("%NAME%", warp.getName()).replace("%PRICE%", paid + "");
            else return Lang.getPrefix() + Lang.get("Warp_Created_Free").replace("%NAME%", warp.getName());
        } else if(paid.doubleValue() == 0) return Lang.getPrefix() + Lang.get("Warp_Edited").replace("%NAME%", warp.getName());
        else if(paid.doubleValue() > 0) return Lang.getPrefix() + Lang.get("Warp_Edited_Pay").replace("%NAME%", warp.getName()).replace("%PRICE%", paid + "");
        else return Lang.getPrefix() + Lang.get("Warp_Edited_Refund").replace("%NAME%", warp.getName()).replace("%PRICE%", cut(-paid.doubleValue()) + "");
    }

    public PlayerWarp getClone() {
        return warp;
    }
}
