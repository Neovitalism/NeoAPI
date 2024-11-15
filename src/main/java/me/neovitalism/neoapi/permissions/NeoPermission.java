package me.neovitalism.neoapi.permissions;

import me.neovitalism.neoapi.NeoAPI;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class NeoPermission {
    public static final NeoPermission EMPTY = NeoPermission.of("", -1);

    private final String permission;
    private final int permissionLevel;

    private NeoPermission(String permission, int permissionLevel) {
        this.permission = permission;
        this.permissionLevel = permissionLevel;
    }

    public boolean matches(ServerCommandSource source) {
        if (this.permission.isEmpty()) return true;
        return NeoAPI.getPermissionProvider().hasPermission(source, this.permission, this.permissionLevel);
    }

    public boolean matches(ServerPlayerEntity player) {
        if (this.permission.isEmpty()) return true;
        return NeoAPI.getPermissionProvider().hasPermission(player, this.permission, this.permissionLevel);
    }

    @Override
    public String toString() {
        return "NeoPermission{permission=\"" + this.permission + "\",permissionLevel=" + this.permissionLevel + "}";
    }

    public NeoPermission[] toArray() {
        return new NeoPermission[]{this};
    }

    public static NeoPermission of(String permission) {
        return NeoPermission.of(permission, 4);
    }

    public static NeoPermission of(String permission, int level) {
        return new NeoPermission(permission, level);
    }

    public static NeoPermission[] of(String... permissions) {
        NeoPermission[] permissionArray = new NeoPermission[permissions.length];
        for (int i = 0; i < permissions.length; i++) permissionArray[i] = NeoPermission.of(permissions[i]);
        return permissionArray;
    }

    public static NeoPermission[] add(NeoPermission original, String... permissions) {
        return NeoPermission.add(new NeoPermission[]{original}, permissions);
    }

    public static NeoPermission[] add(NeoPermission[] originals, String... permissions) {
        NeoPermission[] permissionArray = new NeoPermission[originals.length + permissions.length];
        int index = 0;
        for (NeoPermission neoPermission : originals) {
            permissionArray[index] = neoPermission;
            index++;
        }
        for (String permission : permissions) {
            permissionArray[index] = NeoPermission.of(permission);
            index++;
        }
        return permissionArray;
    }
}
