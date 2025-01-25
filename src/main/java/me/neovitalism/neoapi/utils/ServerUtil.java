package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.NeoAPI;

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
}
