package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.permissions.NeoPermission;
import me.neovitalism.neoapi.utils.ColorUtil;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;

public class ReloadCommand extends CommandBase {
    private final NeoMod instance;

    public ReloadCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher, String... aliases) {
        super(dispatcher, NeoPermission.of(instance.getModID().toLowerCase(Locale.ENGLISH) + ".reload"), aliases);
        this.instance = instance;
    }

    @Override
    public NeoPermission[] getBasePermissions() {
        return this.storedPermission.toArray();
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.then(literal("reload")
                .requires(this.storedPermission::matches)
                .executes(context -> {
                    this.instance.configManager();
                    context.getSource().sendMessage(ColorUtil.parseColour(
                            this.instance.getModPrefix() + "&aReloaded!"));
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
