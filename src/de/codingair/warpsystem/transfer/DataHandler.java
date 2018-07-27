package de.codingair.warpsystem.transfer;

public interface DataHandler {
    String GET_CHANNEL = "WarpSystem.GET";
    String REQUEST_CHANNEL = "WarpSystem.REQUEST";
    void onEnable();
    void onDisable();
}
