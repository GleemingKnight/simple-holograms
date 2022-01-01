package me.gleeming.hologram.reflection.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.gleeming.hologram.reflection.Reflection;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode(callSuper = false)
@Data
public class REntityItem extends Reflection {

    private final Location location;
    private final ItemStack itemStack;
    private Object entityItem;

    /**
     * Gets an armor stands entity id
     */
    public int getEntityId() {
        return (int) callMethod(
                entityItem,
                "getId"
        );
    }

    /**
     * Gets an armor stands data watcher
     */
    public Object getDataWatcher() {
        return callMethod(
                entityItem,
                "getDataWatcher"
        );
    }


    /**
     * Updates the item stack
     */
    public void updateItemStack(ItemStack itemStack) {
        Object nmsItem = callMethod(
                getClass("org.bukkit.craftbukkit." + Reflection.NMS_VERSION + ".inventory.CraftItemStack"),
                "asNMSCopy",
                itemStack
        );

        callMethod(entityItem, "setItemStack", nmsItem);
    }


    /**
     * Builds the armor stand
     */
    public Object build() {
        entityItem = initialize(
                getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".EntityItem"),
                callMethod(location.getWorld(), "getHandle")
        );

        updateItemStack(itemStack);
        callMethod(entityItem, "setLocation", location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        return entityItem;
    }

}
