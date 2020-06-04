package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class FeatureOptions extends Options {
    private Option<Boolean> sendTeleportMessage;
    protected Option<Boolean> enabled;
    private Origin origin;

    public FeatureOptions(Origin origin) {
        super("Config");
        this.origin = origin;
        enabled = new Option<>("WarpSystem.Functions." + origin.getConfigName());
        sendTeleportMessage = new Option<>("WarpSystem.Send.Teleport_Message." + origin.getConfigName());
    }

    public FeatureOptions(Origin origin, FeatureOptions options) {
        super(options.getFile());
        this.origin = origin;
        enabled = new Option<>("WarpSystem.Functions." + origin.getConfigName());
        sendTeleportMessage = new Option<>("WarpSystem.Send.Teleport_Message." + origin.getConfigName());
        apply(options);
    }

    @Override
    public void write() {
        set(sendTeleportMessage);
        set(enabled);
        save();
    }

    @Override
    public void read() {
        get(sendTeleportMessage);
        get(enabled);
    }

    @Override
    public void apply(Options options) {
        if(options instanceof FeatureOptions) {
            FeatureOptions o = (FeatureOptions) options;
            this.sendTeleportMessage = o.sendTeleportMessage.clone();
            this.enabled = o.enabled.clone();
        }
    }

    @Override
    public FeatureOptions clone() {
        return new FeatureOptions(origin, this);
    }

    public Option<Boolean> getSendTeleportMessage() {
        return sendTeleportMessage;
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }
}
