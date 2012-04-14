package com.greenlaw110.rythm.utils;

/**
 * Parse duration
 */
public interface IDurationParser {
    /**
     * Parse duration
     * @param s
     * @return duration in seconds
     */
    int parseDuration(String s);

    public static final IDurationParser DEFAULT_PARSER = new IDurationParser() {
        @Override
        public int parseDuration(String s) {
            return Time.parseDuration(s);
        }
    };
}
