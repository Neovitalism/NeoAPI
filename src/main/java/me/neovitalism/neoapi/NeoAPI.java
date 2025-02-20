package me.neovitalism.neoapi;

import me.neovitalism.neoapi.async.NeoAPIExecutorManager;
import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.permissions.DefaultPermissionProvider;
import me.neovitalism.neoapi.permissions.LuckPermsPermissionProvider;
import me.neovitalism.neoapi.permissions.PermissionProvider;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.UUIDCache;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Future;

public class NeoAPI extends NeoMod {
    private static final NeoExecutor SCHEDULER = NeoAPIExecutorManager.createScheduler("NeoAPI-Thread", 1);

    private static NeoAPI instance = null;
    private MinecraftServer server = null;
    private PermissionProvider permissionProvider = null;
    private FabricServerAudiences adventure = null;

    @Override
    public String getModID() {
        return "NeoAPI";
    }

    @Override
    public String getModPrefix() {
        return "";
    }

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
        UUIDCache.startup();
        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            PlayerManager.addTag(handler.getPlayer(), "neoapi.joinedBefore");
        }));
    }

    public static NeoAPI inst() {
        return NeoAPI.instance;
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
            this.getLogger().info("Found LuckPerms! Permission support enabled.");
            return;
        } catch (ClassNotFoundException ignored) {}
        this.permissionProvider = new DefaultPermissionProvider();
        this.getLogger().warn("Couldn't find LuckPerms.. falling back to permission levels.");
    }

    public Future<?> runTaskAsync(Runnable runnable) {
        return NeoAPI.SCHEDULER.runTaskAsync(runnable);
    }
}
