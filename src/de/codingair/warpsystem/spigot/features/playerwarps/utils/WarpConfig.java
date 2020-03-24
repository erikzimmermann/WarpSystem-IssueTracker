package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarpConfig;
import de.codingair.codingapi.tools.io.lib.ParseException;

import java.util.concurrent.TimeUnit;

public class WarpConfig {
    private final TimeUnit unit;
    private final int durationCosts;
    private final int durationSteps;
    private final int publicCosts;
    private final int messageCosts;

    public WarpConfig(TimeUnit unit, int durationCosts, int durationSteps, int publicCosts, int messageCosts) {
        this.unit = unit;
        this.durationCosts = durationCosts;
        this.durationSteps = durationSteps;
        this.publicCosts = publicCosts;
        this.messageCosts = messageCosts;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public int getDurationCosts() {
        return durationCosts;
    }

    public int getDurationSteps() {
        return durationSteps;
    }

    public int getPublicCosts() {
        return publicCosts;
    }

    public int getMessageCosts() {
        return messageCosts;
    }

    public TempWarpConfig clone() {
        return new TempWarpConfig(unit, durationCosts, durationSteps, publicCosts, messageCosts);
    }

    public String toJSONString() {
        JSON json = new JSON();

        json.put("Unit", unit.name());
        json.put("durationCosts", durationCosts);
        json.put("durationSteps", durationSteps);
        json.put("publicCosts", publicCosts);
        json.put("messageCosts", messageCosts);

        return json.toJSONString();
    }

    public static TempWarpConfig getByJSON(String s) {
        try {
            JSON json = (JSON) new JSONParser().parse(s);

            TimeUnit unit = TimeUnit.valueOf(json.get("Unit"));
            int durationCosts = json.getInteger("durationCosts");
            int durationSteps = json.getInteger("durationSteps");
            int publicCosts = json.getInteger("publicCosts");
            int messageCosts = json.getInteger("messageCosts");

            return new TempWarpConfig(unit, durationCosts, durationSteps, publicCosts, messageCosts);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
