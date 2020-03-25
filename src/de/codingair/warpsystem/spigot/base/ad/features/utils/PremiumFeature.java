package de.codingair.warpsystem.spigot.base.ad.features.utils;

public interface PremiumFeature {
    //result := reload needed
    boolean disable();
    String getName();
    String[] getSuccessMessage();
}