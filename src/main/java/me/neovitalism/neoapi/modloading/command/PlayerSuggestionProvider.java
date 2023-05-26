package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.neovitalism.neoapi.modloading.NeoMod;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PlayerSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    private final NeoMod instance;
    private boolean permissionToInclude = false;
    private String exemptPermission = "";

    public PlayerSuggestionProvider(NeoMod instance) {
        this.instance = instance;
    }

    public PlayerSuggestionProvider(NeoMod instance, boolean permissionToInclude) {
        this.instance = instance;
        this.permissionToInclude = permissionToInclude;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        List<String> completions = new ArrayList<>();
        instance.getServer().getPlayerManager().getPlayerList().forEach(player -> {
            if(exemptPermission == null || !NeoMod.checkForPermission(player, exemptPermission)) {
                completions.add(player.getName().getString());
            }
        });
        if(permissionToInclude) completions.add("all");
        try {
            String arg = context.getArgument("player", String.class);
            if (arg != null) {
                for (String completion : completions) {
                    if (startsWith(arg, completion)) {
                        builder.suggest(completion);
                    }
                }
            }
        } catch(IllegalArgumentException e) {
            completions.forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private static boolean startsWith(String arg, String completion) {
        if(arg.length() > completion.length()) return false;
        String argEquiv = completion.substring(0, arg.length());
        return arg.equalsIgnoreCase(argEquiv);
    }

    public PlayerSuggestionProvider setExemptPermission(String exemptPermission) {
        this.exemptPermission = exemptPermission;
        return this;
    }
}
