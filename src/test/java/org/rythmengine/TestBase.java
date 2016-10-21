/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_COMPACT_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL;
import static org.rythmengine.conf.RythmConfigurationKey.ENGINE_OUTPUT_JAVA_SOURCE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_NATURAL_TEMPLATE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_SMART_ESCAPE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TRANSFORM_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * The Test base class
 */
public abstract class TestBase extends Assert {
    protected boolean debug=false;
    protected static ILogger logger = Logger.get(TestBase.class);
    
    protected String t; // the rythm template code
    protected String s;
    
    /**
     * run the given template with the given arguments
     * @param template
     * @param args
     * @return the resulting rendered string
     */
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
    
    /**
     * check that the current generated equals the given result
     * @param result - the result to compare to
     */
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
