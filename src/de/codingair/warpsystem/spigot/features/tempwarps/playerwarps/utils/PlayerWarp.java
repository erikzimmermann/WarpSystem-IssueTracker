package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.DataWriter;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.Serializable;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerWarp extends FeatureObject {
    private User owner;
    private List<User> trusted;

    private String name;
    private List<String> description;
    private String teleportMessage;
    private ItemBuilder item;

    private boolean isPublic;
    private double teleportCosts;

    private long born;
    private long started;
    private long expired;
    private long time;
    private String creatorKey = null;

    public PlayerWarp() {
        super();
        owner = new User("owner");
        trusted = new ArrayList<>();
        description = new ArrayList<>();
    }

    public PlayerWarp(Player player, String name) {
        this();
        this.name = name;
        owner.id = player.getUniqueId();
        owner.name = player.getName();

        this.item = new ItemBuilder();
        resetItem();
        addAction(new WarpAction(new Destination(Location.getByLocation(player.getLocation()).toJSONString(2), DestinationType.Location)));
    }

    private PlayerWarp(PlayerWarp warp) {
        super(warp);
        apply(warp);
    }

    @Override
    public FeatureObject perform(Player player) {
        TeleportOptions options = new TeleportOptions((Destination) null, this.name);
        if(this.teleportMessage != null) options.setMessage(ChatColor.translateAlternateColorCodes('&', this.teleportMessage));

        return super.perform(player, options);
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if(object instanceof PlayerWarp) {
            PlayerWarp w = (PlayerWarp) object;

            this.owner = w.owner;
            this.trusted = w.trusted;
            this.name = w.name;
            this.description = w.description;
            this.teleportMessage = w.teleportMessage;
            this.item = w.item;
            this.isPublic = w.isPublic;
            this.teleportCosts = w.teleportCosts;
            this.born = w.born;
            this.started = w.started;
            this.expired = w.expired;
            this.time = w.time;
            this.creatorKey = w.creatorKey;
        }
    }

    @Override
    public void write(DataWriter d) {
        super.write(d);

        JSONArray array = new JSONArray();
        for(User user : this.trusted) {
            JSON userJson = new JSON();
            user.write(userJson);
            array.add(userJson);
        }

        this.owner.write(d);
        d.put("trusted", array);
        d.put("name", name);
        d.put("dscrptn", description);
        d.put("tpmsg", teleportMessage);
        d.put("item", item);
        d.put("public", isPublic);
        d.put("tpcosts", teleportCosts);
        d.put("born", born);
        d.put("started", started == born ? 0 : started);
        d.put("expired", expired);
        d.put("time", time);
        d.put("key", creatorKey);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.owner.read(d);
        this.trusted = new ArrayList<>();
        this.name = d.get("name");
        this.description = d.getList("dscrptn");
        this.teleportMessage = d.get("tpmsg");
        this.item = d.getItemBuilder("item");
        this.isPublic = d.getBoolean("public");
        this.born = d.getLong("born");
        this.started = d.getLong("started");
        this.expired = d.getLong("expired");
        this.time = d.getLong("time");
        this.creatorKey = d.get("key");

        JSONArray array = d.getList("trusted");
        if(array != null)
            for(Object o : array) {
                JSON data = new JSON((Map<?, ?>) o);
                User user = new User();
                user.read(data);
                trusted.add(user);
            }

        return super.read(d);
    }

    public PlayerWarp changeItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);
        this.item.setType(item.getType()).setData(builder.getData());
        return this;
    }

    public PlayerWarp resetItem() {
        this.item.setType(XMaterial.PLAYER_HEAD);
        if(this.owner.getPlayer() != null) this.item.setSkullId(this.owner.getPlayer());
        if(!this.item.getName().startsWith("§f")) this.item.setName("§f" + getName(true));
        return this;
    }

    public boolean isStandardItem() {
        return this.item.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && this.item.getSkullId().equals(WarpSystem.getInstance().getHeadManager().getSkinId(this.owner.id));
    }

    public PlayerWarp clone() {
        return new PlayerWarp(this);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }

    public boolean isOwner(UUID id) {
        return this.owner.getId().equals(id);
    }

    public List<User> getTrusted() {
        return trusted;
    }

    public boolean isTrusted(Player player) {
        return isTrusted(player.getUniqueId());
    }

    public boolean isTrusted(UUID id) {
        for(User user : this.trusted) {
            if(user.getId().equals(id)) return true;
        }

        return false;
    }

    public boolean canTeleport(Player player) {
        return isOwner(player) || isTrusted(player);
    }

    public String getName() {
        return name;
    }

    public String getName(boolean color) {
        if(name == null) return null;
        String s = ChatColor.translateAlternateColorCodes('&', name);
        return color ? s : ChatColor.stripColor(s);
    }

    public boolean equalsName(String name) {
        if(name == null) return false;
        return getName(false).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)));
    }

    public void setName(String name) {
        this.name = name;
        this.item.setName(getName(true));
    }

    public List<String> getDescription() {
        return description;
    }

    public String getTeleportMessage() {
        return teleportMessage;
    }

    public void setTeleportMessage(String teleportMessage) {
        this.teleportMessage = teleportMessage;
    }

    public ItemBuilder getItem() {
        return item;
    }

    public void setItem(ItemBuilder item) {
        this.item = item;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public PlayerWarp setPublic(boolean state) {
        isPublic = state;
        return this;
    }

    public double getTeleportCosts() {
        return teleportCosts;
    }

    public void setTeleportCosts(double teleportCosts) {
        this.teleportCosts = teleportCosts;
    }

    public long getBorn() {
        return born;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public long getTime() {
        return time;
    }

    public PlayerWarp setTime(long time) {
        this.time = time;
        return this;
    }

    public String getCreatorKey() {
        return creatorKey;
    }

    public static class User implements Serializable {
        private String jsonPrefix;
        private String name;
        private UUID id;

        public User() {
        }

        public User(Player player) {
            this.name = player.getName();
            this.id = player.getUniqueId();
        }

        protected User(String jsonPrefix) {
            this.jsonPrefix = jsonPrefix;
        }

        protected User(String jsonPrefix, String name, UUID id) {
            this.jsonPrefix = jsonPrefix;
            this.name = name;
            this.id = id;
        }

        @Override
        public boolean read(DataWriter d) {
            this.name = d.get(p() + "name");
            this.id = UUID.fromString(d.get(p() + "id"));

            return true;
        }

        @Override
        public void write(DataWriter d) {
            d.put(p() + "name", name);
            d.put(p() + "id", id.toString());
        }

        private String p() {
            return jsonPrefix == null ? "" : jsonPrefix + ".";
        }

        @Override
        public void destroy() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public Player getPlayer() {
            Player p = Bukkit.getPlayer(id);
            if(p != null && !p.getName().equals(name)) name = p.getName();
            return p;
        }
    }
}
