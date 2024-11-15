package me.neovitalism.neoapi.permissions;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class DefaultPermissionProvider implements PermissionProvider {
    @Override
    public boolean hasPermission(ServerPlayerEntity player, String permission, int defaultLevel) {
        return player.hasPermissionLevel(defaultLevel);
    }

    @Override
    public @Nullable String getMetaValue(ServerPlayerEntity player, String permission) {
        return null;
    }
}
