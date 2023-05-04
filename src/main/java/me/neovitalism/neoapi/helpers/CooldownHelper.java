package me.neovitalism.neoapi.helpers;

import me.neovitalism.neoapi.player.PlayerManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.TimeUnit;

public class CooldownHelper {
    public static int getCooldown(ServerPlayerEntity player, String cooldownKey) {
        NbtCompound persistentData = PlayerManager.getPersistentData(player);
        if(persistentData.contains(cooldownKey + "-Cooldown")) {
            long now = System.currentTimeMillis();
            long endTime = persistentData.getLong(cooldownKey + "-Cooldown");
            if(endTime-now <= 0) {
                return 0;
            } else {
                return (int) TimeUnit.MILLISECONDS.toSeconds(endTime-now);
            }
        } else return 0;
    }

    public static void startCooldown(ServerPlayerEntity player, String cooldownKey, int cooldown) {
        long endTime = System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(cooldown);
        PlayerManager.getPersistentData(player).putLong(cooldownKey + "-Cooldown", endTime);
    }
}
