package me.neovitalism.neoapi.objects;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class Location {
    private final ServerWorld world;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public Location(Configuration config) {
        String worldName = config.getString("world", null);
        ServerWorld world = null;
        if (worldName != null) {
            for (ServerWorld serverWorld : NeoAPI.getServer().getWorlds()) {
                if (!serverWorld.getRegistryKey().getValue().toString().equals(worldName)) continue;
                world = serverWorld;
                break;
            }
        }
        this.world = world;
        this.x = config.getInt("x");
        this.y = config.getInt("y");
        this.z = config.getInt("z");
        this.pitch = config.getInt("pitch", -1000);
        this.yaw = config.getInt("yaw", -1000);
    }

    public Location(ServerPlayerEntity player) {
        this.world = player.getServerWorld();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.pitch = player.getPitch();
        this.yaw = player.getYaw();
    }

    public Location(ServerWorld world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = -1000;
        this.yaw = -1000;
    }

    public Location(ServerWorld world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void teleport(ServerPlayerEntity player) {
        float tpPitch = (this.pitch != -1000) ? this.pitch : player.getPitch();
        float tpYaw = (this.yaw != -1000) ? this.yaw : player.getYaw();
        player.teleport(this.world, this.x, this.y, this.z, tpYaw, tpPitch);
    }

    public boolean isEqualTo(Location location) {
        if (!this.isEqualToCoordinatesOf(location)) return false;
        return this.yaw == location.getYaw() && this.pitch == location.getPitch();
    }

    public boolean isEqualToCoordinatesOf(Location location) {
        if (!this.world.equals(location.getWorld())) return false;
        if (this.x != location.getX()) return false;
        if (this.y != location.getY()) return false;
        return this.z == location.getZ();
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void addReplacements(Map<String, String> replacements) {
        replacements.put("{x}", String.valueOf(this.x));
        replacements.put("{y}", String.valueOf(this.y));
        replacements.put("{z}", String.valueOf(this.z));
        replacements.put("{pitch}", String.valueOf(this.pitch));
        replacements.put("{yaw}", String.valueOf(this.yaw));
        replacements.put("{world}", this.world.getRegistryKey().getValue().toString());
    }

    public Configuration toConfiguration() {
        Configuration locationConfig = new Configuration();
        locationConfig.set("world", this.world.getRegistryKey().getValue().toString());
        locationConfig.set("x", this.x);
        locationConfig.set("y", this.y);
        locationConfig.set("z", this.z);
        locationConfig.set("pitch", this.pitch);
        locationConfig.set("yaw", this.yaw);
        return locationConfig;
    }
}
