package me.gleeming.hologram;

import lombok.Getter;
import me.gleeming.hologram.listener.HologramListener;
import me.gleeming.hologram.reflection.impl.REntityArmorStand;
import me.gleeming.hologram.reflection.impl.RPacketDestroy;
import me.gleeming.hologram.reflection.impl.RPacketMetadata;
import me.gleeming.hologram.reflection.impl.RPacketSpawnLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Simple one class hologram api using reflection
 * @author github.com/GleemingKnight
 */
@Getter
public class Hologram {

    static {
        Bukkit.getPluginManager().registerEvents(new HologramListener(), Bukkit.getPluginManager().getPlugins()[0]);
    }

    // The distance in which the holograms can be seen by players
    public static int VIEW_DISTANCE = 100;

    // All the holograms currently created
    @Getter private static final List<Hologram> holograms = new ArrayList<>();

    // Current armor stand objects
    private final List<REntityArmorStand> armorStands = new ArrayList<>();

    // Location hologram starts at
    private final Location location;

    // Information about who is supposed to see it
    private final List<UUID> canSee = new ArrayList<>();
    private boolean showToAll = false;

    // List of players that can currently see the armor stand
    private final List<UUID> viewing = new ArrayList<>();

    public Hologram(Location location, String... lines) {
        this.location = location;
        this.updateLines(lines);

        holograms.add(this);
    }

    /**
     * Updates the lines of the armor stand
     */
    public void updateLines(String... newLines) {
        int difference = armorStands.size() - newLines.length;

        for(int i = 0; i < Math.abs(difference); i++) {
            if(difference > 0) {
                armorStands.remove(armorStands.size() - 1);
            } else {
                REntityArmorStand armorStand = new REntityArmorStand(
                        (armorStands.size() == 0 ? location : armorStands.get(armorStands.size() - 1).getLocation()).subtract(0, 0.22, 0)
                );

                armorStand.build();
                armorStands.add(armorStand);
            }
        }

        Iterator<REntityArmorStand> armorStandIterator = armorStands.iterator();
        Arrays.stream(newLines).forEach(line -> {
            REntityArmorStand armorStand = armorStandIterator.next();
            armorStand.updateCustomName(ChatColor.translateAlternateColorCodes('&', line));

            RPacketMetadata packet = new RPacketMetadata(armorStand.getEntityId(), armorStand.getDataWatcher(), true);
            viewing.stream().map(Bukkit::getPlayer).forEach(packet::sendPacket);
        });
    }

    /**
     * Makes the armor stand visible to all players
     */
    public Hologram showToAll() {
        showToAll = true;
        viewing.clear();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getLocation().distance(location) <= VIEW_DISTANCE)
                .forEach(this::create);

        return this;
    }

    /**
     * Hides the armor stand from all players
     */
    public Hologram hideFromAll() {
        showToAll = false;
        viewing.stream().map(Bukkit::getPlayer).forEach(this::destroy);
        canSee.stream().map(Bukkit::getPlayer).forEach(this::create);
        return this;
    }

    /**
     * Shows the armor stand to a player
     * @param player Player
     */
    public Hologram show(Player player) {
        canSee.add(player.getUniqueId());
        if(player.getLocation().distance(location) <= VIEW_DISTANCE) create(player);
        return this;
    }

    /**
     * Hides the armor stand from a player
     * @param player Player
     */
    public Hologram hide(Player player) {
        canSee.remove(player.getUniqueId());
        destroy(player);
        return this;
    }

    /**
     * Creates the armor stand for the player
     * @param player Player
     */
    public void create(Player player) {
        viewing.add(player.getUniqueId());
        armorStands.stream()
                .filter(armorStand -> armorStand.getCustomName().length() > 0)
                .forEach(armorStand -> new RPacketSpawnLiving(armorStand.getArmorStand()).sendPacket(player));
    }

    /**
     * Destroys the armor stand for the player
     * @param player Player
     */
    public void destroy(Player player) {
        viewing.remove(player.getUniqueId());
        armorStands.forEach(armorStand -> new RPacketDestroy(armorStand.getEntityId()).sendPacket(player));
    }

    /**
     * Deletes the hologram
     */
    public void delete() {
        holograms.remove(this);
        new ArrayList<>(viewing).stream().map(Bukkit::getPlayer).forEach(this::destroy);
    }
}
