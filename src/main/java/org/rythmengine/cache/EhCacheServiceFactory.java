package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;
import org.rythmengine.extension.ICacheServiceFactory;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 2/12/13
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
class EhCacheServiceFactory implements ICacheServiceFactory {
    @Override
    public ICacheService get() {
        return EhCacheService.INSTANCE;
    }
}
