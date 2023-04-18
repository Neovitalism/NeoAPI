package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public interface CommandBase {
    String[] getCommandAliases();

    LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher);

    default void registerAliases(CommandDispatcher<ServerCommandSource> dispatcher, LiteralCommandNode<ServerCommandSource> command, String permission, int level) {
        for(String alias : getCommandAliases()) {
            dispatcher.register(literal(alias).requires(serverCommandSource ->
                    NeoMod.checkForPermission(serverCommandSource, permission, level)).redirect(command));
        }
    }
}
