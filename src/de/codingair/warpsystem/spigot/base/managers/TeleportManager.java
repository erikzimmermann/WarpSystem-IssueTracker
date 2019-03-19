package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.events.PlayerGlobalWarpEvent;
import de.codingair.warpsystem.spigot.api.events.PlayerWarpEvent;
import de.codingair.warpsystem.spigot.api.events.utils.GlobalWarp;
import de.codingair.warpsystem.spigot.api.events.utils.Warp;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.Teleport;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    public static final String NO_PERMISSION = "%NO_PERMISSION%";
    private List<Particle> particles = new ArrayList<>();
    private List<Teleport> teleports = new ArrayList<>();

    private boolean canMove = false;
    private int seconds = 5;
    private int particleId = 0;
    private double radius = 1.5;
    private boolean showMessage = true;

    public TeleportManager() {
        particles.add(Particle.FIREWORKS_SPARK);
        particles.add(Particle.SUSPENDED_DEPTH);
        particles.add(Particle.CRIT);
        particles.add(Particle.CRIT_MAGIC);
        particles.add(Particle.SMOKE_NORMAL);
        particles.add(Particle.SMOKE_LARGE);
        particles.add(Particle.SPELL);
        particles.add(Particle.SPELL_INSTANT);
        particles.add(Particle.SPELL_MOB);
        particles.add(Particle.SPELL_WITCH);
        particles.add(Particle.DRIP_WATER);
        particles.add(Particle.DRIP_LAVA);
        particles.add(Particle.VILLAGER_ANGRY);
        particles.add(Particle.VILLAGER_HAPPY);
        particles.add(Particle.TOWN_AURA);
        particles.add(Particle.NOTE);
        particles.add(Particle.ENCHANTMENT_TABLE);
        particles.add(Particle.FLAME);
        particles.add(Particle.CLOUD);
        particles.add(Particle.REDSTONE);
        particles.add(Particle.SNOW_SHOVEL);
        particles.add(Particle.HEART);
        particles.add(Particle.PORTAL);
    }

    /**
     * Have to be launched after the IconManager (see WarpSign.class - fromJSONString method - need warps and categories)
     */
    public boolean load() {
        boolean success = true;

        this.particleId = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Animation", 17);
        this.seconds = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Delay", 5);
        this.canMove = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Allow_Move", false);
        this.showMessage = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true);
        return success;
    }

    public void save(boolean saver) {
        FileConfiguration config = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig();

        config.set("WarpSystem.Teleport.Animation", this.particleId);
        config.set("WarpSystem.Teleport.Delay", this.seconds);
        config.set("WarpSystem.Teleport.Allow_Move", this.canMove);

        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs) {
        teleport(player, origin, destination, displayName, costs, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean message) {
        teleport(player, origin, destination, displayName, costs, false, this.canMove, message, false, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean message, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, false, this.canMove, message, false, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, false, this.canMove, true, false, callback);
    }

    public void instantTeleport(Player player, Origin origin, Destination destination, String displayName) {
        teleport(player, origin, destination, displayName, 0, true, true, true, false, null);
    }

    public void instantTeleport(Player player, Origin origin, Destination destination, String displayName, boolean message) {
        teleport(player, origin, destination, displayName, 0, true, true, message, false, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, this.canMove, message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, boolean message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, canMove, message ?
                costs > 0 ?
                        Lang.getPrefix() + Lang.get("Money_Paid")
                        : Lang.getPrefix() + Lang.get("Teleported_To")
                : null, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, this.canMove, message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, boolean afterEffects, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, null, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, this.canMove, message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, boolean afterEffects, Callback<TeleportResult> callback) {
        if(WarpSystem.maintenance && !player.hasPermission(WarpSystem.PERMISSION_ByPass_Maintenance)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Warning_Maintenance"));
            return;
        }

        if(isTeleporting(player)) {
            Teleport teleport = getTeleport(player);
            long diff = System.currentTimeMillis() - teleport.getStartTime();
            if(diff > 50)
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
            return;
        }

        if((destination.getType() == DestinationType.GlobalWarp || destination.getType() == DestinationType.Server) && !WarpSystem.getInstance().isOnBungeeCord()) {
            if(callback != null) callback.accept(TeleportResult.NOT_ON_BUNGEE_CORD);
            player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Server_Is_Not_Online"));
            return;
        }

        int seconds = this.seconds;
        Callback<TeleportResult> resultCallback = null;

        if((WarpSystem.OP_CAN_SKIP_DELAY && player.isOp()) || player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay)) seconds = 0;

        //Call events
        if(origin != Origin.GlobalWarp || destination.getType() != DestinationType.UNKNOWN) {
            if(destination.getType() == DestinationType.GlobalWarp) {
                String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(destination.getId());
                String server = GlobalWarpManager.getInstance().getGlobalWarps().get(name);

                PlayerGlobalWarpEvent event = new PlayerGlobalWarpEvent(player, new GlobalWarp(name, server), origin, displayName, message, seconds, costs);
                Bukkit.getPluginManager().callEvent(event);

                if(event.isCancelled()) {
                    if(callback != null) callback.accept(TeleportResult.CANCELLED_BY_SYSTEM);
                    if(event.getTeleportResultCallback() != null) event.getTeleportResultCallback().accept(TeleportResult.CANCELLED_BY_SYSTEM);
                    return;
                }

                resultCallback = event.getTeleportResultCallback();
                costs = event.getCosts();
                seconds = event.getSeconds();
                displayName = event.getDisplayName();
                message = event.getMessage();
            }
        }

        Callback<TeleportResult> finalResultCallback = resultCallback;
        Teleport teleport = new Teleport(player, destination, displayName, permission, seconds, costs, message, canMove, silent, teleportSound, afterEffects, new Callback<TeleportResult>() {
            @Override
            public void accept(TeleportResult object) {
                if(callback != null) callback.accept(object);
                if(finalResultCallback != null) finalResultCallback.accept(object);
            }
        });

        SimulatedTeleportResult simulated = teleport.simulate(player);
        if(simulated.getError() != null) {
            player.sendMessage(simulated.getError());
            if(callback != null) callback.accept(simulated.getResult());
            return;
        }

        try {
            player.closeInventory();
        } catch(Throwable ignored) {}

        if(costs > 0) {
            if(!player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs) && AdapterType.getActive() != null) {
                double bank = AdapterType.getActive().getMoney(player);

                if(bank < costs) {
                    if(callback != null) callback.accept(TeleportResult.NOT_ENOUGH_MONEY);
                    player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", (costs % ((int) costs) == 0 ? (int) costs : costs) + ""));
                    return;
                }

                this.teleports.add(teleport);
                AdapterType.getActive().setMoney(player, bank - costs);
            } else this.teleports.add(teleport);
        } else this.teleports.add(teleport);

        if(seconds == 0 || skip) teleport.teleport();
        else teleport.start();
    }

    public void cancelTeleport(Player p) {
        if(!isTeleporting(p)) return;

        Teleport teleport = getTeleport(p);
        teleport.cancel(true, false);
        this.teleports.remove(teleport);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Cancel_Message", true)) {
            MessageAPI.sendActionBar(p, Lang.get("Teleport_Cancelled"));
        }
    }

    public Teleport getTeleport(Player p) {
        for(Teleport teleport : teleports) {
            if(teleport.getPlayer().getName().equalsIgnoreCase(p.getName())) return teleport;
        }

        return null;
    }

    public boolean isTeleporting(Player p) {
        return getTeleport(p) != null;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getParticleId() {
        return particleId;
    }

    public Particle getParticle() {
        return particles.get(particleId);
    }

    public void setParticleId(int particleId) {
        this.particleId = particleId;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Teleport> getTeleports() {
        return teleports;
    }
}
