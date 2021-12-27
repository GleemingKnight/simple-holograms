package me.gleeming.hologram.listener;

import me.gleeming.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HologramListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Hologram.getHolograms().stream()
                .filter(hologram -> hologram.isShowToAll() || hologram.getCanSee().contains(player.getUniqueId()))
                .filter(hologram -> hologram.getLocation().distance(player.getLocation()) <= Hologram.VIEW_DISTANCE)
                .forEach(hologram -> hologram.create(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Hologram.getHolograms().forEach(hologram -> {
            hologram.destroy(player);
            hologram.getCanSee().remove(player.getUniqueId());
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Hologram.getHolograms().stream()
                .filter(hologram -> hologram.isShowToAll() || hologram.getCanSee().contains(player.getUniqueId()))
                .forEach(hologram -> {
                    if(hologram.getLocation().distance(player.getLocation()) <= Hologram.VIEW_DISTANCE && !hologram.getViewing().contains(player.getUniqueId())) {
                        hologram.create(player);
                    }

                    if(hologram.getLocation().distance(player.getLocation()) > Hologram.VIEW_DISTANCE && hologram.getViewing().contains(player.getUniqueId())) {
                        hologram.destroy(player);
                    }
                });
    }
}
