package de.codingair.warpsystem.utils;

import java.util.Map;

public class JSONObject extends org.json.simple.JSONObject {
    public JSONObject() {
    }

    public JSONObject(Map map) {
        super(map);
    }

    @Override
    public Object put(Object key, Object value) {
        if(value == null) return remove(key);
        else return super.put(key, value);
    }

    public <T> T  get(String key) {
        Object o = super.get(key);
        return o == null ? null : (T) o;
    }
}
