package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.ActionObject;
import de.codingair.warpsystem.transfer.packets.spigot.PerformCommandPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            if(WarpSystem.getInstance().isOnBungeeCord() && Bukkit.getPluginCommand(tag) == null) {
                WarpSystem.getInstance().getDataHandler().send(new PerformCommandPacket(player.getName(), command, new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean exists) {
                        if(!exists) player.sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
                    }
                }));
            } else player.performCommand(command);
        }

        return true;
    }
}
