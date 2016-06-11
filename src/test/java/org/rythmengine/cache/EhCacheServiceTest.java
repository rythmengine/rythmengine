/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.cache;

import org.rythmengine.extension.ICacheService;

public class EhCacheServiceTest extends CacheServiceTestBase {
    @Override
    protected ICacheService cacheService() {
        return EhCacheService.INSTANCE;
    }
}
