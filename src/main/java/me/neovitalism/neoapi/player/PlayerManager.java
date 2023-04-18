package me.neovitalism.neoapi.player;

import me.neovitalism.neoapi.entity.EntityDataStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerManager {
    public static NbtCompound getPersistentData(ServerPlayerEntity player) {
        return ((EntityDataStorage)player).getPersistentData();
    }
}
