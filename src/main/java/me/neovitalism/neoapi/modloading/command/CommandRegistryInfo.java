package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandRegistryInfo {
    private final CommandDispatcher<ServerCommandSource> dispatcher;
    private final CommandRegistryAccess registryAccess;
    private final CommandManager.RegistrationEnvironment environment;

    public CommandRegistryInfo(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        this.dispatcher = dispatcher;
        this.registryAccess = registryAccess;
        this.environment = environment;
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.dispatcher;
    }

    public CommandRegistryAccess getRegistryAccess() {
        return this.registryAccess;
    }

    public CommandManager.RegistrationEnvironment getEnvironment() {
        return this.environment;
    }
}
