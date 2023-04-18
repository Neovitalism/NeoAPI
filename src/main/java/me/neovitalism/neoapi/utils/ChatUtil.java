package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.lang.LangManager;
import me.neovitalism.neoapi.modloading.NeoMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public final class ChatUtil {
    public static void sendPrettyMessage(ServerCommandSource source, String message) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            source.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(ServerPlayerEntity player, String message) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            player.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(ServerCommandSource source, String prefix, String message) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            source.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(ServerPlayerEntity player, String prefix, String message) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            player.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerCommandSource source, String langKey) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            source.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerPlayerEntity player, String langKey) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            player.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerCommandSource source, boolean useModPrefix, String langKey) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String prefix = (useModPrefix) ? instance.getModPrefix() : langManager.getLangSafely("Prefix");
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            source.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerPlayerEntity player, boolean useModPrefix, String langKey) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String prefix = (useModPrefix) ? instance.getModPrefix() : langManager.getLangSafely("Prefix");
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            player.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(ServerCommandSource source, String message, Map<String, String> replacements) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            message = replaceReplacements(message, replacements);
            source.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(ServerPlayerEntity player, String message, Map<String, String> replacements) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            message = replaceReplacements(message, replacements);
            player.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(ServerCommandSource source, String prefix, String message, Map<String, String> replacements) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            message = replaceReplacements(message, replacements);
            source.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(ServerPlayerEntity player, String prefix, String message, Map<String, String> replacements) {
        if(message != null && !message.isEmpty() && !message.isBlank()) {
            message = replaceReplacements(message, replacements);
            player.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerCommandSource source, String langKey, Map<String, String> replacements) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            message = replaceReplacements(message, replacements);
            source.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerPlayerEntity player, String langKey, Map<String, String> replacements) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            message = replaceReplacements(message, replacements);
            player.sendMessage(ColorUtil.parseColour(message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerCommandSource source, boolean useModPrefix, String langKey, Map<String, String> replacements) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String prefix = (useModPrefix) ? instance.getModPrefix() : langManager.getLangSafely("Prefix");
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            message = replaceReplacements(message, replacements);
            source.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static void sendPrettyMessage(NeoMod instance, ServerPlayerEntity player, boolean useModPrefix, String langKey, Map<String, String> replacements) {
        LangManager langManager = instance.getLangManager();
        if(langManager == null) return;
        String prefix = (useModPrefix) ? instance.getModPrefix() : langManager.getLangSafely("Prefix");
        String message = langManager.getLangSafely(langKey);
        if(!message.isBlank() && !message.isEmpty()) {
            message = replaceReplacements(message, replacements);
            player.sendMessage(ColorUtil.parseColour(prefix + message));
        }
    }

    public static String replaceReplacements(String input, Map<String, String> replacements) {
        for(String key : replacements.keySet()) {
            input = input.replace(key, replacements.get(key));
        }
        return input;
    }
}
