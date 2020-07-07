package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.effects.RotatingParticleSpiral;
import org.bukkit.Location;

public class AfterEffects extends TeleportStage {
    private final Value<Location> afterEffectPosition;

    protected AfterEffects(Value<Location> afterEffectPosition) {
        this.afterEffectPosition = afterEffectPosition;
    }

    @Override
    public void start() {
        if(!options.isAfterEffects() || !player.isOnline() || options.getDestination().isBungee()) {
            end();
            return;
        }

        new RotatingParticleSpiral(player, afterEffectPosition.getValue(), options.isPublicAnimations()).runTaskTimer(WarpSystem.getInstance(), 1, 1);
        end();
    }

    @Override
    public void destroy() {
    }
}
