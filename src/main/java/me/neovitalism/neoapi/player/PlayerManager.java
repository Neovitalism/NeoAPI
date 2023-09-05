package me.neovitalism.neoapi.player;

import me.neovitalism.neoapi.entity.EntityDataStorage;
import me.neovitalism.neoapi.modloading.NeoMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PlayerManager {
    public static NbtCompound getPersistentData(ServerPlayerEntity player) {
        return ((EntityDataStorage)player).getPersistentData();
    }

    public static boolean containsTag(ServerPlayerEntity player, String tag) {
        return player.getCommandTags().contains(tag);
    }

    public static void addTag(ServerPlayerEntity player, String tag) {
        player.getCommandTags().add(tag);
    }

    public static void removeTag(ServerPlayerEntity player, String tag) {
        player.getCommandTags().remove(tag);
    }

    public static void toggleTag(ServerPlayerEntity player, String tag) {
        if(containsTag(player, tag)) {
            removeTag(player, tag);
        } else addTag(player, tag);
    }

    public static ServerPlayerEntity getPlayer(NeoMod instance, String name) {
        return getPlayerManager(instance).getPlayer(name);
    }

    public static ServerPlayerEntity getPlayer(NeoMod instance, UUID uuid) {
        return getPlayerManager(instance).getPlayer(uuid);
    }

    public static List<ServerPlayerEntity> getAllPlayers(NeoMod instance) {
        return getPlayerManager(instance).getPlayerList();
    }

    public static List<ServerPlayerEntity> getAllPlayersExcept(NeoMod instance, String exemptPermission) {
        if(exemptPermission == null) return getAllPlayers(instance);
        List<ServerPlayerEntity> players = new ArrayList<>();
        getAllPlayers(instance).forEach(player -> {
            if(!NeoMod.checkForPermission(player, exemptPermission)) {
                players.add(player);
            }
        });
        return players;
    }

    public static List<ServerPlayerEntity> getAllPlayersExcept(NeoMod instance, String exemptPermission, List<UUID> exemptPlayerUUIDs) {
        List<ServerPlayerEntity> players = new ArrayList<>();
        getAllPlayers(instance).forEach(player -> {
            if(!exemptPlayerUUIDs.contains(player.getUuid()) && !NeoMod.checkForPermission(player, exemptPermission)) {
                players.add(player);
            }
        });
        return players;
    }

    private static net.minecraft.server.PlayerManager getPlayerManager(NeoMod instance) {
        return instance.getServer().getPlayerManager();
    }
}
