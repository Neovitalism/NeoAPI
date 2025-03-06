package me.neovitalism.neoapi.async.saving;

import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.storage.AbstractStorage;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Deprecated(since = "2.2.0")
/**
 * Use {@link AbstractStorage} instead for storage needs.
 */
public abstract class AsyncSavable {
    private final NeoMod instance;
    private final NeoExecutor executor;
    private final String fileName;
    private boolean markedToSave = false;
    private Future<?> saveFuture = null;

    public AsyncSavable(NeoExecutor executor) {
        this.instance = null;
        this.executor = executor;
        this.fileName = null;
    }

    public AsyncSavable(NeoMod instance, NeoExecutor executor, String fileName) {
        this.instance = instance;
        this.executor = executor;
        this.fileName = fileName;
    }

    protected void save() {
        if (this.instance == null || this.fileName == null) return;
        Configuration config = this.toConfig();
        if (config == null) return;
        this.instance.saveConfig(this.fileName, config);
    }

    protected Configuration toConfig() {
        return null;
    }

    public void load() {
        if (this.instance == null || this.fileName == null) return;
        Configuration config = this.instance.getConfig(this.fileName, false);
        if (config == null) return;
        this.load(config);
    }

    public void load(Configuration config) {}

    public void markToSave() {
        this.markedToSave = true;
        this.scheduleSave();
    }

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
