package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;
import org.rythmengine.extension.ICacheServiceFactory;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 2/12/13
 * Time: 8:46 AM
 * To change this template use File | Settings | File Templates.
 */
public enum CacheServiceFactory implements ICacheServiceFactory {
    INSTANCE;
    @Override
    public ICacheService get() {
        try {
            Class.forName("net.sf.ehcache.Cache");
            Class<ICacheServiceFactory> factoryClass = (Class<ICacheServiceFactory>)Class.forName("org.rythmengine.cache.EhCacheServiceFactory");
            ICacheServiceFactory fact = factoryClass.newInstance();
            return fact.get();
        } catch (Exception e) {
            // ignore
        }
        return SimpleCacheService.INSTANCE;
    }
}
