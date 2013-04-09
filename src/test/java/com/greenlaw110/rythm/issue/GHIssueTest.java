package com.greenlaw110.rythm.issue;

import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;
import models.Foo;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Test Github Issues
 */
public class GHIssueTest extends TestBase {
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
        eq("1970-01-01:0");
    }
    
    @Test
    public void test141() {
        t = "@args integration.T t;@t";
        s = r(t);
        eq("");
    }
    
    @Test
    public void test142() {
        t = "@locale(\"zh_CN\"){@i18n('template', \"planet\", 7, new Date())}";
        s = r(t);
        assertContains(s, "我们于");
        assertContains(s, DateFormat.getDateInstance(DateFormat.LONG, new Locale("zh", "CN")).format(new Date()));
    }
    
    @Test
    public void test143() {
        s = S.join(new Integer[]{1,2,3}, "::");
        eq("1::2::3");
        s = S.join(new Double[]{1.0,2.0,3.0}, ":");
        eq("1.0:2.0:3.0");
    }
    
    @Test
    public void test144() {
        t = "@args Integer[] itr;@itr[0]";
        s = r(t, JSONWrapper.wrap("{\"itr\": [\"1\"]}"));
        eq("1");
        
        t = "@args List<Integer> itr;@itr.get(0)";
        s = r(t, JSONWrapper.wrap("{\"itr\": [\"1\"]}"));
        eq("1");
    }
    
    @Test
    public void test145() {
        t = "@args Date today = new Date();@today.format(\"yyyy\")";
        s = r(t);
        eq(S.format(new Date(), "yyyy"));
        
        t = "@args String x = \"x\";@x";
        s = r(t);
        eq("x");
    }
    
    @Test
    public void test146() {
        t = "@if(true){\n{true}\n}";
        s = r(t);
        eq("{true}");
    }
    
    public static void main(String[] args) {
        run(GHIssueTest.class);
    }
}
