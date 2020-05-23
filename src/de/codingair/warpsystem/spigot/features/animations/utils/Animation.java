package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animation implements Serializable {
    private String name = null;
    private Location teleportLoc = null;
    private List<Buff> buffList = new ArrayList<>();
    private List<ParticlePart> particleParts = new ArrayList<>();
    private SoundData tickSound = new SoundData(Sound.BLOCK_NOTE_BLOCK_HARP, 1F, 0.5F);
    private SoundData teleportSound = new SoundData(Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);

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
    public boolean read(DataWriter d) {
        buffList.clear();
        particleParts.clear();

        this.name = d.getString("name");
        this.teleportLoc = d.getLocation("teleportlocation");

        JSONArray buffArray = d.getList("bufflist");
        for(Object o : buffArray) {
            JSON data = new JSON((Map<?, ?>) o);
            Buff b = new Buff();
            b.read(data);
            buffList.add(b);
        }

        JSONArray particleArray = d.getList("particleparts");
        for(Object o : particleArray) {
            JSON data = new JSON((Map<?, ?>) o);
            ParticlePart p = new ParticlePart();
            p.read(data);
            particleParts.add(p);
            break;
        }

        String[] data = d.getString("ticksound") == null ? null : d.getString("ticksound").split("#", -1);
        tickSound = data == null ? null : new SoundData(Sound.matchXSound(data[0]).orElse(null), Float.parseFloat(data[1]), Float.parseFloat(data[2]));

        data = d.getString("teleportsound") == null ? null : d.getString("teleportsound").split("#", -1);
        teleportSound = data == null ? null : new SoundData(Sound.matchXSound(data[0]).orElse(null), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("name", this.name);
        d.put("teleportlocation", this.teleportLoc);

        JSONArray buffArray = new JSONArray();
        for(Buff buff : this.buffList) {
            JSON data = new JSON();
            buff.write(data);
            buffArray.add(data);
        }
        d.put("bufflist", buffArray);

        JSONArray particleArray = new JSONArray();
        for(ParticlePart part : this.particleParts) {
            JSON data = new JSON();
            part.write(data);
            particleArray.add(data);
        }
        d.put("particleparts", particleArray);

        d.put("ticksound", tickSound == null ? null : tickSound.getSound().name() + "#" + tickSound.getVolume() + "#" + tickSound.getPitch());
        d.put("teleportsound", teleportSound == null ? null : teleportSound.getSound().name() + "#" + teleportSound.getVolume() + "#" + teleportSound.getPitch());
    }

    @Override
    public void destroy() {
        buffList.clear();
        particleParts.clear();
    }

    public Animation clone() {
        return new Animation(this);
    }

    public void apply(Animation clone) {
        buffList.clear();
        particleParts.clear();

        this.name = clone.name;
        this.teleportLoc = clone.teleportLoc;

        for(Buff buff : clone.getBuffList()) {
            this.buffList.add(new Buff(buff));
        }

        for(ParticlePart part : clone.getParticleParts()) {
            this.particleParts.add(new ParticlePart(part));
        }

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

    public void setTeleportLoc(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
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
}
