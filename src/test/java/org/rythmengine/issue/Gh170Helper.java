package org.rythmengine.issue;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.utils.IO;
import org.rythmengine.utils.S;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.rythmengine.conf.RythmConfigurationKey.*;

public class Gh170Helper {

    private static final String TMPL_NAME = "gh170helper.txt";
    private static File tmplHome;

    private Gh170Helper() {
    }

    private static String content() {
        return "@args String who\nHello @who";
    }

    private static void prepareTmplHome() throws Exception {
        tmplHome = new File(System.getProperty("java.io.tmpdir"), S.random(5));
        tmplHome.mkdirs();
        if (!tmplHome.exists()) {
            throw new IOException("Cannot make template home dir: " + tmplHome.getAbsolutePath());
        }
    }

    private static RythmEngine prepareEngine() throws Exception {
        prepareTmplHome();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ENGINE_MODE.getKey(), Rythm.Mode.dev);
        config.put(HOME_TEMPLATE.getKey(), tmplHome);
        config.put(CACHE_ENABLED.getKey(), true);
        return new RythmEngine(config);
    }

    private static File tmplFile() {
        return new File(tmplHome, TMPL_NAME);
    }

    private static void prepareTmpl() {
        IO.writeContent(content(), tmplFile());
    }

    public static void main(String[] args) throws Exception {
        final RythmEngine engine = prepareEngine();
        System.out.println(engine.mode());

        prepareTmpl();
        System.out.println(engine.render(TMPL_NAME, "world"));
    }
}
