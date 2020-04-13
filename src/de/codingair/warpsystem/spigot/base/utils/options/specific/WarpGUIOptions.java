package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;

public class WarpGUIOptions extends FeatureOptions {
    private Option<Boolean> enabled = new Option<>("WarpSystem.Functions.Warps", true);
    private Option<Integer> userSize = new Option<>("WarpSystem.GUI.User.Size", 54, size -> 9 <= size && size <= 54 && size % 9 == 0);
    private Option<Integer> adminSize = new Option<>("WarpSystem.GUI.Admin.Size", 54, size -> 9 <= size && size <= 54 && size % 9 == 0);
    private Option<String> userStandardTitle = new Option<>("WarpSystem.GUI.User.Title.Standard", "&c&nWarps&r");
    private Option<String> userPageTitle = new Option<>("WarpSystem.GUI.User.Title.In_Category", "&c&nWarps&r &c@%PAGE%");
    private Option<String> adminStandardTitle = new Option<>("WarpSystem.GUI.Admin.Title.Standard", "&c&nWarps&r");
    private Option<String> adminPageTitle = new Option<>("WarpSystem.GUI.Admin.Title.In_Category", "&c&nWarps&r &c@%PAGE%");

    public WarpGUIOptions() {
        super(Origin.WarpIcon);
    }

    public WarpGUIOptions(WarpGUIOptions options) {
        super(Origin.WarpIcon, options);
        apply(options);
    }

    @Override
    public void write() {
        set(enabled);
        set(userSize);
        set(adminSize);
        set(userStandardTitle);
        set(userPageTitle);
        set(adminStandardTitle);
        set(adminPageTitle);

        super.write();
    }

    @Override
    public void read() {
        get(enabled);
        get(userSize);
        get(adminSize);
        get(userStandardTitle);
        get(userPageTitle);
        get(adminStandardTitle);
        get(adminPageTitle);

        super.read();
    }

    @Override
    public void apply(Options options) {
        if(options instanceof WarpGUIOptions) {
            WarpGUIOptions o = (WarpGUIOptions) options;

            this.enabled = o.enabled.clone();
            this.userSize = o.userSize.clone();
            this.adminSize = o.adminSize.clone();
            this.userStandardTitle = o.userStandardTitle.clone();
            this.userPageTitle = o.userPageTitle.clone();
            this.adminStandardTitle = o.adminStandardTitle.clone();
            this.adminPageTitle = o.adminPageTitle.clone();
            super.apply(options);
        }
    }

    @Override
    public WarpGUIOptions clone() {
        return new WarpGUIOptions(this);
    }

    public Option<Boolean> getEnabled() {
        return enabled;
    }

    public Option<Integer> getUserSize() {
        return userSize;
    }

    public Option<Integer> getAdminSize() {
        return adminSize;
    }

    public Option<String> getUserStandardTitle() {
        return userStandardTitle;
    }

    public Option<String> getUserPageTitle() {
        return userPageTitle;
    }

    public Option<String> getAdminStandardTitle() {
        return adminStandardTitle;
    }

    public Option<String> getAdminPageTitle() {
        return adminPageTitle;
    }
}
