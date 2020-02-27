package de.codingair.warpsystem.spigot.features.playerwarps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.MusicData;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.pages.POptions;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.pages.PTrusted;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;

import java.util.*;

public class PWEditor extends Editor<PlayerWarp> {
    private PlayerWarp original;
    private PlayerWarp warp;
    private boolean creating;
    private Number paid = 0;

    public PWEditor(Player p, String name) {
        this(p, new PlayerWarp(p, name).setPublic(PlayerWarpManager.getManager().isFirstPublic()).setTime(PlayerWarpManager.getManager().getMinTime()));
    }

    public PWEditor(Player p, PlayerWarp warp) {
        this(p, warp, warp.clone());
    }

    private PWEditor(Player p, PlayerWarp warp, PlayerWarp clone) {
        super(p, clone, new Backup<PlayerWarp>(warp) {
            @Override
            public void applyTo(PlayerWarp clone) {
                boolean creating = !PlayerWarpManager.getManager().exists(warp.getName());
                Number costs = calculateCosts(creating, warp, clone);
                MoneyAdapterType.getActive().withdraw(p, costs.doubleValue());

                clone.setStarted(clone.getStarted() + clone.getPassedTime());
                warp.apply(clone);
                clone.destroy();

                if(creating) PlayerWarpManager.getManager().add(warp);
            }

            @Override
            public void cancel(PlayerWarp clone) {
                clone.destroy();
            }
        }, () -> clone.getItem().getItem(), new PAppearance(p, clone, warp, PlayerWarpManager.getManager().exists(warp.getName())), new POptions(p, clone, warp, PlayerWarpManager.getManager().exists(warp.getName())), new PTrusted(p, clone, warp));

        this.original = warp;
        this.warp = clone;

        if(clone.getLeftTime() < PlayerWarpManager.getManager().getMinTime()) clone.setTime(PlayerWarpManager.getManager().getMinTime());

        this.creating = !PlayerWarpManager.getManager().exists(warp.getName());

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.7F, 1.5F));
        setCancelSound(new SoundData(Sound.ITEM_BREAK, 0.7F, 1F));

        MusicData music0 = new MusicData(Sound.LEVEL_UP, 0.7F, 0.9F, 0);
        MusicData music1 = new MusicData(Sound.LEVEL_UP, 0.7F, 1.2F, 1);
        music0.setFollower(music1);
        setSuccessSound(music0);

        initControllButtons();
    }

    public void updateTime() {
        if(getCurrent().getClass() == POptions.class) {
            ((SyncButton) getCurrent().getButton(4, 2)).update();
        }

        updateCosts();
    }

    public void updateCosts() {
        ((SyncButton) getButtonAt(8, 2)).update();
    }

    public static String getCostsMessage(double costs) {
        if(costs == 0) return null;
        return Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Costs") + ": §7" + cut(costs) + " " + Lang.get("Coins");
    }

    public static Number cut(double n) {
        if(n == (int) n) return (int) n;
        else {
            double d = ((double) (int) (n * 100)) / 100;

            if(PlayerWarpManager.getManager().isNaturalNumbers()) return Math.round(d);
            else return d;
        }
    }

    private Number calculateCosts() {
        return paid = calculateCosts(creating, original, warp);
    }

    private static Number calculateCosts(boolean creating, PlayerWarp original, PlayerWarp warp) {
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
        if(warp == null) return costs;

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
            else diff = warp.getTime() + original.getPassedTime() - original.getTime();

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

    @Override
    public void open() {
        super.open();
        setOpenSound(null);
    }

    @Override
    public boolean canFinish() {
        if(this.warp == null) return false;
        return canPay(getPlayer(), calculateCosts().doubleValue());
    }

    @Override
    public String finishButtonNameAddition() {
        Number costs = calculateCosts();
        return costs.doubleValue() == 0 ? "" : "§7 - " + (costs.doubleValue() >= 0 ? (Lang.get("Costs") + ": " + (canPay(getPlayer(), costs.doubleValue()) ? "§a" : "§c") + costs + " " + Lang.get("Coins")) :
                Lang.get("Refund") + ": §a" + cut(-costs.doubleValue()) + " " + Lang.get("Coins"));
    }

    @Override
    public List<String> finishButtonLoreAddition() {
        List<String> lore = new ArrayList<>();
        double[] costs = calculate(creating, original, warp);
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

    private static boolean canPay(Player player, double costs) {
        return MoneyAdapterType.getActive().getMoney(player) >= costs;
    }

    @Override
    public String getSuccessMessage() {
        if(creating) return Lang.getPrefix() + Lang.get("Warp_Created").replace("%NAME%", warp.getName()).replace("%PRICE%", paid + "");
        else if(paid.doubleValue() == 0) return super.getSuccessMessage();
        else if(paid.doubleValue() > 0) return Lang.getPrefix() + Lang.get("Warp_Edited_Pay").replace("%NAME%", warp.getName()).replace("%PRICE%", paid + "");
        else return Lang.getPrefix() + Lang.get("Warp_Edited_Refund").replace("%NAME%", warp.getName()).replace("%PRICE%", cut(-paid.doubleValue()) + "");
    }

    public PlayerWarp getClone() {
        return warp;
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Player_Warp_Editor");
    }
}