package me.neovitalism.neoapi.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDCache {
    private static final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String UUID_TO_NAME_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/";

    private static final Map<String, UUID> USERNAME_UUID_CACHE = new HashMap<>();
    private static final Map<UUID, String> UUID_USERNAME_CACHE = new HashMap<>();

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
        }
        return username;
    }

    public static void cacheUUID(String playerName, UUID playerUUID) {
        UUIDCache.USERNAME_UUID_CACHE.put(playerName, playerUUID);
        UUIDCache.UUID_USERNAME_CACHE.put(playerUUID, playerName);
    }
}
