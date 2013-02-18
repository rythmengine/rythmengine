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
package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.exception.CompileException;
import org.junit.Test;

/**
 * Test @import parser
 */
public class ImportParserTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@import com.greenlaw110.rythm.*, com.greenlaw110.rythm.conf.RythmConfigurationKey";
        getSource();
        assertContains(s, "import com.greenlaw110.rythm.*");
        assertContains(s, "import com.greenlaw110.rythm.conf.RythmConfigurationKey");
    }

    @Test
    public void testFullFormat() {
        t = "@import(){com.greenlaw110.rythm.*\ncom.greenlaw110.rythm.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import com.greenlaw110.rythm.*");
        assertContains(s, "import com.greenlaw110.rythm.conf.RythmConfigurationKey");

        t = "@import(){com.greenlaw110.rythm.*;\ncom.greenlaw110.rythm.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import com.greenlaw110.rythm.*");
        assertContains(s, "import com.greenlaw110.rythm.conf.RythmConfigurationKey");
    }

    @Test(expected = CompileException.class)
    public void testErrorImport() {
        t = "@import assdsfa";
        r(t);
    }

    @Test
    public void testStatic() {
        t = "@import static com.greenlaw110.rythm.Rythm.*";
        getSource();
        assertContains(s, "import static com.greenlaw110.rythm.Rythm.*;");
    }

    @Test(expected = CompileException.class)
    public void testStaticError() {
        t = "@import static com.greenlaw110.rythm.*";
        getSource();
        assertContains(s, "import static com.greenlaw110.rythm.Rythm.*;");
    }

    @Test
    public void testStaticFullFormat() {
        t = "@import(){static com.greenlaw110.rythm.Rythm.*\nstatic com.greenlaw110.rythm.conf.RythmConfigurationKey.*}";
        getSource();
        assertContains(s, "import static com.greenlaw110.rythm.Rythm.*;");
        assertContains(s, "import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@import java.lang.*\nxyz";
        s = r(t);
        eq("abc\nxyz");
        
        t = "abc\n@import(){java.lang.*}\nxyz";
        s = r(t);
        eq("abc\nxyz");
        
        t = "abc\n\t@import(){java.lang.*}\nxyz";
        s = r(t);
        eq("abc\nxyz");
    }

    public static void main(String[] args) {
        run(ImportParserTest.class);
    }

}
