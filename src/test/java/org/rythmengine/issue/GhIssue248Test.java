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
public class GhIssue248Test extends TestBase {
    boolean debug=false;
    
    private void setUpFor248() {
        ISourceCodeEnhancer se = new ISourceCodeEnhancer() {
            @Override
            public List<String> imports() {
                return Collections.emptyList();
            }

            @Override
            public String sourceCode() {
                return "";
            }

            @Override
            public Map<String, ?> getRenderArgDescriptions() {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("_9", "String");
                m.put("_1", "String");
                m.put("_5", "String");
                return m;
            }

            @Override
            public void setRenderArgs(ITemplate template) {
                template.__setRenderArg("_9", "_9");
                template.__setRenderArg("_1", "_1");
                template.__setRenderArg("_5", "_5");
            }
        };
        System.getProperties().put(CODEGEN_SOURCE_CODE_ENHANCER.getKey(), se);
    }

    @Test
    public void test248() {
        setUpFor248();
        t = "@args String _4, String _0\n@_0, @_1, @_4, @_5, @_9";
        s = r(t, "_4", "_0");
        eq("_0, _1, _4, _5, _9");
    }
}
