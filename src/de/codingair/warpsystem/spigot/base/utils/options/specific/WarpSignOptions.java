package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class WarpSignOptions extends FeatureOptions {
    public WarpSignOptions() {
        super(Origin.WarpSign);
    }

    public WarpSignOptions(WarpSignOptions options) {
        super(Origin.WarpSign, options);
        apply(options);
    }

    @Override
    public WarpSignOptions clone() {
        return new WarpSignOptions(this);
    }
}
