/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.template.ITemplate;

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

    @After
    public void cleanup() {
      System.getProperties().remove(CODEGEN_SOURCE_CODE_ENHANCER.getKey());
    }
    
    @Test
    public void test248() {
        setUpFor248();
        t = "@args String _4, String _0\n@_0, @_1, @_4, @_5, @_9";
        s = r(t, "_4", "_0");
        eq("_0, _1, _4, _5, _9");
    }
}
