package me.neovitalism.neoapi.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]){6}");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("[&§]([0-9a-fA-fk-oK-OrR])");

    public static Component parseColour(String input) {
        input = replaceCodes(input);
        return MiniMessage.miniMessage().deserialize(input);
    }

    private static String replaceCodes(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        while (matcher.find()) {
            input = input.replace(matcher.group(), "<reset><c:" + matcher.group().substring(1) + ">");
            matcher = HEX_PATTERN.matcher(input);
        }
        return replaceLegacyCodes(input);
    }

    private static String replaceLegacyCodes(String input) {
        Matcher matcher = LEGACY_PATTERN.matcher(input);
        while (matcher.find()) {
            input = input.replace(matcher.group(), getLegacyReplacement(matcher.group().substring(1)));
            matcher = LEGACY_PATTERN.matcher(input);
        }
        return input;
    }

    private static String getLegacyReplacement(String input) {
        return switch (input.toUpperCase(Locale.ENGLISH)) {
            case "0" -> "<reset><c:#000000>";
            case "1" -> "<reset><c:#0000AA>";
            case "2" -> "<reset><c:#00AA00>";
            case "3" -> "<reset><c:#00AAAA>";
            case "4" -> "<reset><c:#AA0000>";
            case "5" -> "<reset><c:#AA00AA>";
            case "6" -> "<reset><c:#FFAA00>";
            case "7" -> "<reset><c:#AAAAAA>";
            case "8" -> "<reset><c:#555555>";
            case "9" -> "<reset><c:#5555FF>";
            case "A" -> "<reset><c:#55FF55>";
            case "B" -> "<reset><c:#55FFFF>";
            case "C" -> "<reset><c:#FF5555>";
            case "D" -> "<reset><c:#FF55FF>";
            case "E" -> "<reset><c:#FFFF55>";
            case "F" -> "<reset><c:#FFFFFF>";
            case "K" -> "<obf>";
            case "L" -> "<b>";
            case "M" -> "<st>";
            case "N" -> "<u>";
            case "O" -> "<i>";
            case "R" -> "<reset>";
            default -> input;
        };
    }
}
