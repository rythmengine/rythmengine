/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_COMPACT_ENABLED;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.utils.Escape;
import org.rythmengine.utils.IO;
import org.rythmengine.utils.JSONWrapper;
import org.rythmengine.utils.S;

/**
 * Test Github Issues
 */
public class GhIssueTest141_176 extends TestBase {
    boolean debug=false;
    
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
    
    @SuppressWarnings("unused")
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
    public void test164() {
        System.getProperties().put(CODEGEN_COMPACT_ENABLED.getKey(), "true");
        t = "@nocompact(){\nabc     ddd\n\n1}";
        s = r(t);
        eq("\nabc     ddd\n\n1");
        t = "@compact(){\nabc     ddd\n\n1}";
        s = r(t);
        eq("\nabc ddd\n1");
    }

    private boolean isProcessAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Test
    /**
     * https://github.com/rythmengine/rythmengine/issues/282
     * @throws Exception
     */
    public void test170() throws Exception {
        String cmdLine = new StringBuilder("java -classpath ")
                .append(System.getProperty("java.class.path"))
                .append(" org.rythmengine.issue.Gh170Helper").toString();
        ProcessBuilder pb = new ProcessBuilder(cmdLine.split("[\\s]+"));
        Process p = pb.start();
        Thread.sleep(4000);
        assertFalse(isProcessAlive(p));
        InputStream is = p.getInputStream();
        String s = IO.readContentAsString(is);
        assertContains(s, "Hello world");
        assertContains(s, "dev");
    }

    @Test
    public void test174() throws Exception {
        String cmdLine = new StringBuilder("java -classpath ")
                .append(System.getProperty("java.class.path"))
                .append(" org.rythmengine.issue.Gh174Helper").toString();
        ProcessBuilder pb = new ProcessBuilder(cmdLine.split("[\\s]+"));
        Process p = pb.start();
        Thread.sleep(5000);
        assertFalse(isProcessAlive(p));
        InputStream is = p.getInputStream();
        String s = IO.readContentAsString(is);
        assertContains(s, "Hello world");
        assertContains(s, "dev");
        assertContains(s, "Bye world");
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
}
