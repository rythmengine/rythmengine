package com.greenlaw110.rythm.logger;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/01/12
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ILoggerFactory {
    ILogger getLogger(Class<?> clazz);
}
