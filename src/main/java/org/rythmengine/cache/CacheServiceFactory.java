package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;
import org.rythmengine.extension.ICacheServiceFactory;

public enum CacheServiceFactory implements ICacheServiceFactory {
    INSTANCE;
    @Override
    public ICacheService get() {
        // do not load ehcache service as certain
        // configuration of the ehcache might start up
        // non-daemon thread and thus block the rythm engine
        // from shutting down without explicitly calling
        // to RythmEngine.shutdown()
//        try {
//            Class.forName("net.sf.ehcache.Cache");
//            Class<ICacheServiceFactory> factoryClass = (Class<ICacheServiceFactory>)Class.forName("org.rythmengine.cache.EhCacheServiceFactory");
//            ICacheServiceFactory fact = factoryClass.newInstance();
//            return fact.get();
//        } catch (Exception e) {
//            // ignore
//        }
        return SimpleCacheService.INSTANCE;
    }
}
