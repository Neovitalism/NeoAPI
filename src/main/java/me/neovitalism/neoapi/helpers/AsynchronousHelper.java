package me.neovitalism.neoapi.helpers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class AsynchronousHelper {
    private static final ExecutorService ASYNC_EXEC = Executors.newFixedThreadPool(8,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("NeoAPI Thread")
                    .build());

    public static <T> CompletableFuture<T> runTaskAsynchronously(Callable<T> callable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ASYNC_EXEC);
    }

    public static void shutdown() {
        ASYNC_EXEC.shutdownNow();
    }
}
