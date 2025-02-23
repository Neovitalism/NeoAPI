package me.neovitalism.neoapi.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TimeUtil {
    private static final String TIME_REGEX = "(([0-9]+)-?h(ours?)?)?-?(([0-9]+)-?m(inutes?)?)?-?(([0-9]+)-?s(econds?)?)?";

    public static long parseSeconds(String input) {
        try {
            long minutes = Long.parseLong(input);
            return minutes * 60;
        } catch (NumberFormatException ignored) {}
        String charactersLeft = input.replaceAll(TimeUtil.TIME_REGEX, "");
        if (!charactersLeft.isEmpty()) return -1;
        String hourString = input.replaceAll(TimeUtil.TIME_REGEX, "$2");
        String minuteString = input.replaceAll(TimeUtil.TIME_REGEX, "$5");
        String secondString = input.replaceAll(TimeUtil.TIME_REGEX, "$8");
        try {
            long hours = (hourString.isEmpty()) ? 0 : Long.parseLong(hourString);
            long minutes = (minuteString.isEmpty()) ? (hours * 60) : Long.parseLong(minuteString) + (hours*60);
            return (secondString.isEmpty()) ? (minutes * 60) : Long.parseLong(secondString) + (minutes*60);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static long ticksToMS(long ticks) {
        return ticks * 50;
    }

    public static String getFormattedTime(long seconds) {
        StringBuilder timeString = new StringBuilder();
        boolean usedComma = false;
        long newSeconds = seconds;
        if (newSeconds >= 86400) {
            long days = newSeconds / 86400;
            timeString.append(days).append(" day");
            if (days > 1) timeString.append("s");
            newSeconds -= days * 86400;
            if (newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 3600 == 0) || (newSeconds < 3600 && newSeconds % 60 == 0) || (newSeconds < 60);
            if (oneLeft) timeString.append(" and ");
            else {
                timeString.append(", ");
                usedComma = true;
            }
        }
        if (newSeconds >= 3600) {
            long hours = newSeconds / 3600;
            timeString.append(hours).append(" hour");
            if (hours > 1) timeString.append("s");
            newSeconds -= hours*3600;
            if (newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 60 == 0) || (newSeconds < 60);
            if (oneLeft) {
                if (usedComma) timeString.append(",");
                timeString.append(" and ");
            } else {
                timeString.append(", ");
                usedComma = true;
            }
        }
        if (newSeconds >= 60) {
            long minutes = newSeconds / 60;
            timeString.append(minutes).append(" minute");
            if(minutes > 1) timeString.append("s");
            newSeconds -= minutes*60;
            if (newSeconds == 0) return timeString.toString();
            if (usedComma) timeString.append(",");
            timeString.append(" and ");
        }
        timeString.append(newSeconds).append(" second");
        if (newSeconds == 0 || newSeconds > 1) timeString.append("s");
        return timeString.toString();
    }

    public static String executedTimeToFormatted(long executedTime) {
        long between = System.currentTimeMillis() - executedTime;
        return TimeUtil.getFormattedTime(TimeUnit.MILLISECONDS.toSeconds(between));
    }

    public static String executeTimeToFormatted(long executeTime) {
        long between = executeTime - System.currentTimeMillis();
        return TimeUtil.getFormattedTime(TimeUnit.MILLISECONDS.toSeconds(between));
    }

    public static void addReplacements(long seconds, Map<String, String> replacements) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hoursNoDays = TimeUnit.SECONDS.toHours(seconds);
        long hours = TimeUnit.DAYS.toHours(days) - hoursNoDays;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hoursNoDays);
        long secondsLeft = seconds - TimeUnit.HOURS.toSeconds(hoursNoDays) - TimeUnit.MINUTES.toSeconds(minutes);
        replacements.put("{days}", String.valueOf(days));
        replacements.put("{hours-no-days}", String.valueOf(hoursNoDays));
        replacements.put("{hours}", String.valueOf(hours));
        replacements.put("{minutes}", String.valueOf(minutes));
        replacements.put("{seconds}", String.valueOf(secondsLeft));
    }
}
