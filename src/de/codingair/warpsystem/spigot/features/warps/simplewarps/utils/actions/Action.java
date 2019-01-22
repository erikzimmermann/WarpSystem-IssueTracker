package de.codingair.warpsystem.spigot.features.warps.simplewarps.utils.actions;

import org.bukkit.entity.Player;

public interface Action {
    void onRun(Player player);

    String toString();

    void byString(String s);

    static <T extends Action> T read(String s) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] data = s.split("/");
        String className = data[0];
        String commit = data[1];

        Class<? extends Action> actionClass = (Class<? extends Action>) Class.forName("de.codingair.warpsystem.spigot.features.warps.simplewarps.utils.actions.types." + className);

        Action action = actionClass.newInstance();
        action.byString(commit);
        return (T) action;
    }
}
