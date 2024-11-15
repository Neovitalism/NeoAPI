package me.neovitalism.neoapi.helpers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeHelper {
    private static final String TIME_REGEX = "(([0-9]*)h)?(([0-9]*)m)?(([0-9]*)s)?";

    public static void addReplacements(Map<String, String> replacements, long milliseconds) {
        long secondsTotal = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long days = TimeUnit.SECONDS.toDays(secondsTotal);
        long hoursNoDays = TimeUnit.SECONDS.toHours(secondsTotal);
        long hours = TimeUnit.DAYS.toHours(days) - hoursNoDays;
        long minutes = TimeUnit.SECONDS.toMinutes(secondsTotal) - TimeUnit.HOURS.toMinutes(hoursNoDays);
        long seconds = secondsTotal - TimeUnit.HOURS.toSeconds(hoursNoDays) - TimeUnit.MINUTES.toSeconds(minutes);
        replacements.put("{days}", String.valueOf(days));
        replacements.put("{hours-no-days}", String.valueOf(hoursNoDays));
        replacements.put("{hours}", String.valueOf(hours));
        replacements.put("{minutes}", String.valueOf(minutes));
        replacements.put("{seconds}", String.valueOf(seconds));
    }

    public static long parseSeconds(String input) {
        try {
            long minutes = Long.parseLong(input);
            return minutes * 60;
        } catch(NumberFormatException ignored) {}
        String charactersLeft = input.replaceAll(TimeHelper.TIME_REGEX, "");
        if (!charactersLeft.isEmpty()) return -1;
        String hourString = input.replaceAll(TimeHelper.TIME_REGEX, "$2");
        String minuteString = input.replaceAll(TimeHelper.TIME_REGEX, "$4");
        String secondString = input.replaceAll(TimeHelper.TIME_REGEX, "$6");
        try {
            long hours = (hourString.isEmpty()) ? 0 : Long.parseLong(hourString);
            long minutes = (minuteString.isEmpty()) ? (hours * 60) : Long.parseLong(minuteString) + (hours*60);
            return (secondString.isEmpty()) ? (minutes * 60) : Long.parseLong(secondString) + (minutes*60);
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}
