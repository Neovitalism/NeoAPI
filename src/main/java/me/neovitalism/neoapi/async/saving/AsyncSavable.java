package me.neovitalism.neoapi.async.saving;

import me.neovitalism.neoapi.async.NeoExecutor;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AsyncSavable {
    private final NeoExecutor executor;
    private boolean markedToSave = false;
    private Future<?> saveFuture = null;

    public AsyncSavable(NeoExecutor executor) {
        this.executor = executor;
    }

    public void markToSave() {
        this.markedToSave = true;
        this.scheduleSave();
    }

    protected abstract void save();

    private void scheduleSave() {
        if (this.saveFuture != null) return;
        this.markedToSave = false;
        this.saveFuture = this.executor.scheduleTaskAsync(() -> {
            this.save();
            this.saveFuture = null;
            if (this.markedToSave) this.scheduleSave();
        }, 30, TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.markedToSave = false;
        if (this.saveFuture != null) this.saveFuture.cancel(true);
        this.save();
    }
}
