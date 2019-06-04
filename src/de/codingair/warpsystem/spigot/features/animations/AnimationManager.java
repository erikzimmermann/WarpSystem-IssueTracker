package de.codingair.warpsystem.spigot.features.animations;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager implements Manager {
    private static AnimationManager instance = null;
    private List<Animation> animationList = new ArrayList<>();

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void save(boolean saver) {

    }

    @Override
    public void destroy() {

    }

    public Animation getAnimation(String name) {
        for(Animation animation : this.animationList) {
            if(animation.getName().equalsIgnoreCase(name)) return animation;
        }

        return null;
    }

    public boolean existsAnimation(String name) {
        return getAnimation(name) != null;
    }

    public static AnimationManager getInstance() {
        if(instance == null) instance = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        if(instance == null) instance = new AnimationManager();
        return instance;
    }
}
