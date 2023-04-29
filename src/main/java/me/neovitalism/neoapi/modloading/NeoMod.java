package me.neovitalism.neoapi.modloading;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.events.JoinEvent;
import me.neovitalism.neoapi.lang.LangManager;
import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.modloading.config.YamlConfiguration;
import me.neovitalism.neoapi.modloading.permission.DefaultPermissionManager;
import me.neovitalism.neoapi.modloading.permission.LuckPermsPermissionManager;
import me.neovitalism.neoapi.modloading.permission.PermissionManager;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.world.WorldManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class NeoMod implements ModInitializer {
    public abstract String getModID();
    public abstract String getModPrefix();
    public abstract LangManager getLangManager();

    private static List<NeoMod> loadedMods = new ArrayList<>();

    public NeoMod() {
        if(!registered) {
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                adventure = FabricServerAudiences.of(server);
                NeoMod.server = server;
            });
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
                adventure = null;
                NeoMod.server = null;
                registered = false;
                permissionProvider = null;
            });
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                boolean joinedBefore = PlayerManager.getFirstJoinTag(handler.getPlayer());
                JoinEvent.EVENT.invoker().interact(handler.getPlayer(), joinedBefore);
            });
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                WorldManager.mapWorlds(server);
                registerPermissionProvider();
            });
            registered = true;
        }
        ServerLifecycleEvents.SERVER_STARTED.register(server -> configManager());
    }

    private static boolean registered = false;

    private static FabricServerAudiences adventure = null;
    public FabricServerAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return adventure;
    }

    private static MinecraftServer server = null;

    public MinecraftServer getServer() {
        return server;
    }

    public Configuration getDefaultConfig() {
        return getConfig("config.yml");
    }

    public void saveConfig(File file, Configuration config) {
        try {
        YamlConfiguration.save(config, file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig(String fileName) {
        Configuration config = null;
        try {
            config = YamlConfiguration.loadConfiguration(getOrCreateConfigurationFile(fileName));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public File getOrCreateConfigurationFile(String fileName) throws IOException {
        File configFolder = FabricLoader.getInstance().getConfigDir().resolve(getModID()).toFile();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        File configFile = new File(configFolder, fileName);
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile);
            Path path = Paths.get(getModID().toLowerCase(Locale.ENGLISH), fileName);
            InputStream in = getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/"));
            in.transferTo(outputStream);
        }
        return configFile;
    }

    public abstract void onInitialize();

    public abstract void configManager();

    private static PermissionManager permissionProvider = null;

    private static void registerPermissionProvider() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            permissionProvider = new LuckPermsPermissionManager();
            NeoAPI.LOGGER.info("Found LuckPerms! Permission support enabled.");
        } catch(ClassNotFoundException e) {
            permissionProvider = new DefaultPermissionManager();
            NeoAPI.LOGGER.warn("Couldn't find LuckPerms.. falling back to permission levels.");
        }
    }

    public static boolean checkForPermission(ServerCommandSource source, String permission, int level) {
        if(source.getPlayer() != null) {
            return permissionProvider.checkForPermission(source.getPlayer(), permission, level);
        } else return true;
    }

    public static String checkMetaPermission(ServerCommandSource source, String metaKey) {
        if(source.getPlayer() != null) {
            return permissionProvider.getMetaPermission(source.getPlayer(), metaKey);
        } else return null;
    }

    public static boolean checkForPermission(@NotNull ServerPlayerEntity player, String permission, int level) {
        return permissionProvider.checkForPermission(player, permission, level);
    }

    public static String checkMetaPermission(@NotNull ServerPlayerEntity player, String metaKey) {
        return permissionProvider.getMetaPermission(player, metaKey);
    }
}