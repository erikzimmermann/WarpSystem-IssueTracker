package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.Serializable;
import de.codingair.warpsystem.utils.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Animation implements Serializable {
    private String name = null;
    private Location teleportLoc = null;
    private List<Buff> buffList = new ArrayList<>();
    private List<ParticlePart> particleParts = new ArrayList<>();
    private SoundData tickSound = new SoundData(Sound.NOTE_PIANO, 1F, 0.5F);
    private SoundData teleportSound = new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F);

    public Animation() {
    }

    public Animation(String name) {
        this.name = name;
    }

    public Animation(Animation clone) {
        apply(clone);
    }

    public Animation(String name, ParticlePart part) {
        this.name = name;
        this.particleParts.add(part);
    }

    @Override
    public boolean read(JSONObject json) {
        buffList.clear();
        particleParts.clear();

        this.name = json.get("name");
        this.teleportLoc = json.get("teleportlocation") == null ? null : Location.getByJSONString(json.get("teleportlocation"));

        JSONArray buffArray = json.get("bufflist");
        for(Object o : buffArray) {
            JSONObject data = (JSONObject) o;
            Buff b = new Buff();
            b.read(data);
            buffList.add(b);
        }

        JSONArray particleArray = json.get("particleparts");
        for(Object o : particleArray) {
            JSONObject data = (JSONObject) o;
            ParticlePart p = new ParticlePart();
            p.read(data);
            particleParts.add(p);
        }

        String[] data = json.get("ticksound") == null ? null : ((String) json.get("ticksound")).split("#", -1);
        tickSound = data == null ? null : new SoundData(Sound.valueOf(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));

        data = json.get("teleportsound") == null ? null : ((String) json.get("teleportsound")).split("#", -1);
        teleportSound = data == null ? null : new SoundData(Sound.valueOf(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
        return true;
    }

    @Override
    public void write(JSONObject json) {
        json.put("name", this.name);
        json.put("teleportlocation", this.teleportLoc == null ? null : this.teleportLoc.toJSONString(4));

        JSONArray buffArray = new JSONArray();
        for(Buff buff : this.buffList) {
            JSONObject data = new JSONObject();
            buff.write(data);
            buffArray.add(data);
        }
        json.put("bufflist", buffArray);

        JSONArray particleArray = new JSONArray();
        for(ParticlePart part : this.particleParts) {
            JSONObject data = new JSONObject();
            part.write(data);
            particleArray.add(data);
        }
        json.put("particleparts", particleArray);

        json.put("ticksound", tickSound == null ? null : tickSound.getSound().name() + "#" + tickSound.getVolume() + "#" + tickSound.getPitch());
        json.put("teleportsound", teleportSound == null ? null : teleportSound.getSound().name() + "#" + teleportSound.getVolume() + "#" + teleportSound.getPitch());
    }

    @Override
    public void destroy() {
    }

    public Animation clone() {
        return new Animation(this);
    }

    public void apply(Animation clone) {
        buffList.clear();
        particleParts.clear();

        this.name = clone.name;
        this.teleportLoc = clone.teleportLoc;
        this.buffList.addAll(clone.getBuffList());
        this.particleParts.addAll(clone.getParticleParts());
        this.tickSound = new SoundData(clone.tickSound.getSound(), clone.tickSound.getVolume(), clone.tickSound.getPitch());
        this.teleportSound = new SoundData(clone.teleportSound.getSound(), clone.teleportSound.getVolume(), clone.teleportSound.getPitch());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getTeleportLoc() {
        return teleportLoc;
    }

    public List<Buff> getBuffList() {
        return buffList;
    }

    public List<ParticlePart> getParticleParts() {
        return particleParts;
    }

    public SoundData getTickSound() {
        return tickSound;
    }

    public void setTickSound(SoundData tickSound) {
        this.tickSound = tickSound;
    }

    public SoundData getTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(SoundData teleportSound) {
        this.teleportSound = teleportSound;
    }

    public void setTeleportLoc(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
    }
}
