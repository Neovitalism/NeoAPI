package me.neovitalism.neoapi.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

public class UUIDCache {
    private static final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String UUID_TO_NAME_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/";

    private static final Map<String, UUID> USERNAME_UUID_CACHE = new HashMap<>();
    private static final Map<UUID, String> UUID_USERNAME_CACHE = new HashMap<>();

    private static boolean cacheToFile = false;
    private static Future<?> saveFuture = null;

    public static UUID getUUIDFromUsername(String username) {
        UUID playerUUID = UUIDCache.USERNAME_UUID_CACHE.get(username);
        if (playerUUID != null) return playerUUID;
        try (InputStream is = URI.create(UUIDCache.NAME_TO_UUID_URL + username).toURL().openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject json = JsonParser.parseReader(rd).getAsJsonObject();
            playerUUID = UUID.fromString(json.get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"));
        } catch (IOException ignored) {}
        if (playerUUID != null) {
            UUIDCache.USERNAME_UUID_CACHE.put(username, playerUUID);
            UUIDCache.UUID_USERNAME_CACHE.put(playerUUID, username);
            UUIDCache.markDirty();
        }
        return playerUUID;
    }

    public static String getUsernameFromUUID(UUID uuid) {
        String username = UUIDCache.UUID_USERNAME_CACHE.get(uuid);
        if (username != null) return username;
        try (InputStream is = URI.create(UUIDCache.UUID_TO_NAME_URL + uuid).toURL().openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject json = JsonParser.parseReader(rd).getAsJsonObject();
            username = json.get("name").getAsString();
        } catch (IOException ignored) {}
        if (username != null) {
            UUIDCache.UUID_USERNAME_CACHE.put(uuid, username);
            UUIDCache.USERNAME_UUID_CACHE.put(username, uuid);
            UUIDCache.markDirty();
        }
        return username;
    }

    public static void setCacheToFile(boolean cacheToFile) {
        UUIDCache.cacheToFile = cacheToFile;
    }

    public static void cacheUUID(String playerName, UUID playerUUID) {
        String cachedUsername = UUIDCache.UUID_USERNAME_CACHE.get(playerUUID);
        if (cachedUsername == null || cachedUsername.equals(playerName)) {
            UUIDCache.USERNAME_UUID_CACHE.put(playerName, playerUUID);
            UUIDCache.UUID_USERNAME_CACHE.put(playerUUID, playerName);
            UUIDCache.markDirty();
        }
    }

    private static void markDirty() {
        if (!UUIDCache.cacheToFile) return;
        if (UUIDCache.saveFuture != null) UUIDCache.saveFuture.cancel(true);
        UUIDCache.saveFuture = NeoAPI.inst().runTaskAsync(() -> {
            Configuration uuidCache = new Configuration();
            for (Map.Entry<UUID, String> user : new HashSet<>(UUIDCache.UUID_USERNAME_CACHE.entrySet())) {
                uuidCache.set(user.getKey().toString(), user.getValue());
            }
            NeoAPI.inst().saveConfig("data/uuid-cache.yml", uuidCache);
        });
    }

    public static void startup() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player == null) return;
            UUIDCache.cacheUUID(player.getName().getString(), player.getUuid());
        });
        Configuration uuidCache = NeoAPI.inst().getConfig("data/uuid-cache.yml", false);
        if (uuidCache == null) return;
        for (String key : uuidCache.getKeys()) {
            UUID uuid = UUID.fromString(key);
            String username = uuidCache.getString(key);
            UUIDCache.USERNAME_UUID_CACHE.put(username, uuid);
            UUIDCache.UUID_USERNAME_CACHE.put(uuid, username);
        }
    }
}
