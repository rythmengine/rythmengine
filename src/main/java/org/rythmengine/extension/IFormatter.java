package org.rythmengine.extension;

import java.util.Locale;

/**
 * Created by luog on 2/01/14.
 */
public interface IFormatter {
    /**
     * Try to format the object. If the formatter does not recongize the object, then
     * {@code null} shall be returned immediately
     *
     * @param val the value object to be formatted
     * @param pattern the pattern to format the object
     * @param locale current locale
     * @param timezone current timezone
     * @return the formatted string from the value
     */
    String format(Object val, String pattern, Locale locale, String timezone);
}
