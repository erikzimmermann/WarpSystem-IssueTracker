package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.warpsystem.spigot.base.utils.featureobjects.Serializable;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;

public class Buff implements Serializable {
    private PotionEffectType type;
    private int level, timeBeforeTeleport, timeAfterTeleport;

    public Buff() {
    }

    public Buff(PotionEffectType type, int level, int timeBeforeTeleport, int timeAfterTeleport) {
        this.type = type;
        this.level = level;
        this.timeBeforeTeleport = timeBeforeTeleport;
        this.timeAfterTeleport = timeAfterTeleport;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        this.type = PotionEffectType.getByName((String) json.get("name"));
        this.level = Integer.parseInt(json.get("level") + "");
        this.timeBeforeTeleport = Integer.parseInt(json.get("timebeforeteleport") + "");
        this.timeAfterTeleport = Integer.parseInt(json.get("timeafterteleport") + "");
        return true;
    }

    @Override
    public void write(JSONObject json) {
        json.put("name", this.type.getName());
        json.put("level", this.level + "");
        json.put("timebeforeteleport", this.timeBeforeTeleport + "");
        json.put("timeafterteleport", this.timeAfterTeleport + "");
    }

    @Override
    public void destroy() {
    }

    public PotionEffectType getType() {
        return type;
    }

    public void setType(PotionEffectType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        if(this.level < 1) this.level = 1;
        if(this.level > 3) this.level = 3;
    }

    public int getTimeBeforeTeleport() {
        return timeBeforeTeleport;
    }

    public void setTimeBeforeTeleport(int timeBeforeTeleport) {
        this.timeBeforeTeleport = timeBeforeTeleport;
        if(this.timeBeforeTeleport < 0) this.timeBeforeTeleport = 0;
        if(this.timeBeforeTeleport > 20) this.timeBeforeTeleport = 20;
    }

    public int getTimeAfterTeleport() {
        return timeAfterTeleport;
    }

    public void setTimeAfterTeleport(int timeAfterTeleport) {
        this.timeAfterTeleport = timeAfterTeleport;
        if(this.timeAfterTeleport < 0) this.timeAfterTeleport = 0;
        if(this.timeAfterTeleport > 60) this.timeAfterTeleport = 60;
    }
}
