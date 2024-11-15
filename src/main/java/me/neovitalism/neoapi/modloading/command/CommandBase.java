package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.permissions.NeoPermission;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public abstract class CommandBase {
    public CommandBase(CommandDispatcher<ServerCommandSource> dispatcher, String... aliases) {
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
}
