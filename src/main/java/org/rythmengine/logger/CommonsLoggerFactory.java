package org.rythmengine.logger;

import org.rythmengine.extension.ILoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 25/11/13
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommonsLoggerFactory implements ILoggerFactory {
    @Override
    public ILogger getLogger(Class<?> clazz) {
        return new CommonsLogger(clazz);
    }
}
