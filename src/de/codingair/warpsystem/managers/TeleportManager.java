package de.codingair.warpsystem.managers;

import de.codingair.codingapi.particles.Particle;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.teleport.Teleport;
import de.codingair.warpsystem.teleport.portals.Portal;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    private List<Portal> portals = new ArrayList<>();
    private List<Particle> particles = new ArrayList<>();
    private List<Teleport> teleports = new ArrayList<>();
    private boolean canMove = false;
    private int seconds = 5;
    private int particleId = 0;
    private double radius = 1.5;

    public TeleportManager() {
        particles.add(Particle.FIREWORKS_SPARK);
        particles.add(Particle.SUSPENDED_DEPTH);
        particles.add(Particle.CRIT);
        particles.add(Particle.CRIT_MAGIC);
        particles.add(Particle.SMOKE_NORMAL);
        particles.add(Particle.SMOKE_LARGE);
        particles.add(Particle.SPELL);
        particles.add(Particle.SPELL_INSTANT);
        particles.add(Particle.SPELL_MOB);
        particles.add(Particle.SPELL_WITCH);
        particles.add(Particle.DRIP_WATER);
        particles.add(Particle.DRIP_LAVA);
        particles.add(Particle.VILLAGER_ANGRY);
        particles.add(Particle.VILLAGER_HAPPY);
        particles.add(Particle.TOWN_AURA);
        particles.add(Particle.NOTE);
        particles.add(Particle.ENCHANTMENT_TABLE);
        particles.add(Particle.FLAME);
        particles.add(Particle.CLOUD);
        particles.add(Particle.REDSTONE);
        particles.add(Particle.SNOW_SHOVEL);
        particles.add(Particle.HEART);
        particles.add(Particle.DRAGON_BREATH);
        particles.add(Particle.END_ROD);
        particles.add(Particle.DAMAGE_INDICATOR);
    }

    public void load() {
        this.particleId = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Animation", 17);
        this.seconds = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Delay", 5);
        this.canMove = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Allow_Move", false);

        //stop and clear old teleporters & load teleporters

        this.portals.forEach(Portal::destroy);
        this.portals.clear();

        for(String s : WarpSystem.getInstance().getFileManager().getFile("Teleporters").getConfig().getStringList("Teleporters")) {
            this.portals.add(Portal.getByJSONString(s));
        }

        this.portals.forEach(p -> p.setRunning(true));
    }

    public void save() {
        FileConfiguration config = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig();

        config.set("WarpSystem.Teleport.Animation", this.particleId);
        config.set("WarpSystem.Teleport.Delay", this.seconds);
        config.set("WarpSystem.Teleport.Allow_Move", this.canMove);

        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();


        //Save teleporters
        config = WarpSystem.getInstance().getFileManager().getFile("Teleporters").getConfig();

        List<String> data = new ArrayList<>();

        for(Portal portal : this.portals) {
            data.add(portal.toJSONString());
        }

        config.set("Teleporters", data);
        WarpSystem.getInstance().getFileManager().getFile("Teleporters").saveConfig();
    }

    public void teleport(Player player, Warp warp) {
        player.closeInventory();

        Teleport teleport = new Teleport(player, warp);
        this.teleports.add(teleport);

        if(seconds == 0 || (WarpSystem.OP_CAN_SKIP_DELAY && player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay))) teleport.teleport();
        else teleport.start();
    }

    public void cancelTeleport(Player p) {
        if(!isTeleporting(p)) return;

        Teleport teleport = getTeleport(p);
        teleport.cancel(true);
        this.teleports.remove(teleport);
    }

    public Teleport getTeleport(Player p) {
        for(Teleport teleport : teleports) {
            if(teleport.getPlayer().getName().equalsIgnoreCase(p.getName())) return teleport;
        }

        return null;
    }

    public boolean isTeleporting(Player p) {
        return getTeleport(p) != null;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getParticleId() {
        return particleId;
    }

    public Particle getParticle() {
        return particles.get(particleId);
    }

    public void setParticleId(int particleId) {
        this.particleId = particleId;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<Teleport> getTeleports() {
        return teleports;
    }
}
