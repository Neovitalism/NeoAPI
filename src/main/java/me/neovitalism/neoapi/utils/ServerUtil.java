package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.NeoAPI;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerUtil {
    public static void executeSync(Runnable runnable) {
        NeoAPI.getServer().executeSync(runnable);
    }

    public static <T> CompletableFuture<T> executeSync(Supplier<T> supplier) {
        return NeoAPI.getServer().submit(supplier);
    }

    public static void broadcast(String message) {
        NeoAPI.adventure().all().sendMessage(ColorUtil.parseColour(message));
    }

    public static void sendMessage(Audience audience, String message) {
        ServerUtil.sendMessage(audience, message, (Map<String, String>) null);
    }

    public static void sendMessage(Audience audience, String message, @Nullable Map<String, String> replacements) {
        if (message == null || message.isBlank()) return;
        if (replacements != null) message = StringUtil.replaceReplacements(message, replacements);
        audience.sendMessage(ColorUtil.parseColour(message));
    }

    public static void sendMessage(Audience audience, String prefix, String message) {
        ServerUtil.sendMessage(audience, prefix, message, null);
    }

    public static void sendMessage(Audience audience, String prefix, String message, @Nullable Map<String, String> replacements) {
        if (message == null || message.isBlank()) return;
        if (replacements != null) message = StringUtil.replaceReplacements(message, replacements);
        if (prefix != null && !prefix.isBlank()) message = prefix + message;
        audience.sendMessage(ColorUtil.parseColour(message));
    }
}
