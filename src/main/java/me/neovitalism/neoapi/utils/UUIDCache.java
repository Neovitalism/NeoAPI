package me.neovitalism.neoapi.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UUIDCache {
    private static final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String UUID_TO_NAME_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/";

    private static final BiMap<String, UUID> PLAYER_UUID_CACHE = HashBiMap.create();

    public static UUID getUUIDFromUsername(String username) {
        return UUIDCache.PLAYER_UUID_CACHE.computeIfAbsent(username, name -> {
            try (InputStream is = URI.create(UUIDCache.NAME_TO_UUID_URL + name).toURL().openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                JsonObject json = JsonParser.parseReader(rd).getAsJsonObject();
                return UUID.fromString(json.get("id").getAsString().replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"));
            } catch (IOException ignored) {}
            return null;
        });
    }

    public static String getUsernameFromUUID(UUID uuid) {
        return UUIDCache.PLAYER_UUID_CACHE.inverse().computeIfAbsent(uuid, playerUUID -> {
            try (InputStream is = URI.create(UUIDCache.UUID_TO_NAME_URL + playerUUID).toURL().openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                JsonObject json = JsonParser.parseReader(rd).getAsJsonObject();
                return json.get("name").getAsString();
            } catch (IOException ignored) {}
            return null;
        });
    }

    public static void cacheUUID(String playerName, UUID playerUUID) {
        UUIDCache.PLAYER_UUID_CACHE.put(playerName, playerUUID);
    }
}
