package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.neovitalism.neoapi.permissions.NeoPermission;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public abstract class CommandBase {
    protected NeoPermission storedPermission;

    public CommandBase(CommandDispatcher<ServerCommandSource> dispatcher, String... aliases) {
        for (String alias : aliases) dispatcher.register(this.getCommand(alias));
    }

    protected CommandBase(CommandDispatcher<ServerCommandSource> dispatcher, NeoPermission storedPermission, String... aliases) {
        this.storedPermission = storedPermission;
        for (String alias : aliases) dispatcher.register(this.getCommand(alias));
    }

    public abstract NeoPermission[] getBasePermissions();

    public abstract LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command);

    private LiteralArgumentBuilder<ServerCommandSource> getCommand(String commandName) {
        return this.getCommand(literal(commandName)
                .requires(source -> {
                    NeoPermission[] basePermissions = this.getBasePermissions();
                    if (basePermissions.length == 0) return true;
                    for (NeoPermission permission : basePermissions) if (permission.matches(source)) return true;
                    return false;
                }));
    }

    protected LiteralArgumentBuilder<ServerCommandSource> literal(String arg) {
        return CommandManager.literal(arg);
    }

    protected <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String arg, ArgumentType<T> type) {
        return CommandManager.argument(arg, type);
    }
}
