package me.gleeming.hologram;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.gleeming.hologram.listener.HologramListener;
import me.gleeming.hologram.reflection.impl.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class ItemHologram {

    // All the item holograms currently created
    @Getter private static final List<ItemHologram> holograms = new ArrayList<>();

    // Current objects
    private final REntityItem entityItem;
    private final REntityArmorStand armorStand;

    // Location hologram starts at
    private final Location location;

    // Information about who is supposed to see it
    private final List<UUID> canSee = new ArrayList<>();
    private boolean showToAll = false;

    // List of players that can currently see the item
    private final List<UUID> viewing = new ArrayList<>();

    public ItemHologram(Location location, ItemStack itemStack) {
        this.location = location;

        this.entityItem = new REntityItem(location, itemStack);
        this.armorStand = new REntityArmorStand(location);

        armorStand.setCustomNameVisible(false);
        holograms.add(this);

        entityItem.build();
        armorStand.build();
    }

    /**
     * Updates the itemstack of the hologram
     */
    public void updateItemStack(ItemStack itemStack) {
        entityItem.updateItemStack(itemStack);
        new RPacketMetadata(entityItem.getEntityId(), entityItem.getDataWatcher(), true);
    }

    /**
     * Makes the armor stand visible to all players
     */
    public void showToAll() {
        showToAll = true;
        viewing.clear();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getLocation().distance(location) <= Hologram.VIEW_DISTANCE)
                .forEach(this::create);
    }

    /**
     * Hides the armor stand from all players
     */
    public void hideFromAll() {
        showToAll = false;
        viewing.stream().map(Bukkit::getPlayer).forEach(this::destroy);
        canSee.stream().map(Bukkit::getPlayer).forEach(this::create);
    }

    /**
     * Shows the armor stand to a player
     * @param player Player
     */
    public void show(Player player) {
        canSee.add(player.getUniqueId());
        if(player.getLocation().distance(location) <= Hologram.VIEW_DISTANCE) create(player);
    }

    /**
     * Hides the armor stand from a player
     * @param player Player
     */
    public void hide(Player player) {
        canSee.remove(player.getUniqueId());
        destroy(player);
    }

    /**
     * Creates the armor stand for the player
     * @param player Player
     */
    public void create(Player player) {
        viewing.add(player.getUniqueId());

        new RPacketSpawnLiving(armorStand.getArmorStand()).sendPacket(player);
        new RPacketMetadata(armorStand.getEntityId(), armorStand.getDataWatcher(), true).sendPacket(player);

        new RPacketSpawn(entityItem.getEntityItem()).sendPacket(player);
        new RPacketMetadata(entityItem.getEntityId(), entityItem.getDataWatcher(), true).sendPacket(player);

        new RPacketAttach(entityItem.getEntityItem(), armorStand.getArmorStand()).sendPacket(player);
    }

    /**
     * Destroys the armor stand for the player
     * @param player Player
     */
    public void destroy(Player player) {
        viewing.remove(player.getUniqueId());

        new RPacketDestroy(entityItem.getEntityId());
        new RPacketDestroy(armorStand.getEntityId());
    }

    /**
     * Deletes the hologram
     */
    public void delete() {
        holograms.remove(this);
        new ArrayList<>(viewing).stream().map(Bukkit::getPlayer).forEach(this::destroy);
    }
}
