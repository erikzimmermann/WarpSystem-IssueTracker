package de.codingair.warpsystem.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class JSONParser extends org.json.simple.parser.JSONParser {
    @Override
    public Object parse(String s) throws ParseException {
        Object o = super.parse(s);

        if(o instanceof JSONObject) {
            return new de.codingair.warpsystem.utils.JSONObject((JSONObject) o);
        } else return o;
    }
}
