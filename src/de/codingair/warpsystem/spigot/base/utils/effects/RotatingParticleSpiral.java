package de.codingair.warpsystem.spigot.base.utils.effects;

import de.codingair.codingapi.particles.Particle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RotatingParticleSpiral extends BukkitRunnable {
    private static final double CHANGE = 0.2;
    private static final double HEIGHT = 2.4;

    private Player player;
    private double r = 0;
    private double rMult = 1;
    private double y = 0;
    private Location loc;
    private double spin = 0;

    public RotatingParticleSpiral(Player player, Location loc) {
        this.player = player;
        this.loc = loc;
    }

    @Override
    public void run() {
        if(r < 0) {
            this.cancel();
            return;
        }

        boolean edge = true;
        for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 4) {
            double x = r * Math.cos(theta + spin);
            double z = r * Math.sin(theta + spin);

            loc.add(x, y, z);
            if(edge) Particle.FIREWORKS_SPARK.getParticlePacket(loc).send(player);
            else Particle.SPELL_WITCH.getParticlePacket(loc).send(player);
            loc.subtract(x, y, z);

            edge = !edge;
        }

        y += CHANGE;
        if(y <= HEIGHT / 2) {
            r += 1.25 * CHANGE * rMult;
            rMult -= CHANGE;
        } else {
            r -= 1.25 * CHANGE * rMult;
            rMult += CHANGE;
        }

        spin -= 0.2;
    }
}
