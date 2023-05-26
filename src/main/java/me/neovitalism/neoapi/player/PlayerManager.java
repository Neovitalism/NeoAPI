package me.neovitalism.neoapi.player;

import me.neovitalism.neoapi.entity.EntityDataStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerManager {
    public static NbtCompound getPersistentData(ServerPlayerEntity player) {
        return ((EntityDataStorage)player).getPersistentData();
    }

    public static boolean containsTag(ServerPlayerEntity player, String tag) {
        return player.getScoreboardTags().contains(tag);
    }

    public static void addTag(ServerPlayerEntity player, String tag) {
        player.getScoreboardTags().add(tag);
    }

    public static void removeTag(ServerPlayerEntity player, String tag) {
        player.getScoreboardTags().remove(tag);
    }

    public static void toggleTag(ServerPlayerEntity player, String tag) {
        if(containsTag(player, tag)) {
            removeTag(player, tag);
        } else addTag(player, tag);
    }

    /**
     * This is not a method to be used, only used to support previously existing tags. This will be removed in a later version.
     **/
    @Deprecated
    public static boolean getFirstJoinTag(ServerPlayerEntity player) {
        NbtCompound persistentData = PlayerManager.getPersistentData(player);
        boolean joinedBeforeNBT = (persistentData.contains("joinedBefore") && persistentData.getBoolean("joinedBefore"));
        if(joinedBeforeNBT) {
            PlayerManager.getPersistentData(player).remove("joinedBefore");
            PlayerManager.addTag(player, "neoapi.joinedBefore");
        }
        return PlayerManager.containsTag(player,"neoapi.joinedBefore");
    }
}
