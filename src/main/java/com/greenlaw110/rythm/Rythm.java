package com.greenlaw110.rythm;

import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.template.ITemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rythm {

    public static enum Mode {
        dev, prod;
    }
    
    public static final RythmEngine engine = new RythmEngine(); static {
        init();
    }

    public static final String version = engine.version;


    public static void init() {
        engine.init();
    }

    public static String render(String template, Object... args) {
        return engine.render(template, args);
    }

    public static String render(File file, Object... args) {
        return engine.render(file, args);
    }

    public static void main2(String[] args) {
        String template = "@args java.util.List users; @each String u: users @u @ ";
        List<String> l = new ArrayList<String>();
        l.add("green");l.add("cherry");
        System.out.println(render(template, l));
    }

    public static void main(String[] args) {
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
    private static DialectManager dm_ = new DialectManager();
    public static DialectManager getDialectManager() {
        return dm_;
    }
    
    private static ExtensionManager em_ = new ExtensionManager();
    public static ExtensionManager getExtensionManager() {
        return em_;
    }
}
