package me.neovitalism.neoapi.objects;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.utils.LocationUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Map;

public class Location {
    private ServerWorld world;
    private double x;
    private double y;
    private double z;
    private float pitch = -1000;
    private float yaw = -1000;

    public Location(Configuration config) {
        String worldName = config.getString("world", null);
        if (worldName == null) this.world = null;
        else this.world = LocationUtil.getWorld(worldName);
        this.x = config.getDouble("x");
        this.y = config.getDouble("y");
        this.z = config.getDouble("z");
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

    public String getWorldName() {
        return LocationUtil.getWorldName(this.world);
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

    public BlockPos getBlockPos() {
        return new BlockPos((int) Math.round(this.x), (int) Math.round(this.y), (int) Math.round(this.z));
    }

    public Vec3d toVec3d() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public Biome getBiome() {
        return this.world.getBiome(this.getBlockPos()).value();
    }

    public WorldChunk getChunk() {
        return this.world.getWorldChunk(this.getBlockPos());
    }

    public BlockState getBlockState() {
        if (this.world == null) return null;
        return this.world.getBlockState(this.getBlockPos());
    }

    public void setBlockState(BlockState state, boolean forceLoad) {
        if (forceLoad) this.setForceLoaded(true);
        this.getWorld().setBlockState(this.getBlockPos(), state);
        if (forceLoad) this.setForceLoaded(false);
    }

    public BlockEntity getBlockEntity() {
        if (this.world == null) return null;
        return this.world.getBlockEntity(this.getBlockPos());
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void shift(double x, double y, double z) {
        this.shift(x, y, z, 0, 0);
    }

    public void shift(double x, double y, double z, float pitch, float yaw) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.pitch += pitch;
        this.yaw += yaw;
    }

    public Location withWorld(ServerWorld world) {
        this.world = world;
        return this;
    }

    public Location withX(double x) {
        this.x = x;
        return this;
    }

    public Location withY(double y) {
        this.y = y;
        return this;
    }

    public Location withZ(double z) {
        this.z = z;
        return this;
    }

    public Location shifted(double x, double y, double z) {
        return this.shifted(x, y, z, 0, 0);
    }

    public Location shifted(double x, double y, double z, float pitch, float yaw) {
        this.shift(x, y, z, pitch, yaw);
        return this;
    }

    public Location centered() {
        this.x = ((int) this.x) + 0.5;
        this.y = (int) this.y;
        this.z = ((int) this.z) + 0.5;
        return this;
    }

    public boolean isLoaded() {
        ChunkPos chunkPos = this.getChunk().getPos();
        return this.getWorld().isChunkLoaded(chunkPos.x, chunkPos.z);
    }

    public void setForceLoaded(boolean forced) {
        ChunkPos chunkPos = this.getChunk().getPos();
        this.getWorld().setChunkForced(chunkPos.x, chunkPos.z, forced);
    }

    public Location copy() {
        return new Location(this.world, this.x, this.y, this.z, this.pitch, this.yaw);
    }

    public void addReplacements(Map<String, String> replacements) {
        replacements.put("{x}", String.valueOf(this.x));
        replacements.put("{y}", String.valueOf(this.y));
        replacements.put("{z}", String.valueOf(this.z));
        replacements.put("{pitch}", String.valueOf(this.pitch));
        replacements.put("{yaw}", String.valueOf(this.yaw));
        if (this.world != null) replacements.put("{world}", this.world.getRegistryKey().getValue().toString());
    }

    public void addReplacements(Map<String, String> replacements, String format) {
        replacements.put("{x}", String.format(format, this.x));
        replacements.put("{y}", String.format(format, this.y));
        replacements.put("{z}", String.format(format, this.z));
        replacements.put("{pitch}", String.format(format, this.pitch));
        replacements.put("{yaw}", String.format(format, this.yaw));
        if (this.world != null) replacements.put("{world}", this.world.getRegistryKey().getValue().toString());
    }

    public Configuration toConfiguration() {
        Configuration locationConfig = new Configuration();
        if (this.world != null) locationConfig.set("world", this.world.getRegistryKey().getValue().toString());
        locationConfig.set("x", this.x);
        locationConfig.set("y", this.y);
        locationConfig.set("z", this.z);
        if (this.pitch != -1000) locationConfig.set("pitch", this.pitch);
        if (this.yaw != -1000) locationConfig.set("yaw", this.yaw);
        return locationConfig;
    }

    public static Location from(ServerWorld world, BlockPos pos) {
        return new Location(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
