/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.cache;

import org.junit.Ignore;
import org.rythmengine.extension.ICacheService;

/**
 * Test 
 */
@Ignore
public class SimpleCacheServiceTest extends CacheServiceTestBase {
    @Override
    protected ICacheService cacheService() {
        return SimpleCacheService.INSTANCE;
    }
}
