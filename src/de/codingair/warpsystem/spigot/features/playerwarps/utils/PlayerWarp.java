package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.money.Adapter;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.GlobalLocationAdapter;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PlayerWarpTeleportProcessPacket;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class PlayerWarp extends FeatureObject {
    private User owner;
    private List<User> trusted;
    private List<Category> classes;

    private boolean source = false;
    private String name;
    private List<String> description;
    private String teleportMessage;
    private ItemBuilder item;

    private boolean isPublic;
    private double teleportCosts;
    private byte inactiveSales;

    private long born;
    private long started;
    private long time;
    private String creatorKey = null;
    private boolean notify;

    public PlayerWarp() {
        super();
        owner = new User("owner");
        trusted = new ArrayList<>();
        description = new ArrayList<>();
        classes = new ArrayList<>();
    }

    public PlayerWarp(Player player, String name) {
        this();
        this.name = name;
        owner.id = WarpSystem.getInstance().getUUIDManager().get(player);
        owner.name = player.getName();

        resetItem();
        addAction(new WarpAction(new Destination(new GlobalLocationAdapter(WarpSystem.getInstance().getCurrentServer(), Location.getByLocation(player.getLocation())))));
    }

    private PlayerWarp(PlayerWarp warp) {
        super(warp);
        apply(warp);
    }

    @Override
    public FeatureObject perform(Player player) {
        TeleportOptions options = new TeleportOptions((Destination) null, this.name);

        options.addCallback(new Callback<Result>() {
            @Override
            public void accept(Result res) {
                if(res == Result.SUCCESS) {
                    PlayerWarpTeleportProcessPacket packet = PlayerWarpManager.getManager().checkBungeeCord() ? new PlayerWarpTeleportProcessPacket(name, owner.getId()) : null;
                    if(packet != null && !isOwner(player) && !isTrusted(player)) packet.setIncreasePerformed(true);

                    if(options.getFinalCosts(player).doubleValue() > 0 && !isOwner(player) && !isTrusted(player)) {
                        if(packet != null) packet.setIncreaseSales(true);
                        increaseInactiveSales();
                    }

                    if(packet != null) {
                        //update on BungeeCord
                        WarpSystem.getInstance().getDataHandler().send(packet);
                    }

                    PlayerWarpManager.getManager().updateGUIs();
                }
            }
        });

        if(this.teleportMessage != null) options.setMessage(Lang.getPrefix() + ChatColor.translateAlternateColorCodes('&', this.teleportMessage));

        if(isOwner(player) || isTrusted(player)) performed--;
        else {
            options.setCosts(this.teleportCosts);
        }

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

            if(this.description == null) this.description = new ArrayList<>(w.description);
            else {
                this.description.clear();
                this.description.addAll(w.description);
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
            this.inactiveSales = w.inactiveSales;
            this.born = w.born;
            this.started = w.started;
            this.time = w.time;
            this.creatorKey = w.creatorKey;
            this.notify = w.notify;
        }
    }

    public PlayerWarpData getData() {
        PlayerWarpData data = new PlayerWarpData();

        data.setName(this.name);
        data.setOwner(new PlayerWarpData.User(this.owner.jsonPrefix, this.owner.name, this.owner.id));

        List<PlayerWarpData.User> trusted = new ArrayList<>();
        for(User user : this.trusted) {
            trusted.add(new PlayerWarpData.User(user.jsonPrefix, user.name, user.id));
        }
        data.setTrusted(trusted);

        data.setType(this.item.getType().name());
        data.setSkullId(this.item.getSkullId());
        if(this.item.getColor() != null) data.setRGB(this.item.getColor().getColor().asRGB());
        data.setData(this.item.getData());

        data.setPublic(this.isPublic);
        data.setTeleportMessage(this.teleportMessage);
        data.setTeleportCosts(this.teleportCosts);
        data.setInactiveSales(this.inactiveSales);

        List<Byte> classes = new ArrayList<>();
        for(Category c : this.classes) {
            classes.add((byte) c.getId());
        }
        data.setClasses(classes);

        data.setDescription(new ArrayList<>(this.description));
        data.setBorn(this.born);
        data.setStarted(this.started);
        if(isTimeDependent()) data.setTime(this.time);
        data.setCreatorKey(this.creatorKey);
        data.setNotify(this.notify);
        data.setPerformed(this.performed);

        Destination d = getAction(WarpAction.class).getValue();
        GlobalLocationAdapter a = (GlobalLocationAdapter) d.getAdapter();
        data.setServer(a.getServer() == null ? WarpSystem.getInstance().getCurrentServer() : a.getServer());

        Location l = a.getLocation();
        data.setWorld(l.getWorldName());
        data.setX(l.getX());
        data.setY(l.getY());
        data.setZ(l.getZ());
        data.setYaw(l.getYaw());
        data.setPitch(l.getPitch());

        return data;
    }

    public void setData(PlayerWarpData d) {
        if(d.name != null) this.name = d.name;
        if(d.owner != null) this.owner = new User("owner", d.owner.getName(), d.owner.getId());

        if(d.trusted != null) {
            if(this.trusted == null) this.trusted = new ArrayList<>();
            else this.trusted.clear();

            for(PlayerWarpData.User user : d.trusted) {
                trusted.add(new User(null, user.getName(), user.getId()));
            }
        }

        if(this.item == null) this.item = new ItemBuilder();
        if(d.data != null) this.item.setData(d.data);
        if(d.type != null) {
            Optional<XMaterial> m = XMaterial.matchXMaterial(d.type, d.data == null || Version.get().isBiggerThan(Version.v1_12) ? 0 : d.data);

            if(!m.isPresent()) {
                throw new IllegalArgumentException("Error at loading PlayerWarp(Owner-Name=" + (d.owner == null ? owner.getName() : d.owner.getName()) + "; Warp-Name=" + (d.name == null ? name : d.name) + "). Material is null: (" + d.type + ", " + d.data + ")");
            } else {
                this.item.setType(m.get().parseMaterial(true, true, XMaterial.STONE.parseMaterial()));
                this.item.setData(m.get().getData());
            }
        } else if(d.data != null) {
            Optional<XMaterial> m = XMaterial.matchXMaterial(item.getType().name(), Version.get().isBiggerThan(Version.v1_12) ? 0 : d.data);

            if(!m.isPresent()) {
                throw new IllegalArgumentException("Error at loading PlayerWarp(Owner-Name=" + (d.owner == null ? owner.getName() : d.owner.getName()) + "; Warp-Name=" + (d.name == null ? name : d.name) + "). Material is null: (" + d.type + ", " + d.data + ")");
            } else {
                this.item.setType(m.get().parseMaterial(true, true, XMaterial.STONE.parseMaterial()));
                this.item.setData(m.get().getData());
            }
        }

        if(d.skullId != null) this.item.setSkullId(d.skullId);
        if(d.rgb != null && d.rgb > 0) this.item.setColor(DyeColor.getByColor(Color.fromRGB(d.rgb)));

        if(d.isPublic != null) this.isPublic = d.isPublic;
        if(d.teleportMessage != null) this.teleportMessage = d.teleportMessage;
        if(d.teleportCosts != null) this.teleportCosts = d.teleportCosts;
        if(d.getInactiveSales() != null) this.inactiveSales = d.inactiveSales;

        if(d.classes != null) {
            if(this.classes == null) this.classes = new ArrayList<>();
            else this.classes.clear();

            for(Byte b : d.classes) {
                Category c = PlayerWarpManager.getManager().getWarpClass(b);
                if(c != null) classes.add(c);
            }
        }

        if(d.description != null) {
            if(this.description == null) this.description = new ArrayList<>();
            else this.description.clear();

            this.description.addAll(d.description);
        }
        if(d.born != null) this.born = d.born;
        if(d.started != null) this.started = d.started;
        if(d.time != null) this.time = d.time;
        if(d.creatorKey != null) this.creatorKey = d.creatorKey;
        if(d.notify != null) this.notify = d.notify;
        if(d.performed != null) this.performed = d.performed;

        boolean createDestination = getAction(Action.WARP) == null;
        GlobalLocationAdapter a = createDestination ? new GlobalLocationAdapter(null, new Location()) : (GlobalLocationAdapter) ((Destination) getAction(Action.WARP).getValue()).getAdapter();
        Location l = a.getLocation();

        if(d.server != null) a.setServer(d.server);
        if(d.world != null) l.setWorldName(d.world);
        if(d.x != null) l.setX(d.x);
        if(d.y != null) l.setY(d.y);
        if(d.z != null) l.setZ(d.z);
        if(d.yaw != null) l.setYaw(d.yaw);
        if(d.pitch != null) l.setPitch(d.pitch);

        if(createDestination) addAction(new WarpAction(new Destination(a)));
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
        d.put("name", name);
        d.put("description", description);
        d.put("tpmsg", teleportMessage);
        d.put("item", item);
        d.put("public", isPublic);
        d.put("tpcosts", teleportCosts);
        d.put("sales", inactiveSales);
        d.put("born", born);
        d.put("started", started == born ? 0 : started);
        d.put("time", time);
        d.put("key", creatorKey);
        d.put("notify", notify);
        d.put("trusted", trustedMembers);
        d.put("classes", classes);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        boolean success = super.read(d);
        if(this.trusted == null) this.trusted = new ArrayList<>();
        else this.trusted.clear();

        if(this.classes == null) this.classes = new ArrayList<>();
        else this.classes.clear();

        this.owner.read(d);
        this.name = d.getString("name").replace(".", "_");
        this.description = d.getList("description");
        this.teleportMessage = d.getString("tpmsg");
        this.item = d.getItemBuilder("item");

        if(!XMaterial.isNewVersion() && this.item.getType() == XMaterial.PLAYER_HEAD.parseMaterial(false, false)) {
            this.item.setType(XMaterial.PLAYER_HEAD.parseMaterial(true, false));
            this.item.setDurability(XMaterial.PLAYER_HEAD.getData());
            this.item.setData(XMaterial.PLAYER_HEAD.getData());
        }

        this.isPublic = d.getBoolean("public");
        this.teleportCosts = d.getDouble("tpcosts");
        this.inactiveSales = d.getByte("sales");
        this.born = d.getLong("born");
        this.started = d.getLong("started");
        this.time = d.getLong("time");
        this.creatorKey = d.getString("key");
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

        resetItem();

        //check "custom teleport costs"
        if(!PlayerWarpManager.getManager().isCustomTeleportCosts()) this.teleportCosts = 0;
        return success;
    }

    @Override
    public void destroy() {
        super.destroy();
        if(this.trusted != null) this.trusted.clear();
        if(this.classes != null) this.classes.clear();
        if(this.description != null) this.description.clear();
    }

    public PlayerWarp changeItem(ItemBuilder item) {
        if(this.item == null) {
            return setItem(item);
        }

        this.item.setType(item.getType())
                .setData(item.getData())
                .setDurability(item.getDurability())
                .setAmount(item.getAmount())
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

    public void resetItem() {
        if(isStandardItem()) return;
        changeItem(getStandardItemBuilder());
    }

    private ItemBuilder getStandardItemBuilder() {
        ItemBuilder builder = new ItemBuilder(XMaterial.PLAYER_HEAD);

        String id = WarpSystem.getInstance().getHeadManager().getSkinId(owner.getId());
        if(id != null) builder.setSkullId(id);

        return builder;
    }

    public boolean isStandardItem() {
        return this.item != null && this.item.getType() == XMaterial.PLAYER_HEAD.parseMaterial(true, false) && Objects.equals(this.item.getSkullId(), WarpSystem.getInstance().getHeadManager().getSkinId(this.owner.id));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        PlayerWarp warp = (PlayerWarp) o;
        return owner.equals(warp.owner) &&
                name.equals(warp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
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
        return player != null && isOwner(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public boolean isOwner(UUID id) {
        return this.owner.getId().equals(id);
    }

    public List<User> getTrusted() {
        return trusted;
    }

    public boolean isTrusted(Player player) {
        return isTrusted(WarpSystem.getInstance().getUUIDManager().get(player));
    }

    public boolean isTrusted(UUID id) {
        for(User user : this.trusted) {
            if(user.getId().equals(id)) return true;
        }

        return false;
    }

    public boolean canTeleport(Player player) {
        return isPublic || isOwner(player) || isTrusted(player);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.item.setName("§f" + getName(true));
    }

    public String getName(boolean color) {
        if(name == null) return null;
        String s = ChatColor.translateAlternateColorCodes('&', name);
        return color ? s : ChatColor.stripColor(s);
    }

    public boolean equalsName(String name) {
        if(name == null) return false;
        name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name.replace(" ", "_")));
        return getName(false).equalsIgnoreCase(name);
    }

    public String getTeleportMessage() {
        return teleportMessage;
    }

    public void setTeleportMessage(String teleportMessage) {
        this.teleportMessage = teleportMessage;
    }

    public ItemBuilder getItem() {
        return getItem(null);
    }

    public PlayerWarp setItem(ItemBuilder item) {
        this.item = item;
        this.item.removeEnchantments();
        return this;
    }

    public ItemBuilder getItem(String highlight) {
        ItemBuilder b = item.clone()
                .setName(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Name") + ": §f" + (highlight == null ? name : ChatColor.highlight(name, highlight, "§e§n", "§f", true)).replace("_", " "))
                .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Description") + ":" + (description.isEmpty() ? " §c-" : ""))
                .addLore(getPreparedDescription());

        b.addLore("");
        b.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Owner") + ": §7" + owner.getName());

        b.setHideStandardLore(true);
        b.setHideEnchantments(true);

        if(teleportCosts > 0) b.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Costs") + ": §7" + getCutTeleportCosts() + " " + Lang.get("Coins"));
        if(isPublic) b.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Teleports") + ": §7" + getPerformed());

        return b;
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

    public Number getCutTeleportCosts() {
        Number n = teleportCosts;
        if(n.intValue() == teleportCosts) return n.intValue();
        return teleportCosts;
    }

    public void born() {
        if(this.born > 0) return;
        this.born = System.currentTimeMillis();
    }

    public long getBorn() {
        return born;
    }

    public void setBorn(long born) {
        this.born = born;
    }

    public long getStarted() {
        return started;
    }

    public PlayerWarp setStarted(long started) {
        this.started = started;
        return this;
    }

    public boolean isExpired() {
        return started > 0 && time > 0 && started + time < System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public boolean isTimeDependent() {
        return this.time > 0;
    }

    public PlayerWarp setTime(long time) {
        this.time = time;
        return this;
    }

    public long getLeftTime() {
        if(this.started == 0) return this.time;
        else return Math.max(started + time - System.currentTimeMillis(), 0);
    }

    public long getPassedTime() {
        if(this.started == 0) return 0;
        else return Math.min(System.currentTimeMillis() - started, time);
    }

    public String getCreatorKey() {
        return creatorKey;
    }

    public void setCreatorKey(String creatorKey) {
        this.creatorKey = creatorKey;
    }

    public boolean isBeingEdited() {
        if(owner == null || owner.getPlayer() == null) return false;
        return API.getRemovable(owner.getPlayer(), PWEditor.class) != null;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public long getExpireDate() {
        if(this.started == 0) return 0;
        return this.started + this.time;
    }

    public float getRefundFactor() {
        if(this.started == 0) return 1;
        else if(PlayerWarpManager.getManager().isInternalRefundFactor()) return ((float) (int) (((float) getLeftTime() / (float) (getExpireDate() - getBorn())) * 10000)) / 10000;
        else return 1;
    }

    public List<Category> getClasses() {
        return classes;
    }

    public boolean hasClass(Category c) {
        return this.classes.contains(c);
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public void addDescription(String line) {
        this.description.add(line);
    }

    private List<String> getPreparedDescription() {
        if(description.isEmpty()) return null;

        List<String> description = new ArrayList<>();

        for(String s : this.description) {
            description.add("§f" + s);
        }

        return description;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    public void increasePerformed() {
        performed++;
    }

    public int getInactiveSales() {
        return ((int) inactiveSales) & 0xFF;
    }

    public void setInactiveSales(int inactiveSales) {
        this.inactiveSales = (byte) Math.min(inactiveSales, 256);
    }

    public void resetInactiveSales() {
        inactiveSales = 0;
    }

    public void increaseInactiveSales() {
        inactiveSales = (byte) (getInactiveSales() + 1);
    }

    public double collectInactiveSales(Player player) {
        double money = getInactiveSales() * teleportCosts;

        Adapter a = Bank.adapter();
        if(a != null) {
            resetInactiveSales();

            if(PlayerWarpManager.getManager().checkBungeeCord()) {
                PlayerWarpTeleportProcessPacket packet = new PlayerWarpTeleportProcessPacket(name, owner.getId(), false, true, false);
                WarpSystem.getInstance().getDataHandler().send(packet);
            }

            a.deposit(player, money);
        } else return 0;

        return money;
    }

    public static class User implements Serializable, de.codingair.warpsystem.transfer.serializeable.Serializable {
        private final String jsonPrefix;
        private String name;
        private UUID id;

        public User() {
            this(null, null, null);
        }

        public User(Player player) {
            this(player.getName(), WarpSystem.getInstance().getUUIDManager().get(player));
        }

        public User(String name, UUID id) {
            this(null, name, id);
        }

        protected User(String jsonPrefix) {
            this(jsonPrefix, null, null);
        }

        protected User(String jsonPrefix, String name, UUID id) {
            this.jsonPrefix = jsonPrefix;
            this.name = name;
            this.id = id;
        }

        @Override
        public void write(DataOutputStream o) throws IOException {
            o.writeUTF(this.name);
            o.writeLong(this.id.getMostSignificantBits());
            o.writeLong(this.id.getLeastSignificantBits());
        }

        @Override
        public void read(DataInputStream i) throws IOException {
            this.name = i.readUTF();
            this.id = new UUID(i.readLong(), i.readLong());
        }

        @Override
        public boolean read(DataWriter d) {
            this.name = d.getString(p() + "name");
            this.id = UUID.fromString(d.getString(p() + "id"));
            return true;
        }

        @Override
        public void write(DataWriter d) {
            d.put(p() + "name", this.name);
            d.put(p() + "id", id.toString());
        }

        private String p() {
            return jsonPrefix == null ? "" : jsonPrefix + ".";
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return id.equals(user.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Player getPlayer() {
            Player p = Bukkit.getPlayer(id);
            if(p != null && !p.getName().equals(this.name)) this.name = p.getName();
            return p;
        }
    }
}
