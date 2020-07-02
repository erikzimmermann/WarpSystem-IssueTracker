package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import org.bukkit.ChatColor;

import java.util.Objects;

public class Options implements Serializable {
    private Boolean message;
    private String customMessage;
    private Integer delay;
    private Boolean rotation;

    public Options apply(Options options) {
        this.message = options.message;
        this.customMessage = options.customMessage;
        this.delay = options.delay;
        this.rotation = options.rotation;
        return this;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        Integer i = d.getInteger("message", null);
        if(i == null) message = null;
        else message = i == 2;

        customMessage = d.getString("custom_message", null);
        delay = d.getInteger("delay", null);

        i = d.getInteger("rotation", null);
        if(i == null) rotation = null;
        else rotation = i == 2;
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("message", message == null ? 0 : (message ? 2 : 1));
        d.put("custom_message", customMessage);
        d.put("delay", delay);
        d.put("rotation", rotation == null ? 0 : (rotation ? 2 : 1));
    }

    @Override
    public void destroy() {
        message = null;
        customMessage = null;
        delay = null;
        rotation = null;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Options options = (Options) o;
        return Objects.equals(message, options.message) &&
                Objects.equals(customMessage, options.customMessage) &&
                Objects.equals(delay, options.delay) &&
                Objects.equals(rotation, options.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, customMessage, delay, rotation);
    }

    public String buildMessage(String message) {
        if(this.message != null && !this.message) message = null;
        if(customMessage != null) message = ChatColor.translateAlternateColorCodes('&', customMessage);
        return message;
    }

    public boolean sendMessage() {
        return this.message == null || this.message;
    }

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public Integer getDelay(int seconds) {
        if(delay != null) seconds = delay;
        return seconds;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public boolean isRotation() {
        return rotation == null || rotation;
    }

    public void setRotation(Boolean rotation) {
        this.rotation = rotation ? null : rotation;
    }
}
