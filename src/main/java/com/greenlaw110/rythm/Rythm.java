package com.greenlaw110.rythm;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teb.Stock;

import com.greenlaw110.rythm.cache.HashMapCache;
import com.greenlaw110.rythm.cache.ICache;
import com.greenlaw110.rythm.internal.TemplateCompiler;
import com.greenlaw110.rythm.internal.TemplateCompiler.CompiledTemplate;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.IO;
import com.greenlaw110.rythm.util.S;

public class Rythm {
    private static ICache cache = new HashMapCache();
    
    private static TemplateCompiler compiler = new TemplateCompiler();
    
    private static File templateRoot = null;
    
    public static void setTemplateRoot(File root) {
        if (null != root) {
            if (root.isDirectory()) templateRoot = root;
            else throw new RuntimeException("template root not a director: " + root);
        }
    }
    
    public static File getTemplateRoot() {
        if (null == templateRoot) {
            String root = System.getProperty("rythm.templateRoot");
            if (null != root) {
                File f = new File(root);
                if (f.isDirectory()) {
                    templateRoot = f;
                } else {
                    // TODO log warning message
                }
            }
        }
        return templateRoot;
    }
    
    static ICache cache() {
        return cache;
    }
    
    public static CompiledTemplate compile(String template) {
        return compile(template, null, true);
    }
    
    public static CompiledTemplate compile(String template, String className) {
        return compile(template, className, true);
    }
    
    public static CompiledTemplate compile(String template, String className, boolean forceCompile) {
        // try first check if template is a file path
        File f = new File(template);
        if (f.canRead()) return compile(f, className, forceCompile);
        else return compiler.compile(template, className, forceCompile);
    }
    
    public static CompiledTemplate compile(File templateSource) {
        return compile(templateSource, null, true);
    }
    
    public static CompiledTemplate compile(File templateSource, String className) {
        return compile(templateSource, className, true);
    }
    
    public static CompiledTemplate compile(File templateSource, String className, boolean forceCompile) {
        String cacheKey = templateSource.getAbsolutePath();
        CompiledTemplate ct = cache.get(cacheKey);
        if (null == ct) {
            if (!templateSource.canRead()) throw new RuntimeException("Cannot read template source file: " + templateSource);
            String template = IO.readContentAsString(templateSource);
            ct = compile(template, className, forceCompile);
            cache.set(cacheKey, ct);
        }
        return ct;
    }
    
    private static ITemplate getTemplate_(File templateSource) {
        return compile(templateSource).template();
    }
    
    private static ITemplate getTemplate_(String template) {
        String cacheKey = template;
        CompiledTemplate ct = S.isNotEmpty(cacheKey) ? cache.get(cacheKey) : null;
        if (null == ct) {
            // see if template is a file name
            File root = getTemplateRoot();
            File f = null == root ? new File(template) : new File(root, template);
            if (f.canRead()) {
                return getTemplate_(f);
            }
            ct = compile(template);
            if (S.isNotEmpty(cacheKey)) cache.set(cacheKey, ct);
        }
        return ct.template();
    }

    public static ITemplate getTemplate(String template, Map<String, Object> args) {
        ITemplate t = getTemplate_(template);
        t.setRenderArgs(args);
        return t;
    }
    
    public static ITemplate getTemplate(String template, Object... args) {
        ITemplate t = getTemplate_(template);
        t.setRenderArgs(args);
        return t;
    }
    
    public static ITemplate getTemplate(File templateSource, Map<String, Object> args) {
        ITemplate t = getTemplate_(templateSource);
        t.setRenderArgs(args);
        return t;
    }
    
    public static ITemplate getTemplate(File templateSource, Object... args) {
        ITemplate t = getTemplate_(templateSource);
        t.setRenderArgs(args);
        return t;
    }
    
    public static String render(String template, Map<String, Object> args) {
        return render(template, args, null);
    }

    public static String render(String template, String cacheKey, Map<String, Object> args) {
        ITemplate t = getTemplate(template, cacheKey, args);
        return t.render();
    }
    
    public static String render(String template, Object... args) {
        return render(template, "", args);
    }
    
    public static String render(String template, String cacheKey, Object... args) {
        ITemplate t = getTemplate(template, cacheKey, args);
        return t.render();
    }
    
    public static String render(File templateSource, Map<String, Object> args) {
        if (!templateSource.canRead()) {
            throw new RuntimeException("Cannot read template source file: " + templateSource.getAbsolutePath());
        }
        ITemplate t = getTemplate(templateSource, args);
        return t.render();
    }
    
    public static String render(File templateSource, Object... args) {
        if (!templateSource.canRead()) {
            throw new RuntimeException("Cannot read template source file: " + templateSource.getAbsolutePath());
        }
        ITemplate t = getTemplate(templateSource, args);
        return t.render();
    }
    
    public static void main(String[] args) {
        test3();
    }
    
    public static void test0() {
        String template = "IO.readContentAgetTemplate Cannot read template source file: sSeadContentAgetTemplate Cannot read template source file: tring(new File@var String name;Hello @name\nString out = render(\"stocks.rythm.html\", Stock.dummyItems())";
        long l = System.currentTimeMillis();
        int hash = 0;
        for (int i = 0; i < 10000; ++i) {
            hash = template.hashCode();
        }
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(hash);
    }
    
    public static void test2() {
        Map<String, Object> params = new HashMap<String, Object>();
        
        File templateSource = new File("stocks.rythm.html");
        List<Stock> items = Stock.dummyItems();
        params.put("items", items);
        getTemplate(templateSource, items);
        System.out.println();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 5000; ++i) {
            render(templateSource, params);
            //t.setRenderArgs(items);
            //t.render();
        }
        System.out.println(System.currentTimeMillis() - l);
        //System.out.println(s);
    }
    
    public static void test1() {
        String template = "@args String name;Hello @name\nString out = render(\"stocks.rythm.html\", Stock.dummyItems())";
        String out = render(template, "Marco");
        //String out = render("stocks.rythm.html", Stock.dummyItems());
        //System.out.println(out);
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            out = render(template, "green");
        }
        System.out.println();
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(out);
    }
    
    public static void test3() {
        String template = IO.readContentAsString(new File("stocks.rythm.html"));
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1000; ++i) {
            compiler.compile(template, "stocks", false);
        }
        System.out.println(System.currentTimeMillis() - l);
    }
    
    // --- SPI interfaces ---
    private static DialectManager dm_ = new DialectManager();
    public static DialectManager getDialectManager() {
        return dm_;
    }
    
    private static ExtensionManager em_ = new ExtensionManager();
    public static ExtensionManager getExtensionManager() {
        return em_;
    }
}
