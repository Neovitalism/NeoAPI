package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.permissions.NeoPermission;
import me.neovitalism.neoapi.utils.ColorUtil;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand extends CommandBase {
    private final NeoMod instance;
    protected NeoPermission reloadPermission;

    public ReloadCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher, String... aliases) {
        super(dispatcher, aliases);
        this.instance = instance;
        this.reloadPermission = NeoPermission.of(instance.getModID().toLowerCase(Locale.ENGLISH) + ".reload");
    }

    @Override
    public NeoPermission[] getBasePermissions() {
        return this.reloadPermission.toArray();
    }

    private NeoPermission getReloadPermission() {
        if (this.reloadPermission == null) return NeoPermission.EMPTY;
        return this.reloadPermission;
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.then(literal("reload")
                .requires(this.getReloadPermission()::matches)
                .executes(context -> {
                    instance.configManager();
                    context.getSource().sendMessage(ColorUtil.parseColour(
                            instance.getModPrefix() + "&aReloaded!"));
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
