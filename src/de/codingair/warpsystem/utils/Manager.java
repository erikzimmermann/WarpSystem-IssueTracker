package de.codingair.warpsystem.utils;

public interface Manager {
    boolean load(boolean loader);
    void save(boolean saver);
    void destroy();
}
