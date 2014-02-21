package org.rythmengine.extension;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by luog on 2/01/14.
 */
public class JodaDateTimeFormatter implements IFormatter {
    @Override
    public String format(Object val, String pattern, Locale locale, String timezone) {
        if (!(val instanceof DateTime)) return null;
        DateTimeFormatter fmt;
        if (null != pattern) fmt = DateTimeFormat.forPattern(pattern);
        else fmt = DateTimeFormat.fullDateTime();

        fmt = fmt.withLocale(locale);
        if (null != timezone) {
            DateTimeZone dtz = DateTimeZone.forID(timezone);
            fmt = fmt.withZone(dtz);
        }
        return fmt.print((DateTime)val);
    }
}
