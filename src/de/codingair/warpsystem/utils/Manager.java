package de.codingair.warpsystem.utils;

public interface Manager {

    default void preLoad() {
    }

    boolean load(boolean loader);

    void save(boolean saver);

    void destroy();
}
