package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final  class StringUtil {
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("(^| )([a-z])");

    public static String replaceReplacements(String input, @Nullable Map<String, String> replacements) {
        if (input == null) return null;
        if (replacements != null) {
            for (Map.Entry<String, String> replacer : replacements.entrySet()) {
                input = input.replace(replacer.getKey(), replacer.getValue());
            }
        }
        input = n(input);
        input = s(input);
        return input;
    }

    public static String replaceFromConfig(Configuration config, String key, @Nullable Map<String, String> replacements) {
        return StringUtil.replaceReplacements(config.getString(key), replacements);
    }

    public static String n(String string) {
        return string.replaceAll("\\{n} (&#?[a-fA-F0-9]+)?([aeiouAEIOU])", "n $1$2")
                .replace("{n}", "");
    }

    public static String s(String string) {
        return string.replaceAll("([sS])\\{s}", "$1'").replace("{s}", "'s");
    }

    public static boolean startsWith(String arg, String completion) {
        if (arg.length() > completion.length()) return false;
        String argEquiv = completion.substring(0, arg.length());
        return arg.equalsIgnoreCase(argEquiv);
    }

    public static Object replaceObject(String input, Map<String, String> replacements) {
        String parsed = StringUtil.replaceReplacements(input, replacements);
        try {
            double doubleVal = Double.parseDouble(parsed);
            try {
                return Integer.parseInt(parsed);
            } catch (NumberFormatException e) {
                return doubleVal;
            }
        } catch (NumberFormatException e) {
            return parsed;
        }
    }

    public static String capitalizeWords(String input) {
        Matcher matcher = StringUtil.LOWERCASE_PATTERN.matcher(input);
        while (matcher.find()) {
            input = input.replaceFirst(matcher.group(), matcher.group().toUpperCase(Locale.ENGLISH));
            matcher = StringUtil.LOWERCASE_PATTERN.matcher(input);
        }
        return input;
    }
}
