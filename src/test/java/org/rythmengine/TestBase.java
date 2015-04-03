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
package org.rythmengine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.internal.RealSystem;
import org.junit.runner.JUnitCore;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.template.TemplateBase;
import org.rythmengine.utils.Eval;

import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.rythmengine.conf.RythmConfigurationKey.*;

/**
 * The Test base class
 */
public abstract class TestBase extends Assert {
    protected static ILogger logger = Logger.get(TestBase.class);
    
    protected String t;
    protected String s;
    
    protected String r(String template, Object... args) {
        return Rythm.render(template, args);
    }

    @Before
    public void initRythm() throws Exception {
        Rythm.shutdown();
        Properties prop = System.getProperties();
        prop.put(HOME_TEMPLATE.getKey(), "root");
        prop.put(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "false");
        prop.put(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "false");
        prop.put(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
        prop.put(FEATURE_TRANSFORM_ENABLED.getKey(), "true");
        prop.put(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        prop.put(ENGINE_OUTPUT_JAVA_SOURCE_ENABLED.getKey(), "false");
        //prop.put(RythmConfigurationKey.I18N_LOCALE.getKey(), new Locale("en", "AU"));
        prop.put(RythmConfigurationKey.I18N_LOCALE.getKey(), Locale.getDefault());
        prop.put("line.separator", "\n");
        prop.put(DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.RAW);
        Class.forName("org.rythmengine.utils.Eval");
        t = null;
        s = null;
    }

    protected void assertNotContains(String found, String notExpected) {
        String msg = String.format("should not contains '%s', found: '%s'", notExpected, found);
        assertTrue(msg, !found.contains(notExpected));
    }

    protected void contains(String expected) {
        assertContains(s, expected);
    }

    protected void assertContains(String found, String expected) {
        String msg = String.format("should contains '%s', found: '%s'", expected, found);
        assertTrue(msg, found.contains(expected));
    }
    
    protected void assertMatches(String found, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(found);
        String msg = String.format("should match '%s', found: '%s'", regex, found);
        assertTrue(msg, m.matches());
    }
    
    protected void eq(String result) {
        if (null == s) {
            s = r(t);
        }
        assertEquals(result, s);
    }

    protected void eqs(String a, String b) {
        assertEquals(a, b);
    }

    protected void eqf(String path) {
        String s0 = Rythm.engine().resourceManager().get(path).asTemplateContent();
        eq(s0);
    }
    
    protected void getSource() {
        TemplateBase tb = (TemplateBase) Rythm.engine().getTemplate(t);
        TemplateClass tc = tb.__getTemplateClass(false);
        s = tc.javaSource;
    }
    
    protected static void run(Class<? extends TestBase> cls) {
        new JUnitCore().runMain(new RealSystem(), cls.getName());
    }
    
}
