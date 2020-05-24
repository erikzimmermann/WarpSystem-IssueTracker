package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class PortalOptions extends FeatureOptions {
    private Option<Boolean> enabled = new Option<>("WarpSystem.Functions.Portals");

    public PortalOptions() {
        super(Origin.Portal);
    }

    public PortalOptions(PortalOptions options) {
        super(Origin.Portal, options);
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
        if(options instanceof PortalOptions) {
            PortalOptions o = (PortalOptions) options;

            this.enabled = o.enabled.clone();
            super.apply(options);
        }
    }

    @Override
    public PortalOptions clone() {
        return new PortalOptions(this);
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }
}
