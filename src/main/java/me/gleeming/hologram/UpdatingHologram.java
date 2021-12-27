package me.gleeming.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

/**
 * Simple class to make updating holograms easier
 * All actual logic is in me.gleeming.hologram.Hologram
 */
public class UpdatingHologram extends Hologram {

    public UpdatingHologram(Location location, int updateInterval, Updater updater) {
        super(location, updater.getLines().toArray(new String[]{}));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugins()[0], () ->
                updateLines(updater.getLines().toArray(new String[]{})), updateInterval, updateInterval);
    }

    public interface Updater {
        List<String> getLines();
    }
}
