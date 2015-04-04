package org.rythmengine.issue;

import org.rythmengine.RythmEngine;

import java.util.HashMap;
import java.util.Map;

public class Gh170Helper {
    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("engine.mode", "dev");
        final RythmEngine engine = new RythmEngine(config);
        System.out.println(engine.mode());
        System.out.println(engine.render("@args Object who\nHello @who", "world"));
    }
}
