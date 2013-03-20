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
package com.greenlaw110.rythm;

import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.TemplateBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.JUnitCore;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

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
    public void initRythm() {
        Rythm.shutdown();
        System.setProperty(HOME_TEMPLATE.getKey(), "root");
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "false");
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "false");
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_TRANSFORM_ENABLED.getKey(), "true");
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        System.setProperty(ENGINE_OUTPUT_JAVA_SOURCE_ENABLED.getKey(), "false");
        System.getProperties().put(RythmConfigurationKey.I18N_LOCALE.getKey(), new Locale("en", "AU"));
        System.setProperty("line.separator", "\n");
        System.getProperties().put(DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.RAW);
        t = null;
        s = null;
    }

    protected void assertNotContains(String found, String notExpected) {
        String msg = String.format("should not contains '%s', found: '%s'", notExpected, found);
        assertTrue(msg, !found.contains(notExpected));
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
        JUnitCore.main(cls.getName());
    }
    
}
