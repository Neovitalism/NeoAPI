package me.neovitalism.neoapi.modloading.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class LuckPermsPermissionManager implements PermissionManager {
    public LuckPermsPermissionManager() {
        luckPermsAPI = LuckPermsProvider.get();
    }

    private final LuckPerms luckPermsAPI;

    @Override
    public boolean checkForPermission(ServerPlayerEntity serverPlayerEntity, String permission, int defaultLevel) {
        return serverPlayerEntity.hasPermissionLevel(4) ||
                getPlayerAdapter().getPermissionData(serverPlayerEntity).checkPermission(permission).asBoolean();
    }

    @Override
    public boolean checkForPermission(ServerPlayerEntity serverPlayerEntity, String permission) {
        return getPlayerAdapter().getPermissionData(serverPlayerEntity).checkPermission(permission).asBoolean();
    }

    @Override
    public @Nullable String getMetaPermission(ServerPlayerEntity serverPlayerEntity, String metaKey) {
        return getPlayerAdapter().getMetaData(serverPlayerEntity).getMetaValue(metaKey);
    }

    private PlayerAdapter<ServerPlayerEntity> getPlayerAdapter() {
        return luckPermsAPI.getPlayerAdapter(ServerPlayerEntity.class);
    }
}
