package me.neovitalism.neoapi.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class LuckPermsPermissionProvider implements PermissionProvider {
    private final LuckPerms luckPermsAPI;

    public LuckPermsPermissionProvider() {
        this.luckPermsAPI = LuckPermsProvider.get();
    }

    @Override
    public boolean hasPermission(ServerPlayerEntity player, String permission, int defaultLevel) {
        if (player.hasPermissionLevel(4)) return true;
        return this.getPlayerAdapter().getPermissionData(player).checkPermission(permission).asBoolean();
    }

    @Override
    public @Nullable String getMetaValue(ServerPlayerEntity player, String metaKey) {
        return this.getPlayerAdapter().getMetaData(player).getMetaValue(metaKey);
    }

    private PlayerAdapter<ServerPlayerEntity> getPlayerAdapter() {
        return this.luckPermsAPI.getPlayerAdapter(ServerPlayerEntity.class);
    }
}
