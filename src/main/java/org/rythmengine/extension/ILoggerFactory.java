/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

import org.rythmengine.logger.ILogger;

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
