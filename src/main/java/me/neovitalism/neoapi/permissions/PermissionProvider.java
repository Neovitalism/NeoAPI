package me.neovitalism.neoapi.permissions;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PermissionProvider {
    boolean hasPermission(ServerPlayerEntity player, String permission, int defaultLevel);

    default boolean hasPermission(ServerPlayerEntity player, String permission) {
        return this.hasPermission(player, permission, 4);
    }

    default boolean hasPermission(ServerCommandSource source, String permission, int level) {
        if (source.getPlayer() == null) return true;
        return this.hasPermission(source.getPlayer(), permission, level);
    }

    @Nullable String getMetaValue(ServerPlayerEntity player, String permission);

    default String getMetaValue(ServerCommandSource source, String permission) {
        if (source.getPlayer() == null) return null;
        return this.getMetaValue(source.getPlayer(), permission);
    }

    default Double getMetaValueNumber(ServerPlayerEntity player, String permission) {
        String value = this.getMetaValue(player, permission);
        if (value == null) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
