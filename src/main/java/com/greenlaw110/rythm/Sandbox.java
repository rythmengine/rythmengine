package com.greenlaw110.rythm;

import com.greenlaw110.rythm.sandbox.SandboxExecutingService;
import com.greenlaw110.rythm.template.ITemplate;

import java.io.File;

/**
 * A wrapper of Rythm engine and make sure the rendering is happen in Sandbox mode
 */
public class Sandbox {
    
    private static final InheritableThreadLocal<Boolean> sandboxMode = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    static boolean sandboxMode() {
        return sandboxMode.get();
    }


    RythmEngine engine;
    SandboxExecutingService secureExecutor = null;
    public Sandbox(RythmEngine engine, SandboxExecutingService executor) {
        this.engine = engine;
        this.secureExecutor = executor;
    }
    private RythmEngine engine() {
        if (null != engine) return engine;
        return Rythm.engine();
    }
    public String render(String template, Object... args) {
        RythmEngine eng = engine();
        eng.enterSandbox();
        try {
            ITemplate t = engine().getTemplate(template, args);
            return secureExecutor.execute(t);
        } finally {
            eng.leaveSandbox();
        }
    }

    public String render(File file, Object... args) {
        sandboxMode.set(true);
        try {
            ITemplate t = engine().getTemplate(file, args);
            return secureExecutor.execute(t);
        } finally {
            sandboxMode.set(false);
        }
    }
    
    public static String hasAccessToRestrictedClasses(RythmEngine engine, String code) {
        for (String s: engine.restrictedClasses) {
            if (code.contains(s)) return s;
        }
        return null;
    }
    
}
