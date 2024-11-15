package me.neovitalism.neoapi.async;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class NeoAPIExecutorManager {
    private static final ThreadFactoryBuilder THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setDaemon(true);
    private static final Set<NeoExecutor> REGISTERED_EXECUTORS = new HashSet<>();

    public static NeoExecutor createScheduler(String nameFormat, int cores) {
        return new NeoExecutor(Executors.newScheduledThreadPool(cores,
                NeoAPIExecutorManager.THREAD_FACTORY_BUILDER.setNameFormat(nameFormat).build()));
    }

    public static void shutdown() {
        NeoAPIExecutorManager.REGISTERED_EXECUTORS.forEach(NeoExecutor::shutdown);
        NeoAPIExecutorManager.REGISTERED_EXECUTORS.clear();
    }
}
