/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import models.Foo;
import models.GH185Model;
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
import org.rythmengine.utils.Escape;
import org.rythmengine.utils.IO;
import org.rythmengine.utils.JSONWrapper;
import org.rythmengine.utils.S;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.rythmengine.conf.RythmConfigurationKey.*;

/**
 * Test Github Issues
 */
public class GhIssueTest70_140 extends TestBase {
    boolean debug=false;
    
    @Test
    public void test70() {
        String s = Rythm.getTemplate("Good @when, @title @name")
                .__setRenderArg("when", "afternoon")
                .__setRenderArg("title", "Mr")
                .__setRenderArg("name", "Rythm")
                .render();
        assertEquals("Good afternoon, Mr Rythm", s);
    } 

    @Test
    public void test116() {
        t = "PlayRythm Demo - @get(\"title\")";
        s = r(t);
        eq("PlayRythm Demo - ");
    }
    
    @Test
    public void test117() {
        System.getProperties().put("default.template_lang.impl", ICodeType.DefImpl.CSV);
        t = "@for(\"FirstName,LastName,Email\"){@__sep}";
        s = r(t);
        eq("FirstName,LastName,Email");
    }
    
    @Test
    public void test120() {
        t = "@def String x(boolean x) {if (x) {return \"x\";} else {return \"y\";}}@x(true)";
        s = r(t);
        eq("x");
    }
    
    @Test
    public void test122() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
        t = "@args String src;<script src='@src'></script><script src='@src'></script>";
        s = r(t, "/js/abc");
        eq("<script src='/js/abc'></script><script src='/js/abc'></script>");
        
        t = "@args models.Foo foo;<script src='@foo.bar()._x()'></script>";
        s = r(t, new Foo());
        eq("<script src=''></script>");
    }
    
    @Test
    public void test123() {
        t = "@args models.Foo foo;@foo.bar()._x()";
        s = r(t, new Foo());
        eq("");
    }

    @Test
    public void test132() {
        t = "@args String s;@s.length()";
        s = r(t, null);
        eq("0");
        
        s = r(t);
        eq("0");
    }

    @Test
    public void test137() {
        t = "@{String s = \"abc\"}\n@s @assign(foo){bar}@foo";
        s = r(t);
        eq("\nabc bar");
    }
    
    @Test
    public void test139() {
        t = "@args Object @1;@if (@1) \n{true} \nelse\n{false}";
        s = r(t, true);
        eq("true");
        s = r(t, "false");
        eq("false");
        
        t = "@for(int i = 0; i < 1; ++i).join()\n{\n@i\n}";
        s = r(t);
        eq("\n0\n");
        
        t = "@args List l;@for(l).join()\n{@_}";
        s = r(t, Arrays.asList("1,2".split(",")));
        eq("\n1,2");
    }
    
    @Test
    public void test140() {
        t = "@args Date d;@d.format(\"yyyy-MM-dd\"):@args Number n;@n.format()";
        s = r(t, null, null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String refValue = sdf.format(new Date(0)) + ":0";
        eq(refValue);
    }
}
