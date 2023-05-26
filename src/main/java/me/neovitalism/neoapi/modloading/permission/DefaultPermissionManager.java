package me.neovitalism.neoapi.modloading.permission;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class DefaultPermissionManager implements PermissionManager {
    public DefaultPermissionManager() {

    }

    @Override
    public boolean checkForPermission(ServerPlayerEntity serverPlayerEntity, String permission, int defaultLevel) {
        return serverPlayerEntity.hasPermissionLevel(defaultLevel);
    }

    @Override
    public boolean checkForPermission(ServerPlayerEntity serverPlayerEntity, String permission) {
        return serverPlayerEntity.hasPermissionLevel(4);
    }

    @Override
    public @Nullable String getMetaPermission(ServerPlayerEntity serverPlayerEntity, String permission) {
        return null;
    }
}
