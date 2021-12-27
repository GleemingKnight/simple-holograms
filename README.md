# Simple Holograms
A lightweight packet based hologram api that utilizes reflection to work across a variety of versions

### Example
```java
// Package: me.gleeming.test
public class Plugin {
    public void onEnable() {
        // You can create a hologram using this
        Hologram hologram = new Hologram(Location,
                "Hello, I'm a test hologram.",
                "Nice to meet you!"
        );

        // You can make the hologram display to all players using
        hologram.showToAll();

        // If you wanted to make a hologram that updates, 
        // you can either use Hologram#updateLines or 
        // you can just utilise the UpdatingHologram class
        UpdatingHologram updatingHologram = new UpdatingHologram(Location, 20, (UpdatingHologram.Updater) () -> {
            List<String> lines = new ArrayList<>();
            
            lines.add("There are currently " + Bukkit.getOnlinePlayers());
            lines.add("We hope you enjoy your stay!");
            
            return lines;
        });
        
        // This will make a hologram that displays the online players and
        // updates every 20 ticks to every player that joins the server
        updatingHologram.showToAll();
    }
}

// Package: me.gleeming.test.listener
public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // You can also create per player holograms
        Hologram hologram = new Hologram(player.getLocation(),
                "Hello, " + player.getName(), ", and",
                "welcome to our server!"
        );
        
        hologram.show(player);
    }
}
```
