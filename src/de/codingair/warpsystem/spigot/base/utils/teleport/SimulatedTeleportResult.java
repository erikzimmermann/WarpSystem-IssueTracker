package de.codingair.warpsystem.spigot.base.utils.teleport;

public class SimulatedTeleportResult {
    private String error;
    private TeleportResult result;

    public SimulatedTeleportResult(String error, TeleportResult result) {
        this.error = error;
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public TeleportResult getResult() {
        return result;
    }
}
