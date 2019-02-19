package de.codingair.warpsystem.spigot.features.effectportals;

import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.standalone.AnimationType;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.Node;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.menu.Menu;
import de.codingair.warpsystem.spigot.features.effectportals.utils.Portal;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PortalEditor implements Removable {
    public static String PLUS_MINUS(String s) {
        return ACTION_BAR(s, "+", "-");
    }

    public static String NEXT_PREVIOUS(String s) {
        return ACTION_BAR(s, "«", "»");
    }

    public static String ACTION_BAR(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    private final UUID uniqueId = UUID.randomUUID();
    public final AnimationType[] ANIMATION_TYPES = new AnimationType[] {AnimationType.CIRCLE, AnimationType.ROTATING_CIRCLE, AnimationType.PULSING_CIRCLE, AnimationType.SINUS};
    private Player player;
    private Portal portal;
    private Portal backupPortal;
    private boolean finished = false;
    private Menu menu;

    public enum Action {
        INCREASE_TELEPORT_RADIUS, DECREASE_TELEPORT_RADIUS,
        INCREASE_ANIMATION_HEIGHT, DECREASE_ANIMATION_HEIGHT,
        NEXT_ANIMATION_TYPE, PREVIOUS_ANIMATION_TYPE,
        NEXT_PARTICLE, PREVIOUS_PARTICLE,
        INCREASE_HOLOGRAM_HEIGHT, DECREASE_HOLOGRAM_HEIGHT,
        CHANGE_START_NAME, CHANGE_DESTINATION_NAME,
        NEXT_SOUND, PREVIOUS_SOUND,
        INCREASE_VOLUME, DECREASE_VOLUME,
        INCREASE_PITCH, DECREASE_PITCH, CANCEL, SAVE,
        CHANGE_PERMISSION
    }

    public PortalEditor(Player player, Portal portal) {
        this.player = player;
        this.backupPortal = portal;
        this.portal = new Portal(this.backupPortal);
        menu = new Menu(this.player, this);
    }

    public PortalEditor(Player player, Node<String, Location> first) {
        this.player = player;
        this.portal = new Portal(first.getValue(), new Destination(), AnimationType.CIRCLE, 1, WarpSystem.getInstance().getTeleportManager().getParticles().get(0), 1, first.getKey(), null, new SoundData(Sound.ENDERMAN_TELEPORT, 1, 1), 2.2, true, true);
        menu = new Menu(this.player, this);
    }

    @Override
    public void destroy() {
        exit(false);
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return Portal.class;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public int getCurrentAnimationTypeIndex() {
        int i = 0;
        for(AnimationType type : this.ANIMATION_TYPES) {
            if(this.portal.getAnimationType().equals(type)) return i;
            i++;
        }

        return 0;
    }

    public int getCurrentParticleIndex() {
        int i = 0;
        for(Particle particle : WarpSystem.getInstance().getTeleportManager().getParticles()) {
            if(this.portal.getParticle().equals(particle)) return i;
            i++;
        }

        return 0;
    }

    public int getCurrentSoundIndex() {
        int i = 0;
        for(Sound sound : Sound.values()) {
            if(this.portal.getTeleportSound().getSound().equals(sound)) return i;
            i++;
        }

        return 0;
    }

    public void doAction(Action action) {
        doAction(action, null);
    }

    public void doAction(Action action, Runnable after) {
        switch(action) {
            case CHANGE_PERMISSION:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        String input = e.getInput();

                        if(input != null && input.equalsIgnoreCase("NONE")) input = null;

                        portal.setPermission(input);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(portal.getPermission() == null ? "NONE" : portal.getPermission()).getItem());
                break;

            case CHANGE_START_NAME:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        String input = e.getInput();

                        if(input == null) {
                            e.setClose(false);
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        portal.setStartName(ChatColor.translateAlternateColorCodes('&', input));
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(portal.getStartName().replace("§", "&")).getItem());
                break;

            case CHANGE_DESTINATION_NAME:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        String input = e.getInput();

                        if(input == null) {
                            e.setClose(false);
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        portal.setDestinationName(ChatColor.translateAlternateColorCodes('&', input));
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(portal.getDestinationName().replace("§", "&")).getItem());
                break;

            case INCREASE_VOLUME:
                double volume = this.portal.getTeleportSound().getVolume() + 0.1;
                if(volume > 1.0) volume = 1.0;

                this.portal.setSoundVolume((float) volume);
                this.portal.getTeleportSound().play(player);
                break;

            case DECREASE_VOLUME:
                volume = this.portal.getTeleportSound().getVolume() - 0.1;
                if(volume < 0) volume = 0.0;

                this.portal.setSoundVolume((float) volume);
                this.portal.getTeleportSound().play(player);
                break;

            case INCREASE_ANIMATION_HEIGHT:
                this.portal.setAnimationHeight(this.portal.getAnimationHeight() + 0.1);
                break;

            case DECREASE_ANIMATION_HEIGHT:
                this.portal.setAnimationHeight(this.portal.getAnimationHeight() - 0.1);
                break;

            case INCREASE_HOLOGRAM_HEIGHT:
                this.portal.setHologramHeight(this.portal.getHologramHeight() + 0.1);
                break;

            case DECREASE_HOLOGRAM_HEIGHT:
                this.portal.setHologramHeight(this.portal.getHologramHeight() - 0.1);
                break;

            case INCREASE_PITCH:
                double pitch = this.portal.getTeleportSound().getPitch() + 0.1;
                if(pitch > 1.0) pitch = 1.0;

                this.portal.setSoundPitch((float) pitch);
                this.portal.getTeleportSound().play(player);
                break;

            case DECREASE_PITCH:
                pitch = this.portal.getTeleportSound().getPitch() - 0.1;
                if(pitch < 0) pitch = 0.0;

                this.portal.setSoundPitch((float) pitch);
                this.portal.getTeleportSound().play(player);
                break;

            case INCREASE_TELEPORT_RADIUS:
                this.portal.setTeleportRadius(this.portal.getTeleportRadius() + 0.1);
                break;

            case DECREASE_TELEPORT_RADIUS:
                this.portal.setTeleportRadius(this.portal.getTeleportRadius() - 0.1);
                break;

            case NEXT_ANIMATION_TYPE:
                int i = getCurrentAnimationTypeIndex() + 1;
                if(i >= ANIMATION_TYPES.length) i = 0;

                this.portal.setAnimationType(ANIMATION_TYPES[i]);
                break;

            case PREVIOUS_ANIMATION_TYPE:
                i = getCurrentAnimationTypeIndex() - 1;
                if(i < 0) i = ANIMATION_TYPES.length - 1;

                this.portal.setAnimationType(ANIMATION_TYPES[i]);
                break;

            case NEXT_PARTICLE:
                i = getCurrentParticleIndex() + 1;
                if(i >= WarpSystem.getInstance().getTeleportManager().getParticles().size()) i = 0;

                this.portal.setParticle(WarpSystem.getInstance().getTeleportManager().getParticles().get(i));
                break;

            case PREVIOUS_PARTICLE:
                i = getCurrentParticleIndex() - 1;
                if(i < 0) i = WarpSystem.getInstance().getTeleportManager().getParticles().size() - 1;

                this.portal.setParticle(WarpSystem.getInstance().getTeleportManager().getParticles().get(i));
                break;

            case NEXT_SOUND:
                i = getCurrentSoundIndex() + 1;
                if(i >= Sound.values().length) i = 0;

                this.portal.getTeleportSound().setSound(Sound.values()[i]);

                this.portal.getTeleportSound().play(player);
                break;

            case PREVIOUS_SOUND:
                i = getCurrentSoundIndex() - 1;
                if(i < 0) i = Sound.values().length - 1;

                this.portal.getTeleportSound().setSound(Sound.values()[i]);

                this.portal.getTeleportSound().play(player);
                break;

            case SAVE:
                finish();
                return;

            case CANCEL:
                exit();
        }
    }

    public void start() {
        API.addRemovable(this);

        if(this.backupPortal != null) this.backupPortal.setRunning(false);
        this.portal.setRunning(true);
        this.menu.open(true);

        this.player.sendMessage(Lang.getPrefix() + Lang.get("Entering_Portal_Editor"));
    }

    private void quit() {
        this.menu.close(true);
    }

    public void finish() {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        if(finished) return;
        if(this.backupPortal != null) this.backupPortal.applyAttrs(this.portal);

        exit(true);

        if(this.backupPortal == null) manager.getPortals().add(this.portal);
    }

    public void exit() {
        exit(portal.isRegistered() || (backupPortal != null && backupPortal.isRegistered()));
    }

    private void exit(boolean running) {
        if(finished) return;
        finished = true;

        this.portal.setRunning(this.backupPortal == null && running);
        if(this.backupPortal != null) this.backupPortal.setRunning(running);

        quit();
        API.removeRemovable(this);
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getPortal() {
        return portal;
    }

    public Portal getBackupPortal() {
        return backupPortal;
    }
}
