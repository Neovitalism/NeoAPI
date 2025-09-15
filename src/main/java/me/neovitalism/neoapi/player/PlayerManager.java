package me.neovitalism.neoapi.player;

import me.neovitalism.neoapi.NeoAPI;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

public final class PlayerManager {
    public static boolean containsTag(ServerPlayerEntity player, String tag) {
        return player.getCommandTags().contains(tag);
    }

    public static void addTag(ServerPlayerEntity player, String tag) {
        player.addCommandTag(tag);
    }

    public static void removeTag(ServerPlayerEntity player, String tag) {
        player.removeCommandTag(tag);
    }

    public static boolean toggleTag(ServerPlayerEntity player, String tag) {
        if (PlayerManager.containsTag(player, tag)) {
            PlayerManager.removeTag(player, tag);
            return false;
        }
        PlayerManager.addTag(player, tag);
        return true;
    }

    public static ServerPlayerEntity getPlayer(String name) {
        return PlayerManager.getPlayerManager().getPlayer(name);
    }

    public static ServerPlayerEntity getPlayer(UUID uuid) {
        return PlayerManager.getPlayerManager().getPlayer(uuid);
    }

    public static List<ServerPlayerEntity> getOnlinePlayers() {
        return PlayerManager.getPlayerManager().getPlayerList();
    }

    public static List<String> getOnlinePlayerNames() {
        return PlayerManager.getOnlinePlayers().stream().map(p -> p.getName().getString()).toList();
    }

    private static net.minecraft.server.PlayerManager getPlayerManager() {
        return NeoAPI.getServer().getPlayerManager();
    }
}
