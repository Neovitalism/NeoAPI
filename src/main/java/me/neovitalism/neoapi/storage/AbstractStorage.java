package me.neovitalism.neoapi.storage;

import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.modloading.NeoMod;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class AbstractStorage {
    private final NeoMod instance;
    protected final StorageType storageType;

    protected MariaDBConnection databaseConnection;

    private NeoExecutor executor = null;
    private boolean markedToSave = false;
    private Future<?> saveFuture = null;

    public AbstractStorage(NeoMod instance, boolean saveResource) {
        this.instance = instance;
        Configuration storageConfig = instance.getConfig("storage.yml", saveResource);
        if (storageConfig != null) {
            StorageType storageType = StorageType.getByName(storageConfig.getString("storage-type"));
            if (storageType == StorageType.MARIADB) {
                Configuration sqlCredentials = storageConfig.getSection("sql");
                this.databaseConnection = new MariaDBConnection(sqlCredentials);
                if (!this.databaseConnection.testConnection()) {
                    instance.getLogger().error("Something went wrong initializing the MariaDB database. Defaulting to YAML.");
                    this.storageType = StorageType.YAML;
                } else {
                    this.storageType = StorageType.MARIADB;
                    for (Map.Entry<String, String> table : this.getTables().entrySet()) {
                        this.databaseConnection.createTable(table.getKey(), table.getValue());
                    }
                }
            } else this.storageType = storageType;
        } else this.storageType = StorageType.YAML;
    }

    public abstract String getFileName();
    public abstract Map<String, String> getTables();

    public void load() {
        Configuration config = this.instance.getConfig(this.getFileName(), false);
        if (config == null) return;
        if (this.storageType == StorageType.YAML) this.load(config);
        if (this.storageType == StorageType.MARIADB) {
            this.loadToDB(config);
            this.deleteFile();
        }
    }

    public abstract void load(Configuration config);
    public abstract void loadToDB(Configuration config);

    private void deleteFile() {
        File file = this.instance.getFile(this.getFileName());
        boolean deleted = file.delete();
        if (!deleted) return;
        File parent = file.getParentFile();
        while (parent.isDirectory() && Objects.requireNonNull(parent.listFiles()).length == 0) {
            deleted = parent.delete();
            if (!deleted) return;
            parent = parent.getParentFile();
        }
    }

    public void markToSave() {
        this.markedToSave = true;
        this.scheduleSave();
    }

    private void scheduleSave() {
        if (this.executor != null) {
            if (this.saveFuture != null) return;
            this.markedToSave = false;
            this.saveFuture = this.executor.scheduleTaskAsync(() -> {
                this.save();
                this.saveFuture = null;
                if (this.markedToSave) this.scheduleSave();
            }, 30, TimeUnit.SECONDS);
        } else {
            this.markedToSave = false;
            this.save();
        }
    }

    private void save() {
        Configuration config = this.toConfig();
        if (config == null) return;
        this.instance.saveConfig(this.getFileName(), config);
    }

    protected Configuration toConfig() {
        return null;
    }

    public void setAsyncExec(NeoExecutor executor) {
        this.executor = executor;
    }

    public void shutdown() {
        if (this.storageType == StorageType.YAML) {
            this.markedToSave = false;
            if (this.saveFuture != null) this.saveFuture.cancel(true);
            this.save();
        } else this.databaseConnection.shutdown();
    }
}
