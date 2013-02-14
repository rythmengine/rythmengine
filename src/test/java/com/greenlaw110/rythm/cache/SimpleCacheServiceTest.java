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
    private ICacheService cache = SimpleCacheService.INSTANCE;
    @Before
    public void setup() {
        cache.setDefaultTTL(3);
    }

    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void testPutGet() throws Exception {
        cache.put("key1", "val1", 2);
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        synchronized (lock) {
            lock.await(1900, TimeUnit.MILLISECONDS);
        }
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        synchronized (lock) {
            lock.await(1101, TimeUnit.MILLISECONDS);
        }
        assertTrue("timeout cached items should be removed", null == cache.get("key1"));
    }

    @Test
    public void testRemove() throws Exception {
        cache.put("key1", "val1", 10);
        synchronized (lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        assertTrue("removed cached item does not match", "val1".equals(cache.remove("key1")));
        assertTrue("removed cached item should not exists", null == cache.get("key1"));
    }

    @Test
    public void testRefreshTTL() throws Exception {
        cache.put("key1", "val1", 1);
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        synchronized (lock) {
            lock.await(900, TimeUnit.MILLISECONDS);
        }
        assertTrue("cached item does not match previous item", "val1".equals(cache.get("key1")));
        cache.put("key1", "val2", 2);
        synchronized (lock) {
            lock.await(1900, TimeUnit.MILLISECONDS);
        }
        assertTrue("cached items does not match", "val2".equals(cache.get("key1")));
        synchronized (lock) {
            lock.await(1101, TimeUnit.MILLISECONDS);
        }
        assertTrue("timeout cached items should be removed", null == cache.get("key1"));
    }

    public static void main(String[] args) {
        run(SimpleCacheServiceTest.class);
    }
}
