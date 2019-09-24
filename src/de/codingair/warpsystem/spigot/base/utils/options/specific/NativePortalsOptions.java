package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class NativePortalsOptions extends FeatureOptions {
    private Option<Boolean> enabled = new Option<>("WarpSystem.Functions.NativePortals", true);

    public NativePortalsOptions() {
        super(Origin.NativePortal);
    }

    public NativePortalsOptions(NativePortalsOptions options) {
        super(Origin.NativePortal, options);
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
        if(options instanceof NativePortalsOptions) {
            NativePortalsOptions o = (NativePortalsOptions) options;

            this.enabled = o.enabled.clone();
            super.apply(options);
        }
    }

    @Override
    public NativePortalsOptions clone() {
        return new NativePortalsOptions(this);
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }
}
