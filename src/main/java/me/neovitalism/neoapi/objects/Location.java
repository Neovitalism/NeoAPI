package me.neovitalism.neoapi.objects;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.world.WorldManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class Location {
    private final ServerWorld world;
    private final double x;
    private final double y;
    private final double z;
    private float pitch = -1000;
    private float yaw = -1000;

    public Location(ServerWorld world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(ServerWorld world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void teleport(ServerPlayerEntity player) {
        float tpPitch = (pitch != -1000) ? pitch : player.getPitch();
        float tpYaw = (yaw != -1000) ? yaw : player.getYaw();
        player.teleport(world, x, y, z, tpYaw, tpPitch);
    }

    public Configuration toConfiguration() {
        Configuration locationConfig = new Configuration();
        locationConfig.set("world", String.valueOf(WorldManager.getWorldUUID(world)));
        locationConfig.set("x", x);
        locationConfig.set("y", y);
        locationConfig.set("z", z);
        locationConfig.set("pitch", pitch);
        locationConfig.set("yaw", yaw);
        return locationConfig;
    }

    public void addReplacements(Map<String, String> replacements) {
        replacements.put("{x}", String.valueOf(x));
        replacements.put("{y}", String.valueOf(y));
        replacements.put("{z}", String.valueOf(z));
        replacements.put("{pitch}", String.valueOf(pitch));
        replacements.put("{yaw}", String.valueOf(yaw));
        replacements.put("{world}", WorldManager.getWorldName(world));
    }
}
