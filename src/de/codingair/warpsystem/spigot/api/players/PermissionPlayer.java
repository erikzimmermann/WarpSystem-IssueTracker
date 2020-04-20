package de.codingair.warpsystem.spigot.api.players;

import de.codingair.codingapi.server.reflections.IReflection;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

public class PermissionPlayer implements Player {
    private Player player;

    public PermissionPlayer(Player player) {
        this.player = player;
    }

    public void sendBlockChange(Location location, int i, byte b) {

    }

    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public int _INVALID_getLastDamage() {
        return 0;
    }

    public void _INVALID_setLastDamage(int i) {

    }

    public void _INVALID_damage(int i) {

    }

    public void _INVALID_damage(int i, Entity entity) {

    }

    public int _INVALID_getHealth() {
        return 0;
    }

    public void _INVALID_setHealth(int i) {

    }

    public int _INVALID_getMaxHealth() {
        return 0;
    }

    public void _INVALID_setMaxHealth(int i) {

    }

    public String getPlayerListHeader() {
        return null;
    }

    public void setPlayerListHeader(String s) {

    }

    public String getPlayerListFooter() {
        return null;
    }

    public void setPlayerListFooter(String s) {

    }

    public void setPlayerListHeaderFooter(String s, String s1) {

    }

    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1) {

    }

    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1) {

    }

    public void stopSound(Sound sound, SoundCategory soundCategory) {

    }

    public void stopSound(String s, SoundCategory soundCategory) {

    }

    public void sendBlockChange(Location location, BlockData blockData) {

    }

    public void hidePlayer(Plugin plugin, Player player) {

    }

    public void showPlayer(Plugin plugin, Player player) {

    }

    public void setResourcePack(String s, byte[] bytes) {

    }

    public void sendTitle(String s, String s1, int i, int i1, int i2) {

    }

    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return null;
    }

    public int getClientViewDistance() {
        return 0;
    }

    public String getLocale() {
        return null;
    }

    public void updateCommands() {

    }

    public void openBook(ItemStack itemStack) {

    }

    public InventoryView openMerchant(Merchant merchant, boolean b) {
        return null;
    }

    public boolean hasCooldown(Material material) {
        return false;
    }

    public int getCooldown(Material material) {
        return 0;
    }

    public void setCooldown(Material material, int i) {

    }

    public boolean sleep(Location location, boolean b) {
        return false;
    }

    public void wakeup(boolean b) {

    }

    public Location getBedLocation() {
        return null;
    }

    public boolean isHandRaised() {
        return false;
    }

    public boolean discoverRecipe(NamespacedKey namespacedKey) {
        return false;
    }

    public int discoverRecipes(Collection<NamespacedKey> collection) {
        return 0;
    }

    public boolean undiscoverRecipe(NamespacedKey namespacedKey) {
        return false;
    }

    public int undiscoverRecipes(Collection<NamespacedKey> collection) {
        return 0;
    }

    public Entity getShoulderEntityLeft() {
        return null;
    }

    public void setShoulderEntityLeft(Entity entity) {

    }

    public Entity getShoulderEntityRight() {
        return null;
    }

    public void setShoulderEntityRight(Entity entity) {

    }

    public Block getTargetBlockExact(int i) {
        return null;
    }

    public Block getTargetBlockExact(int i, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    public RayTraceResult rayTraceBlocks(double v) {
        return null;
    }

    public RayTraceResult rayTraceBlocks(double v, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return null;
    }

    public boolean isSwimming() {
        return false;
    }

    public void setSwimming(boolean b) {

    }

    public boolean isRiptiding() {
        return false;
    }

    public double getHeight() {
        return 0;
    }

    public double getWidth() {
        return 0;
    }

    public BoundingBox getBoundingBox() {
        return null;
    }

    public void setRotation(float v, float v1) {

    }

    public boolean isPersistent() {
        return false;
    }

    public void setPersistent(boolean b) {

    }

    public List<Entity> getPassengers() {
        return null;
    }

    public boolean addPassenger(Entity entity) {
        return false;
    }

    public boolean removePassenger(Entity entity) {
        return false;
    }

    public int getPortalCooldown() {
        return 0;
    }

    public void setPortalCooldown(int i) {

    }

    public Set<String> getScoreboardTags() {
        return null;
    }

    public boolean addScoreboardTag(String s) {
        return false;
    }

    public boolean removeScoreboardTag(String s) {
        return false;
    }

    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    public BlockFace getFacing() {
        return null;
    }

    public Pose getPose() {
        return null;
    }

    public PersistentDataContainer getPersistentDataContainer() {
        return null;
    }

    public String getDisplayName() {
        return this.player.getDisplayName();
    }

    public void setDisplayName(String s) {

    }

    public String getPlayerListName() {
        return this.player.getPlayerListName();
    }

    public void setPlayerListName(String s) {

    }

    public Location getCompassTarget() {
        return this.player.getCompassTarget();
    }

    public void setCompassTarget(Location location) {

    }

    public InetSocketAddress getAddress() {
        return this.player.getAddress();
    }

    public void sendRawMessage(String s) {

    }

    public void kickPlayer(String s) {

    }

    public void chat(String s) {

    }

    public boolean performCommand(String s) {
        return false;
    }

    public boolean isSneaking() {
        return this.player.isSneaking();
    }

    public void setSneaking(boolean b) {

    }

    public boolean isSprinting() {
        return this.player.isSprinting();
    }

    public void setSprinting(boolean b) {

    }

    public void saveData() {

    }

    public void loadData() {

    }

    public boolean isSleepingIgnored() {
        return this.player.isSleepingIgnored();
    }

    public void setSleepingIgnored(boolean b) {

    }

    public void playNote(Location location, byte b, byte b1) {

    }

    public void playNote(Location location, Instrument instrument, Note note) {

    }

    public void playSound(Location location, Sound sound, float v, float v1) {

    }

    public void playSound(Location location, String s, float v, float v1) {

    }

    public void stopSound(Sound sound) {

    }

    public void stopSound(String s) {

    }

    public void playEffect(Location location, Effect effect, int i) {

    }

    public <T> void playEffect(Location location, Effect effect, T t) {

    }

    public void sendBlockChange(Location location, Material material, byte b) {

    }

    public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes) {
        return false;
    }

    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {

    }

    public void sendSignChange(Location location, String[] strings, DyeColor dyeColor) throws IllegalArgumentException {

    }

    public void sendMap(MapView mapView) {

    }

    public void updateInventory() {

    }

    public void awardAchievement(Achievement achievement) {

    }

    public void removeAchievement(Achievement achievement) {

    }

    public boolean hasAchievement(Achievement achievement) {
        return false;
    }

    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {

    }

    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return 0;
    }

    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return 0;
    }

    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return 0;
    }

    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {

    }

    public void setStatistic(Statistic statistic, EntityType entityType, int i) {

    }

    public void setPlayerTime(long l, boolean b) {

    }

    public long getPlayerTime() {
        return 0;
    }

    public long getPlayerTimeOffset() {
        return 0;
    }

    public boolean isPlayerTimeRelative() {
        return this.player.isPlayerTimeRelative();
    }

    public void resetPlayerTime() {

    }

    public WeatherType getPlayerWeather() {
        return this.player.getPlayerWeather();
    }

    public void setPlayerWeather(WeatherType weatherType) {

    }

    public void resetPlayerWeather() {

    }

    public void giveExp(int i) {

    }

    public void giveExpLevels(int i) {

    }

    public float getExp() {
        return 0;
    }

    public void setExp(float v) {

    }

    public int getLevel() {
        return 0;
    }

    public void setLevel(int i) {

    }

    public int getTotalExperience() {
        return 0;
    }

    public void setTotalExperience(int i) {

    }

    public void sendExperienceChange(float v) {

    }

    public void sendExperienceChange(float v, int i) {

    }

    public float getExhaustion() {
        return 0;
    }

    public void setExhaustion(float v) {

    }

    public float getSaturation() {
        return 0;
    }

    public void setSaturation(float v) {

    }

    public int getFoodLevel() {
        return 0;
    }

    public void setFoodLevel(int i) {

    }

    public Location getBedSpawnLocation() {
        return this.player.getCompassTarget();
    }

    public void setBedSpawnLocation(Location location) {

    }

    public void setBedSpawnLocation(Location location, boolean b) {

    }

    public boolean getAllowFlight() {
        return this.player.getAllowFlight();
    }

    public void setAllowFlight(boolean b) {

    }

    public void hidePlayer(Player player) {

    }

    public void showPlayer(Player player) {

    }

    public boolean canSee(Player player) {
        return this.player.canSee(player);
    }

    public boolean isOnGround() {
        return this.player.isOnGround();
    }

    public boolean isFlying() {
        return this.player.isFlying();
    }

    public void setFlying(boolean b) {

    }

    public float getFlySpeed() {
        return 0;
    }

    public void setFlySpeed(float v) throws IllegalArgumentException {

    }

    public float getWalkSpeed() {
        return 0;
    }

    public void setWalkSpeed(float v) throws IllegalArgumentException {

    }

    public void setTexturePack(String s) {

    }

    public void setResourcePack(String s) {

    }

    public Scoreboard getScoreboard() {
        return this.player.getScoreboard();
    }

    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {

    }

    public boolean isHealthScaled() {
        return this.player.isHealthScaled();
    }

    public void setHealthScaled(boolean b) {

    }

    public double getHealthScale() {
        return 0;
    }

    public void setHealthScale(double v) throws IllegalArgumentException {

    }

    public Entity getSpectatorTarget() {
        return this.player.getSpectatorTarget();
    }

    public void setSpectatorTarget(Entity entity) {

    }

    public void sendTitle(String s, String s1) {

    }

    public void resetTitle() {

    }

    public void spawnParticle(Particle particle, Location location, int i) {

    }

    public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {

    }

    public <T> void spawnParticle(Particle particle, Location location, int i, T t) {

    }

    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {

    }

    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {

    }

    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {

    }

    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {

    }

    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {

    }

    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {

    }

    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {

    }

    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {

    }

    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {

    }

    public Spigot spigot() {
        return this.player.spigot();
    }

    public boolean isOnline() {
        return this.player.isOnline();
    }

    public boolean isBanned() {
        return this.player.isBanned();
    }

    public void setBanned(boolean b) {

    }

    public boolean isWhitelisted() {
        return this.player.isWhitelisted();
    }

    public void setWhitelisted(boolean b) {

    }

    public Player getPlayer() {
        return this;
    }

    public long getFirstPlayed() {
        return 0;
    }

    public long getLastPlayed() {
        return 0;
    }

    public boolean hasPlayedBefore() {
        return this.player.hasPlayedBefore();
    }

    public Map<String, Object> serialize() {
        return this.player.serialize();
    }

    public boolean isConversing() {
        return this.player.isConversing();
    }

    public void acceptConversationInput(String s) {

    }

    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    public void abandonConversation(Conversation conversation) {

    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {

    }

    public String getName() {
        return this.player.getName();
    }

    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    public Inventory getEnderChest() {
        return this.player.getEnderChest();
    }

    public MainHand getMainHand() {
        IReflection.MethodAccessor getMainHand = IReflection.getMethod(Player.class, "getMainHand", MainHand.class, new Class[0]);
        return (MainHand) getMainHand.invoke(player);
    }

    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return false;
    }

    public InventoryView getOpenInventory() {
        return null;
    }

    public InventoryView openInventory(Inventory inventory) {
        return null;
    }

    public InventoryView openWorkbench(Location location, boolean b) {
        return null;
    }

    public InventoryView openEnchanting(Location location, boolean b) {
        return null;
    }

    public void openInventory(InventoryView inventoryView) {

    }

    public InventoryView openMerchant(Villager villager, boolean b) {
        return null;
    }

    public void closeInventory() {

    }

    public ItemStack getItemInHand() {
        return this.player.getItemInHand();
    }

    public void setItemInHand(ItemStack itemStack) {

    }

    public ItemStack getItemOnCursor() {
        return this.player.getItemOnCursor();
    }

    public void setItemOnCursor(ItemStack itemStack) {

    }

    public boolean isSleeping() {
        return this.player.isSleeping();
    }

    public int getSleepTicks() {
        return 0;
    }

    public GameMode getGameMode() {
        return this.player.getGameMode();
    }

    public void setGameMode(GameMode gameMode) {

    }

    public boolean isBlocking() {
        return this.player.isBlocking();
    }

    public int getExpToLevel() {
        return 0;
    }

    public double getEyeHeight() {
        return 0;
    }

    public double getEyeHeight(boolean b) {
        return 0;
    }

    public Location getEyeLocation() {
        return this.player.getCompassTarget();
    }

    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return this.player.getLineOfSight(set, i);
    }

    public Block getTargetBlock(Set<Material> set, int i) {
        return this.player.getTargetBlock(set, i);
    }

    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return this.player.getLastTwoTargetBlocks(set, i);
    }

    public Egg throwEgg() {
        return null;
    }

    public Snowball throwSnowball() {
        return null;
    }

    public Arrow shootArrow() {
        return null;
    }

    public int getRemainingAir() {
        return 0;
    }

    public void setRemainingAir(int i) {

    }

    public int getMaximumAir() {
        return 0;
    }

    public void setMaximumAir(int i) {

    }

    public int getMaximumNoDamageTicks() {
        return 0;
    }

    public void setMaximumNoDamageTicks(int i) {

    }

    public double getLastDamage() {
        return 0;
    }

    public void setLastDamage(double v) {

    }

    public int getNoDamageTicks() {
        return 0;
    }

    public void setNoDamageTicks(int i) {

    }

    public Player getKiller() {
        return null;
    }

    public boolean addPotionEffect(PotionEffect potionEffect) {
        return false;
    }

    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return false;
    }

    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return false;
    }

    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return false;
    }

    public void removePotionEffect(PotionEffectType potionEffectType) {

    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.player.getActivePotionEffects();
    }

    public boolean hasLineOfSight(Entity entity) {
        return this.player.hasLineOfSight(entity);
    }

    public boolean getRemoveWhenFarAway() {
        return this.player.getRemoveWhenFarAway();
    }

    public void setRemoveWhenFarAway(boolean b) {

    }

    public EntityEquipment getEquipment() {
        return this.player.getEquipment();
    }

    public boolean getCanPickupItems() {
        return this.player.getCanPickupItems();
    }

    public void setCanPickupItems(boolean b) {

    }

    public boolean isLeashed() {
        return this.player.isLeashed();
    }

    public Entity getLeashHolder() throws IllegalStateException {
        return this.player.getLeashHolder();
    }

    public boolean setLeashHolder(Entity entity) {
        return this.player.setLeashHolder(entity);
    }

    public boolean isGliding() {
        return false;
    }

    public void setGliding(boolean b) {

    }

    public void setAI(boolean b) {

    }

    public boolean hasAI() {
        return false;
    }

    public void attack(Entity entity) {

    }

    public void swingMainHand() {

    }

    public void swingOffHand() {

    }

    public boolean isCollidable() {
        return false;
    }

    public <T> T getMemory(MemoryKey<T> memoryKey) {
        return null;
    }

    public <T> void setMemory(MemoryKey<T> memoryKey, T t) {

    }

    public void setCollidable(boolean b) {

    }

    public AttributeInstance getAttribute(Attribute attribute) {
        return null;
    }

    public void damage(double v) {

    }

    public void damage(double v, Entity entity) {

    }

    public double getHealth() {
        return 0;
    }

    public void setHealth(double v) {

    }

    public double getAbsorptionAmount() {
        return 0;
    }

    public void setAbsorptionAmount(double v) {

    }

    public double getMaxHealth() {
        return 0;
    }

    public void setMaxHealth(double v) {

    }

    public void resetMaxHealth() {

    }

    public Location getLocation() {
        return this.player.getCompassTarget();
    }

    public Location getLocation(Location location) {
        return this.player.getCompassTarget();
    }

    public Vector getVelocity() {
        return this.player.getVelocity();
    }

    public void setVelocity(Vector vector) {

    }

    public World getWorld() {
        return this.player.getWorld();
    }

    public boolean teleport(Location location) {
        return false;
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return false;
    }

    public boolean teleport(Entity entity) {
        return false;
    }

    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return false;
    }

    public List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return this.player.getNearbyEntities(v, v1, v2);
    }

    public int getEntityId() {
        return 0;
    }

    public int getFireTicks() {
        return 0;
    }

    public void setFireTicks(int i) {

    }

    public int getMaxFireTicks() {
        return 0;
    }

    public void remove() {

    }

    public boolean isDead() {
        return this.player.isDead();
    }

    public boolean isValid() {
        return this.player.isValid();
    }

    public Server getServer() {
        return this.player.getServer();
    }

    public Entity getPassenger() {
        return this.player.getPassenger();
    }

    public boolean setPassenger(Entity entity) {
        return false;
    }

    public boolean isEmpty() {
        return this.player.isEmpty();
    }

    public boolean eject() {
        return false;
    }

    public float getFallDistance() {
        return 0;
    }

    public void setFallDistance(float v) {

    }

    public EntityDamageEvent getLastDamageCause() {
        return this.player.getLastDamageCause();
    }

    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {

    }

    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    public int getTicksLived() {
        return 0;
    }

    public void setTicksLived(int i) {

    }

    public void playEffect(EntityEffect entityEffect) {

    }

    public EntityType getType() {
        return this.player.getType();
    }

    public boolean isInsideVehicle() {
        return this.player.isInsideVehicle();
    }

    public boolean leaveVehicle() {
        return this.player.leaveVehicle();
    }

    public Entity getVehicle() {
        return this.player.getVehicle();
    }

    public String getCustomName() {
        return this.player.getCustomName();
    }

    public void setCustomName(String s) {

    }

    public boolean isCustomNameVisible() {
        return this.player.isCustomNameVisible();
    }

    public void setCustomNameVisible(boolean b) {

    }

    public boolean isGlowing() {
        return false;
    }

    public void setGlowing(boolean b) {

    }

    public boolean isInvulnerable() {
        return false;
    }

    public void setInvulnerable(boolean b) {

    }

    public boolean isSilent() {
        return false;
    }

    public void setSilent(boolean b) {

    }

    public boolean hasGravity() {
        return true;
    }

    public void setGravity(boolean b) {

    }

    public void sendMessage(String s) {

    }

    public void sendMessage(String[] strings) {

    }

    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    public List<MetadataValue> getMetadata(String s) {
        return this.player.getMetadata(s);
    }

    public boolean hasMetadata(String s) {
        return this.player.hasMetadata(s);
    }

    public void removeMetadata(String s, Plugin plugin) {

    }

    public boolean isPermissionSet(String s) {
        return this.player.isPermissionSet(s);
    }

    public boolean isPermissionSet(Permission permission) {
        return this.player.isPermissionSet(permission);
    }

    public boolean hasPermission(String s) {
        return this.player.hasPermission(s);
    }

    public boolean hasPermission(Permission permission) {
        return this.player.hasPermission(permission);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return this.player.addAttachment(plugin, s, b);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.player.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return this.player.addAttachment(plugin, s, b, i);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return this.player.addAttachment(plugin, i);
    }

    public void removeAttachment(PermissionAttachment permissionAttachment) {
        this.player.removeAttachment(permissionAttachment);
    }

    public void recalculatePermissions() {
        this.player.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.player.getEffectivePermissions();
    }

    public boolean isOp() {
        return this.player.isOp();
    }

    public void setOp(boolean b) {

    }

    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {

    }

    public Set<String> getListeningPluginChannels() {
        return this.player.getListeningPluginChannels();
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return null;
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return null;
    }
}
