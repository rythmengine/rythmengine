/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_COMPACT_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER;
import static org.rythmengine.conf.RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL;
import static org.rythmengine.conf.RythmConfigurationKey.ENGINE_OUTPUT_JAVA_SOURCE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_NATURAL_TEMPLATE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_SMART_ESCAPE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TRANSFORM_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import models.GH227Model;
import models.SandboxModel;

import org.junit.Ignore;
import org.junit.Test;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.template.ITemplate;
import org.rythmengine.utils.S;

/**
 * Test Github Issues
 */
public class GhIssueTest211_249 extends TestBase {
    boolean debug=false;
    
    @Test
    public void test211() {
        // the test pass in case no exception thrown out
        t = "gh211/foo.txt";
        s = r(t);
        if (debug)
          System.out.println(s);
    }

    @Test
    public void test222() {
        t = "gh222/gh222.html";
        s = r(t);
        eq("AAA");
    }

    @Test
    public void test223() {
        t = "gh223/foo2.html";
        s = r(t);
        eq("bar2-in-root");

        t = "gh223/foo.html";
        s = r(t);
        eq("bar-in-gh223");
    }

    @Test
    @Ignore
    public void test224() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(RythmConfigurationKey.SANDBOX_TIMEOUT.getKey(), 10000);
        RythmEngine engine = new RythmEngine(conf);
        t = "@args models.SandboxModel model\n@model";
        s = engine.sandbox().render(t, new SandboxModel("10", engine));
        eq("Bar[10]");
    }

    @Test
    public void test226() {
        String s = "aaa\u0000bbb";
        String s0 = S.escapeJSON(s).toString();
        assertTrue(s0.contains("u0000"));
    }

    @Test
    public void test227() {
        String format="###,000,000.00";
        GH227Model model = new GH227Model();
        float h = model.getSales();
        NumberFormat nf=new DecimalFormat(format);
        String expected=nf.format(h);
        t = "@args models.GH227Model h\n@h.getSales().format(\""+format+"\")";
        s = r(t, model);
        // debug=true;
        if (debug)
          System.out.println(expected);
        // US locale: "000,010.30"
        eq(expected);
    }

    @Test
    public void test227a() {
        String format="###,000,000.00";
        double number=27.35;
        t = "@s().format("+number+", \""+format+"\")";
        s = r(t);
        NumberFormat nf=new DecimalFormat(format);
        String expected=nf.format(number);
        // debug=true;
        if (debug)
          System.out.println(expected);
        // US locale: "000.027,35"
        eq(expected);
    }

    @Test
    public void test235() {
        t = "gh(235)/main.html";
        s = r(t);
        eq("hello rythm");
    }

    @Test
    public void test236() {
        t = "@if(false){\nfalse\n} else {\n{abc}\n}";
        s = r(t);
        eq("{abc}");
    }

    @Test
    public void test237() {
        t = "gh237/tmpl1.html";
        s = r(t);
        assertTrue(s.contains("tmpl1"));

        t = "gh237/tmpl2.html";
        s = r(t);
        assertTrue(s.contains("tmpl2"));
    }

    @Test
    public void test249() {
        t = "@def java.lang.Object foo(){return null}";
        r(t);
    }

    private void setUpFor244() {
        Rythm.shutdown();
        Properties prop = System.getProperties();
        prop.put(HOME_TEMPLATE.getKey(), "root/gh244");
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
        t = null;
        s = null;
    }

    @Test
    public void test244() {
        setUpFor244();
        t = "x.txt";
        s = r(t, "foo", "bar");
        assertEquals("foo and bar", s);
    }
}
