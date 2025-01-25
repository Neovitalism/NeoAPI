package me.neovitalism.neoapi.async;

import me.neovitalism.neoapi.utils.ServerUtil;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class NeoExecutor {
    private final ScheduledExecutorService scheduler;

    protected NeoExecutor(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void runTaskSync(Runnable runnable) {
        ServerUtil.executeSync(runnable);
    }

    public <T> Future<T> runTaskSync(Supplier<T> supplier) {
        return ServerUtil.executeSync(supplier);
    }

    public ScheduledFuture<?> scheduleTaskSync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return this.scheduleTaskAsync(() -> this.runTaskSync(runnable), delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleRepeatingTaskSync(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return this.scheduleRepeatingTaskAsync(() -> this.runTaskSync(runnable), initialDelay, delay, timeUnit);
    }

    public <T> Future<T> runTaskAsync(Callable<T> callable) {
        return this.scheduler.submit(callable);
    }

    public void runTaskAsync(Runnable runnable) {
        this.scheduler.submit(runnable);
    }

    public <T> ScheduledFuture<T> scheduleTaskAsync(Callable<T> callable, long delay, TimeUnit timeUnit) {
        return this.scheduler.schedule(callable, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleTaskAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return this.scheduler.schedule(runnable, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleRepeatingTaskAsync(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return this.scheduler.scheduleAtFixedRate(runnable, initialDelay, delay, timeUnit);
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }
}
