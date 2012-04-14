package com.greenlaw110.rythm;

import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.IRythmListener;

import java.io.File;
import java.util.*;

public class Rythm {

    public static enum Mode {
        dev, prod;
        public boolean isDev() {
            return dev == this;
        }
        public boolean isProd() {
            return prod == this;
        }
    }

    public static enum ReloadMethod {
        V_VERSION,
        RESTART
    }
    public static RythmEngine engine = new RythmEngine();

    public static final String version = engine.version;

    public static void init(Properties conf) {
        engine.init(conf);
    }

    public static void init() {
        engine.init();
    }

    public static void registerLoggerFactory(ILoggerFactory fact) {
        Logger.registerLoggerFactory(fact);
    }

    public static void registerListener(IRythmListener listener) {
        engine.registerListener(listener);
    }

    public static void unregisterListener(IRythmListener listener) {
        engine.unregisterListener(listener);
    }

    public static void clearListener() {
        engine.clearListener();
    }

    public static void registerTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        engine.registerTemplateClassEnhancer(enhancer);
    }

    public static void unregisterTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        engine.unregisterTemplateClassEnhancer(enhancer);
    }

    public static void clearTemplateClassEnhancer() {
        engine.clearTemplateClassEnhancer();
    }

    public static boolean registerTag(ITag tag) {
        return engine.registerTag(tag);
    }

    public boolean isProdMode() {
        return engine.isProdMode();
    }

    public static String render(String template, Object... args) {
        return engine.render(template, args);
    }

    public static String render(File file, Object... args) {
        return engine.render(file, args);
    }

    public static String renderStr(String template, Object... args) {
        return engine.renderString(template, args);
    }

    public static String renderString(String template, Object... args) {
        return engine.renderString(template, args);
    }

    public static String renderIfTemplateExists(String template, Object... args) {
        return engine.renderIfTemplateExists(template, args);
    }

    public static void main(String[] args) {
        String template = "@args java.util.List<String> users, String title, String name; @each String u: users @u @ title: @title name: @name ";
        List<String> l = new ArrayList<String>();
        l.add("green");l.add("cherry");
        ITemplate t = engine.getTemplate(template);
        t.setRenderArg("users", l);
        t.setRenderArg(2, "Green");
        t.setRenderArg(1, "Mr.");
        System.out.println(t.render());
    }

    public static void main2(String[] args) {
        String s = "@args java.util.Properties component; <input type='checkbox' @if (Boolean.valueOf(String.valueOf(component.get(\"checked\")))) checked @ >";
        Properties p = new Properties();
        p.put("checked", "true");
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("component", p);
        System.out.println(render(s, m));
    }

    public static void main1(String[] args) {
        String template = "@args String who1, String who2; Hello @who1 and @who2!";
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("who1", "uni");
        m.put("who2", "world");
        long start = System.currentTimeMillis();
        System.out.println(render(template, m));
        System.out.println(String.format("%s ms to render inline template at first time", System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        String r = "";
        for (int i = 0; i < 500000; ++i) {
            r = render(template, m);
        }
        System.out.println();
        System.out.println(r);
        System.out.println(String.format("%s ms to render for 500000 times", System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 500000; ++i) {
            r = String.format("Hello %s and %s", "uni", "world");
        }
        System.out.println();
        System.out.println(r);
        System.out.println(String.format("%s ms to string format for 500000 times", System.currentTimeMillis() - start));
    }

    // --- SPI interfaces ---
    public static DialectManager getDialectManager() {
        return engine.getDialectManager();
    }

    public static ExtensionManager getExtensionManager() {
        return engine.getExtensionManager();
    }
}
