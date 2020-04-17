package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TeleportOptions {
    private Origin origin;
    private Destination destination;
    private String displayName;
    private String permission;
    private double costs;
    private boolean skip;
    private boolean canMove;
    private boolean waitForTeleport; //Waiting for walking teleports
    private boolean confirmPayment = true;
    private String payMessage;
    private String paymentDeniedMessage;
    private String message;
    private boolean silent;
    private SoundData teleportSound;
    private boolean afterEffects;
    private boolean teleportAnimation = true;
    private List<Callback<TeleportResult>> callback = new ArrayList<>();
    private Vector velocity = null;

    public TeleportOptions() {
        this((Destination) null, null);
    }

    public TeleportOptions(Location location, String displayName) {
        this(new Destination(new LocationAdapter(location)), displayName);
    }

    public TeleportOptions(Destination destination, String displayName) {
        this.origin = Origin.Custom;
        this.destination = destination;
        this.displayName = displayName;
        this.permission = null;
        this.costs = 0;
        this.skip = false;
        this.canMove = WarpSystem.getInstance().getTeleportManager().getOptions().isAllowMove();
        this.waitForTeleport = false;
        this.message = Lang.getPrefix() + (displayName == null ? Lang.get("Teleported_To") : Lang.get("Teleported_To").replace("%warp%", displayName));
        this.payMessage = Lang.getPrefix() + (displayName == null ? Lang.get("Money_Paid") : Lang.get("Money_Paid").replace("%warp%", displayName));
        this.paymentDeniedMessage = Lang.getPrefix() + Lang.get("Payment_denied");
        this.silent = false;
        this.teleportSound = null;
        this.afterEffects = true;
    }

    public void destroy() {
        callback.clear();
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }

    public Number getFinalCosts(Player player) {
        return new ImprovedDouble(costs > 0 && MoneyAdapterType.getActive() != null && !player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs) ? costs : 0).get();
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isWaitForTeleport() {
        return waitForTeleport;
    }

    public void setWaitForTeleport(boolean waitForTeleport) {
        this.waitForTeleport = waitForTeleport;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public SoundData getTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(SoundData teleportSound) {
        this.teleportSound = teleportSound;
    }

    public boolean isAfterEffects() {
        return afterEffects;
    }

    public void setAfterEffects(boolean afterEffects) {
        this.afterEffects = afterEffects;
    }

    public void runCallbacks(TeleportResult result) {
        for(Callback<TeleportResult> teleportResultCallback : this.callback) {
            teleportResultCallback.accept(result);
        }
    }

    public void addCallback(Callback<TeleportResult> callback) {
        if(callback == null) return;
        this.callback.add(callback);
    }

    public String getPayMessage() {
        return payMessage;
    }

    public void setPayMessage(String payMessage) {
        this.payMessage = payMessage;
    }

    public String getFinalMessage(Player player) {
        return getFinalCosts(player).doubleValue() > 0 ? getPayMessage() : getMessage();
    }

    public boolean isConfirmPayment() {
        return confirmPayment;
    }

    public void setConfirmPayment(boolean confirmPayment) {
        this.confirmPayment = confirmPayment;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public String getPaymentDeniedMessage(Player player) {
        if(this.paymentDeniedMessage == null) return null;
        return this.paymentDeniedMessage.replace("%AMOUNT%", getFinalCosts(player) + "");
    }

    public void setPaymentDeniedMessage(String paymentDeniedMessage) {
        this.paymentDeniedMessage = paymentDeniedMessage;
    }

    public boolean isTeleportAnimation() {
        return teleportAnimation;
    }

    public void setTeleportAnimation(boolean teleportAnimation) {
        this.teleportAnimation = teleportAnimation;
    }
}
