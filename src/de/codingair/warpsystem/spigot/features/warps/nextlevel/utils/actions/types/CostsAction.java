package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.ActionObject;
import org.bukkit.entity.Player;

public class CostsAction extends ActionObject<Double> {
    public CostsAction(double price) {
        super(Action.COSTS, price);
    }

    public CostsAction() {
        this(0.0);
    }

    @Override
    public void read(String s) {
        if(s != null) {
            setValue(Double.parseDouble(s));
        }
    }

    @Override
    public String write() {
        return getValue() + "";
    }

    @Override
    public boolean perform(Player player) {
        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) return true;
        if(AdapterType.getActive() == null) return true;
        if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) return false;

        double prize = getValue();
        if(prize <= 0) return false;

        double bank = AdapterType.getActive().getMoney(player);

        if(bank < prize) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", (prize % ((int) prize) == 0 ? (int) prize : prize) + ""));
            return false;
        }

        AdapterType.getActive().setMoney(player, bank - prize);
        return true;
    }
}
