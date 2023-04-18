package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.utils.ChatUtil;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public abstract class ReloadCommandBase implements CommandBase {
    public ReloadCommandBase(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> reloadCommand = register(instance, dispatcher);
        registerAliases(dispatcher, reloadCommand,instance.getModID().toLowerCase(Locale.ENGLISH)+".reload", 4);
    }

    public LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        String command = instance.getModID().toLowerCase(Locale.ENGLISH);
        return dispatcher.register(literal(command)
                .requires(serverCommandSource ->
                        NeoMod.checkForPermission(serverCommandSource, command + ".reload", 4))
                .then(literal("reload")
                        .executes(context -> {
                            instance.configManager();
                            ChatUtil.sendPrettyMessage(context.getSource(), instance.getModPrefix(), "&aReloaded Config!");
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
