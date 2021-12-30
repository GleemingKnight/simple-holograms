package me.gleeming.hologram.reflection.impl;

import lombok.Getter;
import lombok.Setter;
import me.gleeming.hologram.reflection.Reflection;
import org.bukkit.Location;

@Getter
@Setter
public class REntityArmorStand extends Reflection {

    private final Location location;
    private Object armorStand;

    public REntityArmorStand(Location location) {
        this.location = location;
    }

    private String customName = "";
    private boolean customNameVisible = true;
    private boolean invisible = true;
    private boolean marker = true;

    /**
     * Gets an armor stands entity id
     */
    public int getEntityId() {
        return (int) callMethod(
                armorStand,
                "getId"
        );
    }

    /**
     * Gets an armor stands data watcher
     */
    public Object getDataWatcher() {
        return callMethod(
                armorStand,
                "getDataWatcher"
        );
    }

    /**
     * Updates the custom name
     */
    public void updateCustomName(String name) {
        customName = name;
        callMethod(armorStand, "setCustomName", name);
    }

    /**
     * Builds the armor stand
     */
    public Object build() {
        armorStand = initialize(
                getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".EntityArmorStand"),
                callMethod(location.getWorld(), "getHandle")
        );

        callMethod(armorStand, "setLocation", location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        callMethod(armorStand, "setCustomName", customName);
        callMethod(armorStand, "setCustomNameVisible", customNameVisible);
        callMethod(armorStand, "setInvisible", invisible);

        // In 1.8 setMarker doesn't exist, so it must
        // be done using the nbt tag compounds
        if(NMS_VERSION.startsWith("v1_8")) {
            Object nbtTagCompound = initialize(
                    getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".NBTTagCompound")
            );

            callMethod(armorStand, "c", new Class[] { getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".NBTTagCompound")}, nbtTagCompound);
            callMethod(nbtTagCompound, "setBoolean", "Marker", marker);
            callMethod(armorStand, "f", new Class[] { getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".NBTTagCompound")}, nbtTagCompound);
        } else {
            callMethod(armorStand, "setMarker", marker);
        }

        return armorStand;
    }

}
