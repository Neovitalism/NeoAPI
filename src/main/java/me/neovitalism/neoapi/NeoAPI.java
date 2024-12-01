package me.neovitalism.neoapi;

import me.neovitalism.neoapi.async.NeoAPIExecutorManager;
import me.neovitalism.neoapi.modloading.logging.NeoModLogger;
import me.neovitalism.neoapi.permissions.DefaultPermissionProvider;
import me.neovitalism.neoapi.permissions.LuckPermsPermissionProvider;
import me.neovitalism.neoapi.permissions.PermissionProvider;
import me.neovitalism.neoapi.player.PlayerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;

public class NeoAPI implements ModInitializer {
    public static final NeoModLogger LOGGER = new NeoModLogger("NeoAPI");
    private static NeoAPI instance = null;
    private MinecraftServer server = null;
    private PermissionProvider permissionProvider = null;
    private FabricServerAudiences adventure = null;

    @Override
    public void onInitialize() {
        NeoAPI.instance = this;
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.adventure = FabricServerAudiences.of(server);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.registerPermissionProvider());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.server = null;
            this.permissionProvider = null;
            this.adventure = null;
            NeoAPIExecutorManager.shutdown();
        });
        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) ->
                PlayerManager.addTag(handler.getPlayer(), "neoapi.joinedBefore")));
    }

    public static MinecraftServer getServer() {
        return NeoAPI.instance.server;
    }

    public static PermissionProvider getPermissionProvider() {
        return NeoAPI.instance.permissionProvider;
    }

    public static FabricServerAudiences adventure() {
        return NeoAPI.instance.adventure;
    }

    private void registerPermissionProvider() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            this.permissionProvider = new LuckPermsPermissionProvider();
            NeoAPI.LOGGER.info("Found LuckPerms! Permission support enabled.");
            return;
        } catch (ClassNotFoundException ignored) {}
        this.permissionProvider = new DefaultPermissionProvider();
        NeoAPI.LOGGER.warn("Couldn't find LuckPerms.. falling back to permission levels.");
    }
}
