package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.objects.Location;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public final class LocationUtil {
    public static Location getLookingAt(ServerPlayerEntity player) {
        HitResult hitResult = player.raycast(500, 0, false);
        Vec3d target = hitResult.getPos();
        return new Location(player.getServerWorld(), target.x, target.y, target.z);
    }
}