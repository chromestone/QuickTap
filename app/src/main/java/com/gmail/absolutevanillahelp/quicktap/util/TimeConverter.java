package com.gmail.absolutevanillahelp.quicktap.util;

public final class TimeConverter {

    /**
     * Don't let anyone instantiate this class.
     */
    private TimeConverter() {}

    public static long toMillis(String minutes, String seconds) {

        return (Long.parseLong(minutes) * 60L + Long.parseLong(seconds)) * 1000L;
    }

    public static String toString(long milliseconds) {

        long seconds = milliseconds / 1000;
        return makeItLength4(("" + (seconds / 60)) + (seconds % 60));
    }

    private static String makeItLength4(String str) {

        if (str == null) {

            str = "";
        }

        if (str.length() >= 4) {

            return str;
        }

        return "0".repeat(4 - str.length()) + str;
    }
}
