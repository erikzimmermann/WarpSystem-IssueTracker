package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAction extends ActionObject<List<String>> {
    public MessageAction(String... messages) {
        super(Action.MESSAGE, new ArrayList<>(Arrays.asList(messages)));
    }

    public MessageAction(List<String> messages) {
        super(Action.MESSAGE, messages);
    }

    public MessageAction() {
        super(Action.MESSAGE, null);
    }

    @Override
    public void read(String s) {
        try {
            JSONArray json = (JSONArray) new JSONParser().parse(s);
            setValue(json);
        } catch(ParseException e) {
            List<String> messages = new ArrayList<>();
            messages.add(s);
            setValue(messages);
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
        for(String message : getValue()) {
            player.sendMessage(message);
        }

        return true;
    }

    @Override
    public boolean read(DataWriter d) {
        setValue(d.getList("messages"));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("messages", getValue());
    }

    @Override
    public boolean usable() {
        return false;
    }

    @Override
    public MessageAction clone() {
        return new MessageAction(new ArrayList<>(getValue()));
    }
}
