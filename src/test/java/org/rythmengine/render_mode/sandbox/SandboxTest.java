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
package org.rythmengine.render_mode.sandbox;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test sandbox feature
 */
public class SandboxTest extends TestBase {

    @Test(expected = RuntimeException.class)
    public void testSecurityViolation() {
        String t = "@{System.exit(1)}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RuntimeException.class)
    public void testSecurityViolation1() {
        String t = "@System.__getProperty(\"xxx\")";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RuntimeException.class)
    public void testSecurityViolation2() {
        String t = "@{System.__getProperty(\"xxx\")}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RuntimeException.class)
    public void testSecurityViolation3() {
        String t = "@{Runtime r = Runtime.getRuntime();r.exit(1);}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RuntimeException.class)
    public void testSecurityViolation4() {
        String t = "@{RythmEngine re = new RythmEngine();}";
        Rythm.sandbox().render(t);
    }

    @Test(expected = RuntimeException.class)
    public void testTimeout() {
        String t = "@if (true) {@for(;;){}}";
        Rythm.sandbox().render(t);
    }

    public static void main(String[] args) {
        run(SandboxTest.class);
    }
    
}
