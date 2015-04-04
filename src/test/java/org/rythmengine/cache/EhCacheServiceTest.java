package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;

public class EhCacheServiceTest extends CacheServiceTestBase {
    @Override
    protected ICacheService cacheService() {
        return EhCacheService.INSTANCE;
    }
}
