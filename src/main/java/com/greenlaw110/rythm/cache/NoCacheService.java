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

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 2/05/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoCacheService implements ICacheService {

    public static NoCacheService INSTANCE = new NoCacheService();

    private NoCacheService() {
    }

    @Override
    public void put(String key, Serializable value, int ttl) {
    }

    @Override
    public void put(String key, Serializable value) {
    }

    @Override
    public Serializable remove(String key) {
        return null;
    }

    @Override
    public Serializable get(String key) {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void clean() {
    }
    
    @Override
    public void clear() {
    }

    @Override
    public void setDefaultTTL(int ttl) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void startup() {
    }
}
