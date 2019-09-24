package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class WarpSignOptions extends FeatureOptions {
    private Option<Boolean> enabled = new Option<>("WarpSystem.Functions.WarpSigns", true);

    public WarpSignOptions() {
        super(Origin.WarpSign);
    }

    public WarpSignOptions(WarpSignOptions options) {
        super(Origin.WarpSign, options);
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
        if(options instanceof WarpSignOptions) {
            WarpSignOptions o = (WarpSignOptions) options;

            this.enabled = o.enabled.clone();
            super.apply(options);
        }
    }

    @Override
    public WarpSignOptions clone() {
        return new WarpSignOptions(this);
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }
}
