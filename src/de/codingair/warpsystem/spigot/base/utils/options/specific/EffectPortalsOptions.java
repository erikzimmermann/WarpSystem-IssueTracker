package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class EffectPortalsOptions extends FeatureOptions {
    private Option<Boolean> enabled = new Option<>("WarpSystem.Functions.Portals", true);

    public EffectPortalsOptions() {
        super(Origin.EffectPortal);
    }

    public EffectPortalsOptions(EffectPortalsOptions options) {
        super(Origin.EffectPortal, options);
        apply(options);
    }

    @Override
    public void write() {
        set(enabled);
        super.write();
    }

    @Override
    public void read() {
        get(enabled);
        super.read();
    }

    @Override
    public void apply(Options options) {
        if(options instanceof EffectPortalsOptions) {
            EffectPortalsOptions o = (EffectPortalsOptions) options;

            this.enabled = o.enabled.clone();
            super.apply(options);
        }
    }

    @Override
    public EffectPortalsOptions clone() {
        return new EffectPortalsOptions(this);
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }
}
