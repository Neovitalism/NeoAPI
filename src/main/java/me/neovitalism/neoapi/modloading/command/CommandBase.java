package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public interface CommandBase {
    String[] getCommandAliases();

    LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher);

    default void registerAliases(CommandDispatcher<ServerCommandSource> dispatcher, LiteralCommandNode<ServerCommandSource> command) {
        for(String alias : getCommandAliases()) {
            LiteralArgumentBuilder<ServerCommandSource> builder = literal(alias)
                    .requires(command.getRequirement())
                    .executes(command.getCommand());
            for(CommandNode<ServerCommandSource> child : command.getChildren()) {
                builder.then(child);
            }
            dispatcher.register(builder);
        }
    }
}
