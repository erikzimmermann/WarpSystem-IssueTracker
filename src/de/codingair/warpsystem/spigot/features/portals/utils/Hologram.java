package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Hologram implements Serializable {
    private String text;
    private boolean visible;
    private Location location;
    private double height;
    private de.codingair.codingapi.player.Hologram hologram;

    public Hologram() {
    }

    public Hologram(Hologram hologram) {
        apply(hologram);
    }

    public void apply(Hologram hologram) {
        this.text = hologram.text;
        this.visible = hologram.visible;
        this.height = hologram.height;
        this.location = hologram.location == null ? null : hologram.location.clone();
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.text = d.getString("text");
        this.visible = d.getBoolean("visible");
        setHeight(d.getDouble("height"));
        this.location = new Location();
        this.location.read(d);

        if(this.location.isEmpty()) this.location = null;
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("text", this.text);
        d.put("visible", this.visible);
        d.put("height", this.height);
        if(this.location != null) this.location.write(d);
    }

    public void updatePlayer(Player player) {
        if(this.hologram != null) this.hologram.addPlayer(player);
    }

    public void hide() {
        if(this.hologram != null) {
            this.hologram.setVisible(false);
            this.hologram.update();
        }
    }

    public boolean setVisible(boolean visible) {
        if(this.visible != visible) {
            this.visible = visible;
            return true;
        } else return false;
    }

    public void update() {
        if(visible && this.hologram == null && this.location != null && this.text != null)
            this.hologram = new de.codingair.codingapi.player.Hologram(location.clone().add(0, height, 0), WarpSystem.getInstance(), PortalManager.getInstance().getHologramUpdateInterval(), this.text) {
                @Override
                public String modifyText(Player player, String text) {
                    if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        return PlaceholderAPI.setPlaceholders(player, text);
                    }

                    return super.modifyText(player, text);
                }
            };

        if(this.hologram != null) {
            this.hologram.setVisible(this.visible);
            this.hologram.setText(ChatColor.translateAlternateColorCodes('&', this.text.replace("\\n", "\n")));
            this.hologram.teleport(this.location.clone().add(0, height, 0));
            this.hologram.update();
            this.hologram.addAll();
        }
    }

    public void destroy() {
        if(this.hologram != null) {
            setVisible(false);
            this.hologram.destroy();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isVisible() {
        return visible;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = ((double) (int) Math.round(height * 10)) / 10;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Hologram hologram = (Hologram) o;
        return visible == hologram.visible &&
                Double.compare(hologram.height, height) == 0 &&
                Objects.equals(text, hologram.text) &&
                Objects.equals(location, hologram.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, visible, location, height);
    }
}
