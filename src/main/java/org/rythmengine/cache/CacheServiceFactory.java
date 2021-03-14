/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.cache;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
