package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.objects.Location;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public final class LocationUtil {
    public static ServerWorld getWorld(String worldName) {
        for (ServerWorld serverWorld : NeoAPI.getServer().getWorlds()) {
            if (!LocationUtil.getWorldName(serverWorld).equals(worldName)) continue;
            return serverWorld;
        }
        return null;
    }

    public static String getWorldName(ServerWorld world) {
        return world.getRegistryKey().getValue().toString();
    }

    public static Location getLookingAt(ServerPlayerEntity player) {
        HitResult hitResult = player.raycast(500, 0, false);
        Vec3d target = hitResult.getPos();
        return new Location(player.getServerWorld(), target.x, target.y, target.z);
    }

    public static Location getHighestBlock(Location location) {
        Location copy = location.copy();
        for (int y = location.getWorld().getTopY(); y > location.getWorld().getBottomY(); y--) {
            if (!BlockUtil.isAir(copy.withY(y).getBlockState())) break;
        }
        return null;
    }

    public static boolean canSeeSky(ServerPlayerEntity player) {
        Location playerLoc = new Location(player).centered();
        return player.getWorld().raycast(new RaycastContext(
                new Vec3d(playerLoc.getX(), playerLoc.getY() + 1, playerLoc.getZ()),
                new Vec3d(playerLoc.getX(), player.getWorld().getTopY(), playerLoc.getZ()),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        )).getType() == HitResult.Type.MISS;
    }

    public static Registry<Biome> getBiomeRegistry() {
        return NeoAPI.getServer().getRegistryManager().get(RegistryKeys.BIOME);
    }

    public static Biome getBiome(Identifier biomeID) {
        return LocationUtil.getBiomeRegistry().get(biomeID);
    }

    public static List<Identifier> getAllBiomes(List<String> biomeKeys) {
        List<Identifier> biomes = new ArrayList<>();
        for (String biomeKey : biomeKeys) {
            if (!biomeKey.startsWith("#")) biomes.add(Identifier.of(biomeKey));
            else biomes.addAll(LocationUtil.getAllBiomesInTag(Identifier.of(biomeKey.substring(1))));
        }
        return biomes;
    }

    public static Identifier getBiomeIdentifier(Biome biome) {
        return LocationUtil.getBiomeRegistry().getId(biome);
    }

    public static List<Identifier> getAllBiomesInTag(Identifier tagID) {
        return LocationUtil.getBiomeRegistry()
                .getOrCreateEntryList(TagKey.of(RegistryKeys.BIOME, tagID)).stream()
                .map(registryEntry -> LocationUtil.getBiomeIdentifier(registryEntry.value())).toList();
    }

    public static String biomeToTranslatable(Biome biome) {
        return LocationUtil.biomeToTranslatable(LocationUtil.getBiomeIdentifier(biome));
    }

    public static String biomeToTranslatable(Identifier biomeID) {
        return "<lang:biome." + biomeID.toString().replace(":", ".") +">";
    }

    public static int distance(int x, int z) {
        int max = Math.max(x, z);
        int min = Math.min(x, z);
        return max - min;
    }
}