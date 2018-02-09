package de.codingair.warpsystem.importfilter;

public enum Result {
    DONE(true),
    ERROR(false),
    UNAVAILABLE_NAME(false),
    MISSING_FILE(false);

    private boolean finished;

    Result(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
}
