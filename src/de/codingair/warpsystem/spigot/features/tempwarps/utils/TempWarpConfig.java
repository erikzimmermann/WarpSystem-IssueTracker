package de.codingair.warpsystem.spigot.features.tempwarps.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.TimeUnit;

public class TempWarpConfig {
    private final TimeUnit unit;
    private final int durationCosts;
    private final int durationSteps;
    private final int publicCosts;
    private final int messageCosts;

    public TempWarpConfig(TimeUnit unit, int durationCosts, int durationSteps, int publicCosts, int messageCosts) {
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
        JSONObject json = new JSONObject();

        json.put("Unit", unit.name());
        json.put("durationCosts", durationCosts);
        json.put("durationSteps", durationSteps);
        json.put("publicCosts", publicCosts);
        json.put("messageCosts", messageCosts);

        return json.toJSONString();
    }

    public static TempWarpConfig getByJSON(String s) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            TimeUnit unit = TimeUnit.valueOf((String) json.get("Unit"));
            int durationCosts = Integer.parseInt(json.get("durationCosts") + "");
            int durationSteps = Integer.parseInt(json.get("durationSteps") + "");
            int publicCosts = Integer.parseInt(json.get("publicCosts") + "");
            int messageCosts = Integer.parseInt(json.get("messageCosts") + "");

            return new TempWarpConfig(unit, durationCosts, durationSteps, publicCosts, messageCosts);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
