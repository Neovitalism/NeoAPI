package me.neovitalism.neoapi.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.neovitalism.neoapi.NeoAPI;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandUtil {
    public static boolean executeCommand(ServerCommandSource source, String command) {
        try {
            NeoAPI.getServer().getCommandFunctionManager().getDispatcher().execute(command, source);
            return true;
        } catch (CommandSyntaxException ignored) {}
        return false;
    }

    public static boolean executeServerCommand(String command) {
        return CommandUtil.executeCommand(NeoAPI.getServer().getCommandSource(), command);
    }
}
