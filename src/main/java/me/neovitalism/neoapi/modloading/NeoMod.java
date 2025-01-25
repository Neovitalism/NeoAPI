package me.neovitalism.neoapi.modloading;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.config.YamlConfiguration;
import me.neovitalism.neoapi.modloading.command.CommandRegistryInfo;
import me.neovitalism.neoapi.modloading.logging.NeoModLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class NeoMod implements ModInitializer {
    private final NeoModLogger logger = new NeoModLogger(this.getModID());

    public abstract String getModID();
    public abstract String getModPrefix();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.onServerStart());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.onServerStopping());
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                this.registerCommands(new CommandRegistryInfo(dispatcher, registryAccess, environment))));
    }

    public void onServerStart() {
        this.configManager();
    }

    public void onServerStopping() {}

    public abstract void configManager();

    public abstract void registerCommands(CommandRegistryInfo info);

    public NeoModLogger getLogger() {
        return this.logger;
    }

    public File getDataFolder() {
        File folder = FabricLoader.getInstance().getConfigDir().resolve(this.getModID()).toFile();
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    public File getFile(String fileName) {
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) file.getParentFile().mkdirs();
        return file;
    }

    public Configuration getConfig(String fileName, boolean saveResource) {
        File configFile = this.getFile(fileName);
        if (!configFile.exists()) {
            if (!saveResource) return null;
            this.saveResource(fileName, false);
        }
        return this.getConfig(configFile);
    }

    public Configuration getConfig(File configFile) {
        try {
            return YamlConfiguration.loadConfiguration(configFile); // ?
        } catch (IOException e) {
            this.logger.error("Something went wrong getting the config: " + configFile.getName() + ".");
            e.printStackTrace();
        }
        return null;
    }

    public void saveConfig(String fileName, Configuration config) {
        File file = this.getFile(fileName);
        try {
            YamlConfiguration.save(config, file);
        } catch (IOException e) {
            this.logger.error("Something went wrong saving the config: " + fileName + ".");
            e.printStackTrace();
        }
    }

    public void saveResource(String fileName, boolean overwrite) {
        File file = this.getFile(fileName);
        if (file.exists() && !overwrite) return;
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            Path path = Paths.get(getModID().toLowerCase(Locale.ENGLISH), fileName);
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(path
                    .toString().replace("\\", "/"));
            assert in != null;
            in.transferTo(outputStream);
        } catch (IOException e) {
            this.logger.error("Something went wrong saving the resource: " + fileName + ".");
            e.printStackTrace();
        }
    }
}