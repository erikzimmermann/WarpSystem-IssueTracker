package de.codingair.warpsystem.spigot.base.utils.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;

public class WarpSystemCommandBuilder extends CommandBuilder {
    public WarpSystemCommandBuilder(String name, BaseComponent baseComponent, String... aliases) {
        super(WarpSystem.getInstance(), name, "A WarpSystem-Command", baseComponent, true, aliases);
    }
}
