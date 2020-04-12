package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.transfer.packets.spigot.PerformCommandOnBungeePacket;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandAction extends ActionObject<List<String>> {
    public CommandAction(String... command) {
        super(Action.COMMAND, new ArrayList<>(Arrays.asList(command)));
    }

    public CommandAction(List<String> commands) {
        super(Action.COMMAND, commands);
    }

    public CommandAction() {
        super(Action.COMMAND, null);
    }

    @Override
    public void read(String s) {
        try {
            JSONArray json = (JSONArray) new JSONParser().parse(s);
            setValue(json);
        } catch(ParseException e) {
            List<String> commands = new ArrayList<>();
            commands.add(s);
            setValue(commands);
        }
    }

    @Override
    public String write() {
        if(getValue().size() == 1) return getValue().get(0);

        JSONArray json = new JSONArray();
        json.addAll(getValue());
        return json.toJSONString();
    }

    @Override
    public boolean perform(Player player) {
        for(String command : getValue()) {
            if(command.startsWith("/")) command = command.substring(1);

            String tag = command.contains(" ") ? command.split(" ")[0] : command;

            Command cmd = CommandBuilder.getCommand(tag);

            if(WarpSystem.getInstance().isOnBungeeCord() && cmd == null) {
                WarpSystem.getInstance().getDataHandler().send(new PerformCommandOnBungeePacket(player.getName(), command, new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean exists) {
                        if(!exists) player.sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
                    }
                }));
            } else {
                if(command.contains("%player%"))
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                else player.performCommand(command);
            }
        }

        return true;
    }

    @Override
    public boolean read(DataWriter d) {
        setValue(d.getList("commands"));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("commands", getValue());
    }

    @Override
    public boolean usable() {
        return getValue() != null;
    }

    @Override
    public CommandAction clone() {
        return new CommandAction(new ArrayList<>(getValue()));
    }
}
