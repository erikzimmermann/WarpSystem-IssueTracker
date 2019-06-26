package de.codingair.warpsystem.spigot.base.utils.featureobjects;

import de.codingair.codingapi.tools.JSON.JSONObject;

public interface Serializable {
    boolean read(JSONObject json) throws Exception;
    void write(JSONObject json);

    void destroy();
}
