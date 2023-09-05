package me.neovitalism.neoapi.world;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.modloading.config.YamlConfiguration;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WorldManager {
    private static final Map<UUID, ServerWorld> worldMap = new HashMap<>();
    private static final Map<UUID, String> worldNameMap = new HashMap<>();

    public static void mapWorlds(MinecraftServer server) {
        worldMap.clear();
        server.getWorlds().forEach(world -> {
            worldMap.put(WorldUUIDState.getOrCreateWorldUUID(world), world);
        });
        loadOrSetWorldNames();
    }

    @Nullable
    public static ServerWorld getWorldByUUID(UUID worldUUID) {
        return worldMap.get(worldUUID);
    }

    @NotNull
    public static UUID getWorldUUID(ServerWorld world) {
        return WorldUUIDState.getOrCreateWorldUUID(world);
    }

    @NotNull
    public static String getWorldName(ServerWorld world) {
        return worldNameMap.get(getWorldUUID(world));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void loadOrSetWorldNames() {
        Configuration config = null;
        File configFolder = FabricLoader.getInstance().getConfigDir().resolve("NeoAPI").toFile();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        File worldNameFile = new File(configFolder, "worldnames.yml");
        if(worldNameFile.exists()) {
            try {
                config = YamlConfiguration.loadConfiguration(worldNameFile);
            } catch (IOException ignored) {}
        }
        if(config == null) config = new Configuration();
        Configuration worldNameConfig = config.getSection("WorldNames");
        if(worldNameConfig == null) worldNameConfig = new Configuration();
        for(UUID worldUUID : worldMap.keySet()) {
            String worldUUIDString = String.valueOf(worldUUID);
            String worldName;
            if(worldNameConfig.contains(worldUUIDString)) {
                worldName = worldNameConfig.getString(worldUUIDString);
            } else {
                ServerWorld world = worldMap.get(worldUUID);
                worldName = ((ServerWorldProperties)world.getLevelProperties()).getLevelName() +
                        "-" + world.getRegistryKey().getValue();
                worldNameConfig.set(worldUUIDString, worldName);
            }
            worldNameMap.put(worldUUID, worldName);
        }
        config.set("WorldNames", worldNameConfig);
        try {
            YamlConfiguration.save(config, worldNameFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
