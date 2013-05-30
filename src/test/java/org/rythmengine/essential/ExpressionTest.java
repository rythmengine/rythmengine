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

import org.junit.Test;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.VariableResolverFactory;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_DYNAMIC_EXP;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static org.rythmengine.utils.NamedParams.from;
import static org.rythmengine.utils.NamedParams.p;

/**
 * Test Expression output.
 *
 * @see org.rythmengine.advanced.TransformerTest
 */
public class ExpressionTest extends TestBase {

    @Test
    public void testSimple() {
        t = "hello @who";
        s = r(t, from(p("who", "world")));
        eq("hello world");
    }

    public static class Data {
        public String foo;

        public String bar() {
            return foo + "bar";
        }

        public Data me() {
            return this;
        }

        public Data nullMe() {
            return null;
        }

        public Date now() {
            return new Date();
        }

        public Date nullNow() {
            return null;
        }

        public String nullFoo = null;

        @Override
        public String toString() {
            return bar();
        }
    }

    @Test
    public void testProperties() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@d.foo";
        s = r(t, from(p("d", d)));
        eq("bar");
    }
    
    public static class JavaBean {
        private String id;
        private int count;
        private boolean enabled;
        private Map<String, Object> attrs;
        public JavaBean(String id, int count, boolean enabled) {
            this.id  = id;
            this.count = count;
            this.enabled = enabled;
            attrs = new HashMap<String, Object>();
        }
        public String getId() {
            return id;
        }
        public int getCount() {
            return count;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void set(String key, Object val) {
            attrs.put(key, val);
        }
        public Object get(String key) {
            return attrs.get(key);
        }
    }
    
    @Test
    public void testDynamicExpr() {
        System.setProperty(FEATURE_DYNAMIC_EXP.getKey(), "true");
        JavaBean bean = new JavaBean("foo", 11, true);
        bean.set("engine", "Rythm");
        t = "@b.getId()|@b.count|@b.enabled|@b.engine";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("b", bean);
        PropertyHandlerFactory.registerPropertyHandler(JavaBean.class, new PropertyHandler() {
            @Override
            public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory) {
                JavaBean jb = (JavaBean)contextObj;
                if ("id".equals(name)) {
                    return jb.getId();
                } else if ("count".equals(name)) {
                    return jb.getCount();
                } else if ("enabled".equals(name)) {
                    return jb.isEnabled();
                } else {
                }
                return jb.get(name);
            }

            @Override
            public Object setProperty(String name, Object contextObj, VariableResolverFactory variableFactory, Object value) {
                ((JavaBean) contextObj).set("name", value);
                return null;
            }
        });
        s = r(t, params);
        eq("foo|11|true|Rythm");
    }

    @Test
    public void testMethod() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@d.bar()";
        s = r(t, from(p("d", d)));
        eq("barbar");
    }

    @Test
    public void testComplex() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@(d.bar() + d.foo)";
        s = r(t, from(p("d", d)));
        eq("barbarbar");
    }

    @Test
    public void testNullSafe() {
        Data d = null;
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testChainedNullSafe() {
        Data d = new Data();
        d.foo = "bar";
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("barbar");

        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.nullMe()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        d = null;
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testElvis() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        t = "@args String s\n@(s?:\"foo\")";
        s = r(t, from(p("s", null)));
        eq("foo");

        Data d = new Data();
        t = "@(d.nullMe()?:\"boo\")";
        s = r(t, from(p("d", d)));
        eq("boo");
        
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@(d.nullMe()?:new org.rythmengine.essential.ExpressionTest.Data())";
        s = r(t, from(p("d", d)));
        eq("nullbar");

        // this will fail, rythm cannot handle that complexity yet.
        // t = "@((d.nullMe() ?: new org.rythmengine.essential.ExpressionTest.Data()).now())";
        // s = r(t, from(p("d", d)));
        // eq("foo");

        // this will also fail, rythm cannot handle mix null safe and elvs:
//        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@(d?.nullMe()?:new org.rythmengine.essential.ExpressionTest.Data())";
//        s = r(t, from(p("d", d)));
//        eq("nullbar");
    }
    
//    @Test
//    public void testNullSafeExpressionInsideTagParams() {
//        t = "@args models.Bar bar;@bar?._()";
//        s = r(t, null);
//        eq("");
//
//        t = "@args models.Foo foo, models.Bar bar;@foo.bar(bar?._())";
//        s = r(t, from(p("foo", new Foo()), p("bar", new Bar("rab"))));
//        eq("rab");
//        
//        s = r(t, from(p("foo", new Foo()), p("bar", null)));
//        eq("");
//    }
    
    @Test
    public void testEscape() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
        t = "@1";
        s = r(t, "<h1>x</h1>");
        eq("&lt;h1&gt;x&lt;/h1&gt;");
    }
    
    public static void main(String[] args) {
        run(ExpressionTest.class);
    }

}
