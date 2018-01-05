/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.cache;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.extension.ICacheService;

/**
 * base test class for testing the SimpleCacheService
 *
 */
@Ignore
public abstract class CacheServiceTestBase extends TestBase {
    protected ICacheService cache = SimpleCacheService.INSTANCE;
    @Before
    public void setup() {
        cache.shutdown();
        cache.setDefaultTTL(3);
        cache.startup();
    }

    /**
     * a concrete implementation needs to provide the cacheService implementation 
     * @return - the implementation of the ICacheService
     */
    protected abstract ICacheService cacheService();

    // private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void testPutGet() throws Exception {
        cache.put("key1", "val1", 2);
        assertEquals("val1", (cache.get("key1")));
        Thread.sleep(1900);
        assertEquals("val1", (cache.get("key1")));
        Thread.sleep(1200);
        assertEquals(null, cache.get("key1"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRemove() throws Exception {
        cache.put("key1", "val1", 10);
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        assertTrue("removed cached item does not match", "val1".equals(cache.remove("key1")));
        assertTrue("removed cached item should not exists", null == cache.get("key1"));
    }

    @Test
    public void testRefreshTTL() throws Exception {
        cache.put("key1", "val1", 1);
        assertEquals("val1", cache.get("key1"));
        Thread.sleep(900);
        assertEquals("val1", cache.get("key1"));
        Thread.sleep(250);
        assertEquals(null, cache.get("key1"));
        logger.trace("*****************************************");
        cache.put("key1", "val2", 2);
        assertEquals("val2", cache.get("key1"));
        Thread.sleep(1900);
        assertEquals("val2", cache.get("key1"));
        Thread.sleep(200);
        logger.trace("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        assertEquals(null, cache.get("key1"));
    }
    
    @Test
    public void testExpireOrder() throws Exception {
        cache.put("k2", "v2", 2);
        cache.put("k3", "v3", 3);
        cache.put("k1", "v1", 1);
        Thread.sleep(1050);
        assertNull(cache.get("k1"));
        assertEquals("v2", cache.get("k2"));
        assertEquals("v3", cache.get("k3"));
        Thread.sleep(1000);
        assertNull(cache.get("k2"));
        assertEquals("v3", cache.get("k3"));
        Thread.sleep(1000);
        assertNull(cache.get("k3"));
    }

    public static void main(String[] args) {
        run(CacheServiceTestBase.class);
    }
}
