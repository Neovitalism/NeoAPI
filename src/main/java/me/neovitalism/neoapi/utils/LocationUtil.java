package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class LocationUtil {
    public static Location getLookingAt(ServerPlayerEntity player) {
        HitResult hitResult = player.raycast(500, 0, false);
        Vec3d target = hitResult.getPos();
        return new Location(player.getWorld(), target.x, target.y, target.z);
    }

    public static Entity getLookingAtEntity(ServerPlayerEntity player, int maxDistance) {
        HitResult hitResult = player.raycast(maxDistance, 0, false);
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)hitResult).getEntity();
        } else return null;
    }
}