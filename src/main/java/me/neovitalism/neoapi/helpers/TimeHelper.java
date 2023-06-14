package me.neovitalism.neoapi.helpers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeHelper {
    public static void addReplacements(Map<String, String> replacements, long secondsTotal) {
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
}
