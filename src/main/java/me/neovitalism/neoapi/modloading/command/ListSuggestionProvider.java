package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ListSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    private final String argName;
    private final List<String> completions;

    public ListSuggestionProvider(String argName, List<String> completions) {
        this.argName = argName;
        this.completions = completions;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String arg = context.getArgument(argName, String.class);
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
}
