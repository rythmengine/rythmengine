package com.greenlaw110.rythm.extension;

import com.greenlaw110.rythm.utils.Time;

/**
 * A user application could implement this interface to provide
 * customized time string parsing utility to {@link com.greenlaw110.rythm.cache.ICacheService rythm cache service}.
 * and then configure the Rythm to use customized implementation via 
 * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#CACHE_DURATION_PARSER_IMPL "cache.duration_parser.impl"}
 * configuration.
 * <p>Usually user application does not need to provide it's own implementation, instead, the rythm built in time
 * parser could be used as default implementation</p>
 */
public interface IDurationParser {
    
    /**
     * Parse a string representation and return number of seconds  
     * 
     * @param s
     * @return duration in seconds
     */
    int parseDuration(String s);

    /**
     * Rythm's default implementation of {@link IDurationParser}. It allows the following type of duration string
     * representations:
     * <ul>
     * <li>1d: 1 day</li>
     * <li>3h: 3 hours</li>
     * <li>8mn or 8min: 8 minutes</li>
     * <li>23s: 23 seconds</li>
     * </ul>
     */
    public static final IDurationParser DEFAULT_PARSER = new IDurationParser() {
        @Override
        public int parseDuration(String s) {
            return Time.parseDuration(s);
        }
    };
}
