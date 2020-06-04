package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class PortalOptions extends FeatureOptions {
    public PortalOptions() {
        super(Origin.Portal);
    }

    public PortalOptions(PortalOptions options) {
        super(Origin.Portal, options);
        apply(options);
    }

    @Override
    public PortalOptions clone() {
        return new PortalOptions(this);
    }
}
