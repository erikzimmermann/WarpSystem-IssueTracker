package de.codingair.warpsystem.spigot.base.utils.featureobjects;

import org.json.simple.JSONObject;

public interface Serializable {
    void read(JSONObject json) throws Exception;
    void write(JSONObject json);

    void destroy();
}
