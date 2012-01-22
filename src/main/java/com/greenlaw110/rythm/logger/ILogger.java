package com.greenlaw110.rythm.logger;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/01/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ILogger {
    
    public boolean isTraceEnabled();

    public void trace(String format, Object... args);

    public void trace(Throwable t, String format, Object... args);

    public boolean isDebugEnabled();

    public void debug(String format, Object... args);

    public void debug(Throwable t, String format, Object... args);

    public boolean isInfoEnabled();

    public void info(String format, Object... arg);

    public void info(Throwable t, String format, Object... args);

    public boolean isWarnEnabled();

    public void warn(String format, Object... arg);

    public void warn(Throwable t, String format, Object... args);

    public boolean isErrorEnabled();

    public void error(String format, Object... arg);

    public void error(Throwable t, String format, Object... args);
}
