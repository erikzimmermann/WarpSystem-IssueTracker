package de.codingair.warpsystem.spigot.features.warps.importfilter;

public enum Result {
    DONE(true),
    ERROR(false),
    UNAVAILABLE_NAME(true),
    MISSING_FILE(false);

    private boolean finished;

    Result(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
}
