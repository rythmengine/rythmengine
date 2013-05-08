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
package com.greenlaw110.rythm.extension;

import java.io.Serializable;

/**
 * Define cache service
 */
public interface ICacheService {
    /**
     * Store an item into the cache service by key and set ttl value
     *
     * @param key
     * @param value
     * @param ttl   time to live of the cached item. Time unit is second.
     *              If set to negative number, then it will never expire.
     *              If set to zero then the default ttl value will be used
     */
    void put(String key, Serializable value, int ttl);

    /**
     * Store an item into the cache by key and use default ttl
     *
     * @param key
     * @param value
     */
    void put(String key, Serializable value);

    /**
     * Remove an item from cache service by key
     *
     * @param key
     * @return the value associated with the key
     */
    Serializable remove(String key);

    /**
     * Return an item from the cache service by key
     *
     * @param key
     * @return the value associated with the key
     */
    Serializable get(String key);

    /**
     * Check if the cache contains key
     *
     * @param key
     * @return true if key is found in the cache
     */
    boolean contains(String key);

    /**
     * Remove all cached items
     */
    void clear();

    /**
     * Set default ttl value which will be used if user pass 0 as ttl or not specified ttl
     *
     * @param ttl
     */
    void setDefaultTTL(int ttl);

    /**
     * Shutdown the cache service
     */
    void shutdown();

    /**
     * Restart the cache service after shutdown
     * 
     * <p>Note, by default the cache service
     * should be started after initialized</p>
     */
    void startup();
}
