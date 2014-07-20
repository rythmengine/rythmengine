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
import org.rythmengine.utils.Escape;
import org.rythmengine.utils.JSONWrapper;
import org.rythmengine.utils.S;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.*;

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
        s = S.join("::", new Integer[]{1,2,3});
        eq("1::2::3");
        s = S.join(":", new Double[]{1.0, 2.0, 3.0});
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
        t = "@args Date today = new Date(),Boolean b;@today.format(\"yyyy\"):@b";
        s = r(t);
        eq(S.format(new Date(), "yyyy") + ":false");
        
        t = "@args String x = \"x\";@x";
        s = r(t);
        eq("x");
    }
    
    @Test
    public void test146() {
        t = "@for(\"a\"){\n{true}\n}";
        s = r(t);
        eq("{true}\n");
        t = "@if(true){\n{true}\n}";
        s = r(t);
        eq("{true}");
        t = "@for(int i = 0; i < 1; ++i){\n{true}\n}";
        s = r(t);
        eq("{true}\n");
    }
    
    @Test
    public void test147() {
        t = "gh147/index.html";
        s = r(t);
        contains("pre-callback-in-tag2");
    }
    
    @Test
    public void test148() {
        t = "gh148/main.html";
        s = r(t,"a", "b");
        eq("2/b");

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("id", "x");
        args.put("sid", "b");
        s = r(t, args);
        eq("2/b");
    }
    
    @Test
    public void test155() {
        String x = "\uD83D\uDE30";
        assertEquals(x, S.escapeCSV(x).toString());
        assertEquals(x, Escape.CSV.apply(x).toString());
        System.getProperties().setProperty(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), "org.rythmengine.extension.ICodeType.DefImpl.CSV");
        t = "@s";
        s = r(t, x);
        eq(x);
    }
    
    private void yes(Object p) {
        assertEquals("yes", r(t, p, null));
    }
    
    private void no(Object p) {
        assertEquals("no", r(t, p, null));
    }
    
    @Test
    public void test157() {
        t = "@args List o;@if(o){yes}else{no}";
        no(null);
        t = "@args Integer o;@if(o){yes}else{no}";
        no(null);
        t = "@args Long o;@if(o){yes}else{no}";
        no(null);
        t = "@args Character o;@if(o){yes}else{no}";
        no(null);
        t = "@args Float o;@if(o){yes}else{no}";
        no(null);
        t = "@args Double o;@if(o){yes}else{no}";
        no(null);
        t = "@args Boolean o;@if(o){yes}else{no}";
        no(null);
    }
    
    @Test
    public void test176() {
        t = "@for (int i = 0; i < 10; ++i) {\n" +
                "    @continue(i % 5)\n" +
                "    @i\n" +
                "}";
        s = r(t);
        eq("0\n5\n");
    }

    @Test
    public void test185() {
        GH185Model model = new GH185Model("bar");
        t = "@args models.GH185Model model\n@if (model.foo@) {@model.foo@}";
        s = r(t, model);
        eq("bar");
    }

    @Test
    public void test188() {
        t = "gh188/foo.txt";
        s = r(t);
        eq("good");
    }

    @Test
    public void test193() {
        t = "@if(true) {a} else { \nb\n}";
        s = r(t);
        eq("a");
    }

    @Test
    @Ignore
    public void test194() {
        // This is known issue.
        // The workaround could be use ESCAPED WORD
        t = "@verbatim{\\}";
        s = r(t);
        eq("\\");
    }

    @Test
    @Ignore
    public void test201() {
        // Known issue
        t = "gh201/gh201.txt";
        StringWriter sw = new StringWriter();
        Rythm.engine().render(sw, t);
        s = sw.toString();
        contains("header");
        contains("inner");
        contains("footer");
    }

    @Test
    public void test202() {
        t = "@def echo(String s){@s}@echo(\"okay?\")";
        eq("okay?");
    }

    @Test
    public void test211() {
        // the test pass in case no exception thrown out
        t = "gh211/foo.txt";
        s = r(t);
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
        t = "@args models.GH227Model h\n@h.getSales().format(\"###,000,000.00\")";
        s = r(t, new GH227Model());
        eq("000,010.30");
    }

    @Test
    public void test227a() {
        t = "@s().format(10.3, \"###,000,000.00\")";
        s = r(t);
        eq("000,010.30");
    }
    
    public static void main(String[] args) {
        run(GHIssueTest.class);
    }
}
