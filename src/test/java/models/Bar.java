/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package models;

import org.rythmengine.RythmEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 14/03/13
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Bar {
    private String _ = null;
    public Bar() {}
    public Bar(String b) {_ = b;}
    public String _x() {
        return "";
    }
    public String toString() {
        return _;
    }
    public String _() {
        return _;
    }

    public static void main(String... args) throws Exception {
       Map<String, Object> config = new HashMap<String, Object>();
 
       config.put("engine.mode", "dev");
       config.put("home.template.dir", "p:/jfinal-rythm-demo/WebRoot/css");
 
       final RythmEngine engine = new RythmEngine(config);
 
       WL("Prod Mode:" + engine.isProdMode());
       WL(engine.render("my.html", "brian N"));
       //engine.shutdown();
    }
 
    public static void WL(String s) {
       System.out.println(s);
    }
}
