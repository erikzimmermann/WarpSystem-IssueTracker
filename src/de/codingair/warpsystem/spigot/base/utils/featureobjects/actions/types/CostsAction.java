package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
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
        if(MoneyAdapterType.getActive() == null) return true;
        if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) return false;

        double prize = getValue();
        if(prize <= 0) return false;

        double bank = MoneyAdapterType.getActive().getMoney(player);

        if(bank < prize) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", (prize % ((int) prize) == 0 ? (int) prize : prize) + ""));
            return false;
        }

        MoneyAdapterType.getActive().withdraw(player, prize);
        return true;
    }

    @Override
    public boolean read(DataWriter d) {
        setValue(d.getDouble("costs"));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("costs", getValue());
    }

    @Override
    public boolean usable() {
        return getValue() != null;
    }

    @Override
    public CostsAction clone() {
        return new CostsAction(getValue());
    }
}
