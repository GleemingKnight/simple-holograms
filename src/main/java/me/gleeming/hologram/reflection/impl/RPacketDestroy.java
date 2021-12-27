package me.gleeming.hologram.reflection.impl;

import lombok.AllArgsConstructor;
import me.gleeming.hologram.reflection.Reflection;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class RPacketDestroy extends Reflection {

    private final int entityId;

    /**
     * Sends the packet to a player
     * @param player Player
     */
    public void sendPacket(Player player) {
        Object craftPlayerHandle = callMethod(player, "getHandle");
        Object playerConnection = getField(craftPlayerHandle, "playerConnection");

        callMethod(playerConnection, "sendPacket", initialize(
                getClass("net.minecraft.server." + Reflection.NMS_VERSION + ".PacketPlayOutEntityDestroy"),
                (Object) new int[] { entityId }
        ));
    }

}
