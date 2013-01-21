package com.greenlaw110.rythm.sandbox;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;

import java.io.File;

/**
 * A wrapper of Rythm engine and make sure the rendering is happen in Sandbox mode
 */
public class Sandbox {
    RythmEngine engine;
    public Sandbox(RythmEngine engine) {
        this.engine = engine;
    }
    private RythmEngine engine() {
        if (null != engine) return engine;
        return Rythm.engine();
    }
    public String render(String template, Object... args) {
        RythmEngine eng = engine();
        eng.enterSandbox();
        try {
            return engine().render(template, args);
        } finally {
            eng.resetSandbox();
        }
    }

    public String render(File file, Object... args) {
        RythmEngine eng = engine();
        eng.enterSandbox();
        try {
            return engine().render(file, args);
        } finally {
            eng.resetSandbox();
        }
    }
    
}
