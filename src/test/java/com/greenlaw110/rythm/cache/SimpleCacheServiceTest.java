/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.cache;

import com.greenlaw110.rythm.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleCacheServiceTest extends TestBase {
    private SimpleCacheService cache = SimpleCacheService.INSTANCE;
    @Before
    public void setup() {
        cache.shutdown();
        cache.setDefaultTTL(3);
        cache.debug = false;
        cache.startup();
    }

    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void testPutGet() throws Exception {
        cache.put("key1", "val1", 2);
        assertEquals("val1", (cache.get("key1")));
        Thread.sleep(1900);
        assertEquals("val1", (cache.get("key1")));
        Thread.sleep(1200);
        assertEquals(null, cache.get("key1"));
        cache.debug = false;
    }

    @Test
    public void testRemove() throws Exception {
        cache.put("key1", "val1", 10);
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        assertTrue("removed cached item does not match", "val1".equals(cache.remove("key1")));
        assertTrue("removed cached item should not exists", null == cache.get("key1"));
    }

    @Test
    public void testRefreshTTL() throws Exception {
        //System.err.println("============ testRefreshTTL ===========");
        //cache.debug = true;
        cache.put("key1", "val1", 1);
        assertEquals("val1", cache.get("key1"));
        Thread.sleep(900);
        assertEquals("val1", cache.get("key1"));
        Thread.sleep(250);
        assertEquals(null, cache.get("key1"));
        //System.err.println("*****************************************");
        cache.put("key1", "val2", 2);
        assertEquals("val2", cache.get("key1"));
        Thread.sleep(1900);
        assertEquals("val2", cache.get("key1"));
        Thread.sleep(150);
        //System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        assertEquals(null, cache.get("key1"));
    }
    
    @Test
    public void testExpireOrder() throws Exception {
        //System.err.println("============ testExpireOrder ===========");
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
        run(SimpleCacheServiceTest.class);
    }
}
