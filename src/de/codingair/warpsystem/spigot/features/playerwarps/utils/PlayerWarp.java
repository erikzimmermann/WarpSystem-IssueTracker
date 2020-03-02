package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.API;
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
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import java.util.*;

public class PlayerWarp extends FeatureObject {
    private User owner;
    private List<User> trusted;
    private List<Category> classes;

    private String name;
    private String teleportMessage;
    private ItemBuilder item;

    private boolean isPublic;
    private double teleportCosts;

    private long born;
    private long started;
    private long time;
    private String creatorKey = null;
    private boolean notify;

    public PlayerWarp() {
        super();
        owner = new User("owner");
        trusted = new ArrayList<>();
        classes = new ArrayList<>();
    }

    public PlayerWarp(Player player, String name) {
        this();
        this.name = name;
        owner.id = player.getUniqueId();
        owner.name = player.getName();

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

            if(this.trusted == null) this.trusted = new ArrayList<>(w.trusted);
            else {
                this.trusted.clear();
                this.trusted.addAll(w.trusted);
            }

            if(this.classes == null) this.classes = new ArrayList<>(w.classes);
            else {
                this.classes.clear();
                this.classes.addAll(w.classes);
            }

            this.name = w.name;
            this.teleportMessage = w.teleportMessage;
            this.item = w.item.clone();
            this.isPublic = w.isPublic;
            this.teleportCosts = w.teleportCosts;
            this.born = w.born;
            this.started = w.started;
            this.time = w.time;
            this.creatorKey = w.creatorKey;
            this.notify = w.notify;
        }
    }

    @Override
    public void write(DataWriter d) {
        super.write(d);

        JSONArray trustedMembers = new JSONArray();
        for(User user : this.trusted) {
            JSON userJson = new JSON();
            user.write(userJson);
            trustedMembers.add(userJson);
        }

        JSONArray classes = new JSONArray();
        for(Category c : this.classes) {
            classes.add(ChatColor.stripColor(c.getName()));
        }

        this.owner.write(d);
        d.put("trusted", trustedMembers);
        d.put("classes", classes);
        d.put("name", name);
        d.put("tpmsg", teleportMessage);
        d.put("item", item);
        d.put("public", isPublic);
        d.put("tpcosts", teleportCosts);
        d.put("born", born);
        d.put("started", started == born ? 0 : started);
        d.put("time", time);
        d.put("key", creatorKey);
        d.put("notify", notify);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.owner.read(d);

        if(this.trusted == null) this.trusted = new ArrayList<>();
        else this.trusted.clear();

        if(this.classes == null) this.classes = new ArrayList<>();
        else this.classes.clear();

        this.name = d.get("name");
        this.teleportMessage = d.get("tpmsg");
        this.item = d.getItemBuilder("item");
        this.isPublic = d.getBoolean("public");
        this.born = d.getLong("born");
        this.started = d.getLong("started");
        this.time = d.getLong("time");
        this.creatorKey = d.get("key");
        this.notify = d.getBoolean("notify");

        if(born > 0 && started == 0) started = born;

        JSONArray array = d.getList("trusted");
        if(array != null)
            for(Object o : array) {
                JSON data = new JSON((Map<?, ?>) o);
                User user = new User();
                user.read(data);
                trusted.add(user);
            }

        array = d.getList("classes");
        if(array != null)
            for(Object o : array) {
                String name = (String) o;
                Category c = PlayerWarpManager.getManager().getWarpClass(name);
                if(c != null) this.classes.add(c);
            }

        //check "force player heads"
        if(PlayerWarpManager.getManager().isForcePlayerHead()) resetItem();

        //check "custom teleport costs"
        if(!PlayerWarpManager.getManager().isCustomTeleportCosts()) this.teleportCosts = 0;

        return super.read(d);
    }

    public PlayerWarp changeItem(ItemBuilder item) {
        if(this.item == null) {
            return setItem(item);
        }

        this.item.setType(item.getType())
                .setData(item.getData())
                .setDurability(item.getDurability())
                .setAmount(item.getAmount())
                .setEnchantments(item.getEnchantments())
                .setSkullId(item.getSkullId())
                .setColor(item.getColor())
                .setCustomModel(item.getCustomModel())
                .setPotionData(item.getPotionData())
        ;
        return this;
    }

    public boolean isSameItem(ItemBuilder item) {
        if(this.item == null && item == null) return true;

        if(this.item != null && item != null) {
            return this.item.getType() == item.getType()
                    && this.item.getData() == item.getData()
                    && this.item.getDurability() == item.getDurability()
                    && this.item.getAmount() == item.getAmount()
                    && this.item.getEnchantments() == item.getEnchantments()
                    && Objects.equals(this.item.getSkullId(), item.getSkullId())
                    && this.item.getColor() == item.getColor()
                    && this.item.getCustomModel() == item.getCustomModel()
                    && this.item.getPotionData() == item.getPotionData();
        } else return false;
    }

    public PlayerWarp resetItem() {
        if(isStandardItem()) return this;
        return changeItem(getStandardItemBuilder());
    }

    private ItemBuilder getStandardItemBuilder() {
        ItemBuilder builder = new ItemBuilder(XMaterial.PLAYER_HEAD);

        String id = WarpSystem.getInstance().getHeadManager().getSkinId(owner.getId());
        if(id != null) builder.setSkullId(id);

        if(!builder.getName().startsWith("§f")) builder.setName("§f" + getName(true));
        if(this.item != null && this.item.getLore() != null && !this.item.getLore().isEmpty()) builder.setLore(this.item.getLore());

        return builder;
    }

    public boolean isStandardItem() {
        return this.item != null && this.item.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && Objects.equals(this.item.getSkullId(), WarpSystem.getInstance().getHeadManager().getSkinId(this.owner.id));
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
        this.item.setName("§f" + getName(true));
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

    public PlayerWarp setItem(ItemBuilder item) {
        this.item = item;
        return this;
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

    public boolean born() {
        if(this.born > 0) return false;
        this.born = System.currentTimeMillis();
        return true;
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

    public boolean isExpired() {
        return started > 0 && started + time < System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public long getLeftTime() {
        if(this.started == 0) return this.time;
        else return Math.max(started + time - System.currentTimeMillis(), 0);
    }

    public long getPassedTime() {
        if(this.started == 0) return 0;
        else return Math.min(System.currentTimeMillis() - started, time);
    }

    public PlayerWarp setTime(long time) {
        this.time = time;
        return this;
    }

    public String getCreatorKey() {
        return creatorKey;
    }

    public boolean isBeingEdited() {
        if(owner == null || owner.getPlayer() == null) return false;
        return API.getRemovable(owner.getPlayer(), PWEditor.class) != null;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isNotify() {
        return notify;
    }

    public long getExpireDate() {
        if(this.started == 0) return 0;
        return this.started + this.time;
    }

    public float getRefundFactor() {
        if(this.started == 0) return 1;
        else if(PlayerWarpManager.getManager().isInternalRefundFactor()) return ((float) (int) (((float) getLeftTime() / (float) (getExpireDate() - getBorn())) * 10000)) /10000;
        else return 1;
    }

    public List<Category> getClasses() {
        return classes;
    }

    public boolean hasClass(Category c) {
        return this.classes.contains(c);
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
