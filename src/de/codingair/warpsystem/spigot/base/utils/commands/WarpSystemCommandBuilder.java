package de.codingair.warpsystem.spigot.base.utils.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;

public class WarpSystemCommandBuilder extends CommandBuilder {
    public WarpSystemCommandBuilder(String name, BaseComponent baseComponent, String... aliases) {
        super(name, "A WarpSystem-Command", baseComponent, true, aliases);
    }
}
