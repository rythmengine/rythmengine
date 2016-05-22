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
package org.rythmengine.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.rythmengine.extension.ICacheService;

import java.io.Serializable;

/**
 * implement cache service based on <a href="http://ehcache.org/">EHCACHE</a>
 */
public enum EhCacheService implements ICacheService {

    INSTANCE;

    CacheManager cacheManager;

    net.sf.ehcache.Cache cache;

    private static final String cacheName = "rythm";

    private int defaultTTL = 60;

    private EhCacheService() {
    }

    @Override
    public void put(String key, Serializable value, int ttl) {
        Element element = new Element(key, value);
        if (0 >= ttl) ttl = defaultTTL;
        element.setTimeToLive(ttl);
        cache.put(element);
    }

    @Override
    public void put(String key, Serializable value) {
        put(key, value, defaultTTL);
    }

    @Override
    public Serializable remove(String key) {
        Serializable o = get(key);
        if (null == o) {
            return null;
        }
        cache.remove(key);
        return o;
    }

    @Override
    public void evict(String key) {
        cache.remove(key);
    }


    @Override
    public Serializable get(String key) {
        Element e = cache.get(key);
        return null == e ? null : e.getValue();
    }

    @Override
    public boolean contains(String key) {
        Element e = cache.get(key);
        return null != e;
    }

    @Override
    public void clear() {
        cache.removeAll();
    }

    @Override
    public void setDefaultTTL(int ttl) {
        if (ttl == 0) throw new IllegalArgumentException("time to live value couldn't be zero");
        defaultTTL = ttl;
    }

    @Override
    public void shutdown() {
        clear();
        cacheManager.shutdown();
    }

    @Override
    public void startup() {
        this.cacheManager = CacheManager.create();
        this.cacheManager.addCache(cacheName);
        this.cache = cacheManager.getCache(cacheName);
    }
}
