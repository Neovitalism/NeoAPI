package me.neovitalism.neoapi.modloading.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.StringUtil;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public final class SuggestionProviders {
    public static final class List implements SuggestionProvider<ServerCommandSource> {
        private final String argName;
        private final java.util.List<String> completions;

        public List(String argName, java.util.List<String> completions) {
            this.argName = argName;
            this.completions = completions;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            try {
                String arg = context.getArgument(this.argName, String.class);
                for (String completion : this.completions) {
                    if (StringUtil.startsWith(arg, completion)) builder.suggest(completion);
                }
            } catch (IllegalArgumentException e) {
                this.completions.forEach(builder::suggest);
            }
            return builder.buildFuture();
        }
    }

    public static final class Player implements SuggestionProvider<ServerCommandSource> {
        private final boolean canTargetAll;

        public Player() {
            this(false);
        }

        public Player(boolean canTargetAll) {
            this.canTargetAll = canTargetAll;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            java.util.List<String> completions = new ArrayList<>();
            PlayerManager.getOnlinePlayers().forEach(player -> completions.add(player.getName().getString()));
            if (this.canTargetAll) completions.add("all");
            try {
                String arg = context.getArgument("player", String.class);
                for (String completion : completions) {
                    if (StringUtil.startsWith(arg, completion)) builder.suggest(completion);
                }
            } catch (IllegalArgumentException e) {
                completions.forEach(builder::suggest);
            }
            return builder.buildFuture();
        }
    }
}
