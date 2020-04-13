package de.codingair.warpsystem.spigot.features.animations;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.customanimations.AnimationType;
import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnimationManager implements Manager {
    private static AnimationManager instance = null;
    private List<Animation> animationList = new ArrayList<>();
    private Animation active = null;

    public static AnimationManager getInstance() {
        if(instance == null) instance = WarpSystem.getInstance().getDataManager().getManager(FeatureType.ANIMATION_EDITOR);
        if(instance == null) instance = new AnimationManager();
        return instance;
    }

    @Override
    public boolean load(boolean loader) {
        if(WarpSystem.getInstance().getFileManager().getFile("Animations") == null) WarpSystem.getInstance().getFileManager().loadFile("Animations", "/Memory/");
        WarpSystem.log("  > Loading Animations");

        UTFConfig config = WarpSystem.getInstance().getFileManager().getFile("Animations").getConfig();
        destroy();

        boolean success = true;
        List<?> l = config.getList("Animations");
        if(l != null)
            for(Object data : l) {

                if(data instanceof Map) {
                    try {
                        Animation a = new Animation();
                        JSON json = new JSON((Map<?, ?>) data);
                        a.read(json);
                        animationList.add(a);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                } else if(data instanceof String) {
                    try {
                        Animation a = new Animation();
                        JSON json = (JSON) new JSONParser().parse((String) data);
                        a.read(json);
                        animationList.add(a);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            }

        active = getAnimation(config.getString("Active", null));
        if(active == null) active = createStandard();

        WarpSystem.log("    ...got " + animationList.size() + " animation(s)");

        return success;
    }

    private Animation createStandard() {
        return new Animation("§Standard§", new ParticlePart(AnimationType.CIRCLE, Particle.FLAME, 1, 1, CustomAnimation.MAX_SPEED));
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving Animations");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Animations");

        List<JSON> dataList = new ArrayList<>();
        for(Animation animation : this.animationList) {
            JSON json = new JSON();
            animation.write(json);
            dataList.add(json);
        }

        file.getConfig().set("Animations", dataList);
        file.getConfig().set("Active", active == null ? null : active.getName().equals("§Standard§") ? null : active.getName());
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + animationList.size() + " animation(s)");
    }

    @Override
    public void destroy() {
        this.animationList.clear();
    }

    public boolean addAnimation(Animation anim) {
        if(existsAnimation(anim.getName())) return false;
        this.animationList.add(anim);

        return true;
    }

    public Animation getAnimation(String name) {
        if(name == null) return null;
        for(Animation animation : this.animationList) {
            if(animation.getName().equalsIgnoreCase(name)) return animation;
        }

        return null;
    }

    public boolean removeAnimation(Animation animation) {
        if(!this.animationList.remove(animation)) return false;

        if(this.active == animation) {
            this.active = createStandard();
        }
        return true;
    }

    public List<Animation> getAnimationList() {
        return Collections.unmodifiableList(animationList);
    }

    public boolean existsAnimation(String name) {
        return getAnimation(name) != null;
    }

    public Animation getActive() {
        return active;
    }

    public void setActive(Animation active) {
        this.active = active;
    }
}
