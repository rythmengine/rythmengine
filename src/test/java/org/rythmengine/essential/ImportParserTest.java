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
import org.rythmengine.exception.CompileException;
import org.junit.Test;

/**
 * Test @import parser
 */
public class ImportParserTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@import org.rythmengine.*, org.rythmengine.conf.RythmConfigurationKey";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");
    }

    @Test
    public void testFullFormat() {
        t = "@import(){org.rythmengine.*\norg.rythmengine.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");

        t = "@import(){org.rythmengine.*;\norg.rythmengine.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");
    }

    @Test(expected = CompileException.class)
    public void testErrorImport() {
        t = "@import assdsfa";
        r(t);
    }

    @Test
    public void testStatic() {
        t = "@import static org.rythmengine.Rythm.*";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
    }

    @Test(expected = CompileException.class)
    public void testStaticError() {
        t = "@import static org.rythmengine.*";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
    }

    @Test
    public void testStaticFullFormat() {
        t = "@import(){static org.rythmengine.Rythm.*\nstatic org.rythmengine.conf.RythmConfigurationKey.*}";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
        assertContains(s, "import static org.rythmengine.conf.RythmConfigurationKey.*;");
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
