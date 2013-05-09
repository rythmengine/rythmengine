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
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import static org.rythmengine.conf.RythmConfigurationKey.*;

import org.rythmengine.cache.SimpleCacheService;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Test @args parser
 */
public class CacheParserTest extends TestBase {

    @Before
    public void _config() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.setProperty(CACHE_ENABLED.getKey(), "true");
    }

    @Test
    public void testCache() throws InterruptedException {
        t = "@cache(\"1s\"){@1}";
        r(t, 1); // ensure the template is compiled
        Thread.sleep(1050); // allow timeout for the cache
        int n = new Random().nextInt(1000);
        s = r(t, n);
        Thread.sleep(500);
        s = r(t, n + 1);
        eq(String.valueOf(n));
        Thread.sleep(550);
        s = r(t, n + 1);
        eq(String.valueOf(n + 1));
    }
    
    @Test
    public void testLineBreaks() {
        t = "abc\n@cache(){123}\nxyz";
        //System.err.println(t);
        s = r(t);
        eq("abc\n123\nxyz");

        t = "abc\n@cache(){\n123\n}\nxyz";
        //System.err.println(t);
        s = r(t);
        eq("abc\n123\nxyz");

        t = "xyz@cache(){abc}123";
        s = r(t);
        eq("xyzabc123");

        t = "abc\n @cache(){123}\nxyz";
        //System.err.println(t);
        s = r(t);
        eq("abc\n 123\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@cache()@1@";
        s = r(t, 5);
        eq("5");
        
        s = r(t, 6);
        eq("5");
    }
    
    @Test
    public void testCacheDisabled() {
        System.setProperty(CACHE_ENABLED.getKey(), "false");
        t = "@cache(\"1s\"){@1}";
        int n = new Random().nextInt(1000);
        s = r(t, n);
        s = r(t, n + 1);
        eq(String.valueOf(n + 1));
    }

    public static void main(String[] args) {
        run(CacheParserTest.class);
    }

}
