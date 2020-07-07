package de.codingair.warpsystem.spigot.base.utils.teleport;

public class SimulatedTeleportResult {
    private String error;
    private Result result;

    public SimulatedTeleportResult(String error, Result result) {
        this.error = error;
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public Result getResult() {
        return result;
    }
}
