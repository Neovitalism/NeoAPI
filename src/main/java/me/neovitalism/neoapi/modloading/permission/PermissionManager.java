package me.neovitalism.neoapi.modloading.permission;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PermissionManager {
    boolean checkForPermission(ServerPlayerEntity serverPlayerEntity, String permission, int defaultLevel);

    @Nullable String getMetaPermission(ServerPlayerEntity serverPlayerEntity, String permission);
}
