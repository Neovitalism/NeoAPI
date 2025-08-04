package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.lang.LangManager;

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

    public static long parseSecondsFromKey(String input) {
        String[] split = input.split("-");
        if (split.length != 2) return -1;
        long time;
        try {
            time = Long.parseLong(split[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
        String timeUnit = split[1];
        if (timeUnit.replaceFirst("seconds?", "").isEmpty()) return time;
        if (timeUnit.replaceFirst("minutes?", "").isEmpty()) return time * 60;
        if (timeUnit.replaceFirst("hours?", "").isEmpty()) return time * 3600;
        return -1;
    }

    public static long ticksToMS(long ticks) {
        return ticks * 50;
    }

    public static String getFormattedTime(long seconds) {
        return TimeUtil.getFormattedTime(seconds, LangManager.EMPTY);
    }

    public static String getFormattedTime(long seconds, LangManager langManager) {
        if (langManager == null) langManager = LangManager.EMPTY;
        StringBuilder timeString = new StringBuilder();
        boolean usedComma = false;
        long newSeconds = seconds;
        if (newSeconds >= 86400) {
            long days = newSeconds / 86400;
            String dayLang;
            if (days == 1) dayLang = langManager.getOrDefault("timeunit-day", "day");
            else dayLang = langManager.getOrDefault("timeunit-days", "days");
            timeString.append(days).append(" ").append(dayLang);
            newSeconds -= days * 86400;
            if (newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 3600 == 0) || (newSeconds < 3600 && newSeconds % 60 == 0) || (newSeconds < 60);
            if (oneLeft) {
                timeString.append(" ").append(langManager.getOrDefault("timeunit-joiner", "and")).append(" ");
            } else {
                timeString.append(langManager.getOrDefault("timeunit-comma", ",")).append(" ");
                usedComma = true;
            }
        }
        if (newSeconds >= 3600) {
            long hours = newSeconds / 3600;
            String hourLang;
            if (hours == 1) hourLang = langManager.getOrDefault("timeunit-hour", "hour");
            else hourLang = langManager.getOrDefault("timeunit-hours", "hours");
            timeString.append(hours).append(" ").append(hourLang);
            newSeconds -= hours * 3600;
            if (newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 60 == 0) || (newSeconds < 60);
            if (oneLeft) {
                if (usedComma) timeString.append(langManager.getOrDefault("timeunit-comma", ","));
                timeString.append(" ").append(langManager.getOrDefault("timeunit-joiner", "and")).append(" ");
            } else {
                timeString.append(langManager.getOrDefault("timeunit-comma", ",")).append(" ");
                usedComma = true;
            }
        }
        if (newSeconds >= 60) {
            long minutes = newSeconds / 60;
            String minutesLang;
            if (minutes == 1) minutesLang = langManager.getOrDefault("timeunit-minute", "minute");
            else minutesLang = langManager.getOrDefault("timeunit-minutes", "minutes");
            timeString.append(minutes).append(" ").append(minutesLang);
            newSeconds -= minutes * 60;
            if (newSeconds == 0) return timeString.toString();
            if (usedComma) timeString.append(langManager.getOrDefault("timeunit-comma", ","));
            timeString.append(" ").append(langManager.getOrDefault("timeunit-joiner", "and")).append(" ");
        }
        String secondsLang;
        if (newSeconds < 0) secondsLang = langManager.getOrDefault("timeunit-second", "second");
        else secondsLang = langManager.getOrDefault("timeunit-seconds", "seconds");
        timeString.append(newSeconds).append(" ").append(secondsLang);
        return timeString.toString();
    }

    public static String executedTimeToFormatted(long executedTime) {
        return TimeUtil.executedTimeToFormatted(executedTime, LangManager.EMPTY);
    }

    public static String executedTimeToFormatted(long executedTime, LangManager langManager) {
        long between = System.currentTimeMillis() - executedTime;
        return TimeUtil.getFormattedTime(TimeUnit.MILLISECONDS.toSeconds(between), langManager);
    }

    public static String executeTimeToFormatted(long executeTime) {
        return TimeUtil.executeTimeToFormatted(executeTime, LangManager.EMPTY);
    }

    public static String executeTimeToFormatted(long executeTime, LangManager langManager) {
        long between = executeTime - System.currentTimeMillis();
        return TimeUtil.getFormattedTime(TimeUnit.MILLISECONDS.toSeconds(between), langManager);
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

    public static void addCleanReplacements(long seconds, Map<String, String> replacements) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hoursNoDays = TimeUnit.SECONDS.toHours(seconds);
        long hours = TimeUnit.DAYS.toHours(days) - hoursNoDays;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hoursNoDays);
        long secondsLeft = seconds - TimeUnit.HOURS.toSeconds(hoursNoDays) - TimeUnit.MINUTES.toSeconds(minutes);
        replacements.put("{days}", TimeUtil.cleanTime(days));
        replacements.put("{hours-no-days}", TimeUtil.cleanTime(hoursNoDays));
        replacements.put("{hours}", TimeUtil.cleanTime(hours));
        replacements.put("{minutes}", TimeUtil.cleanTime(minutes));
        replacements.put("{seconds}", TimeUtil.cleanTime(secondsLeft));
    }

    private static String cleanTime(long time) {
        return (time > 9) ? String.valueOf(time) : "0" + time;
    }
}
