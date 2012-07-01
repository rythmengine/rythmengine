package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParserFactory;

import java.util.*;

public class DialectManager {
    IDialect def = null;
    public DialectManager() {
        def = new Rythm();
    }
    static ThreadLocal<Stack<IDialect>> threadLocal = new ThreadLocal<Stack<IDialect>>(); static {
        Stack<IDialect> stack = new Stack<IDialect>();
        stack.push(new Rythm());
        threadLocal.set(stack);
    }
    static IDialect[] dialects = {
        new SimpleRythm(),
        new Rythm()
    };
    public IDialect get() {
        return threadLocal.get().peek();
    }
    public void push(IDialect dialect) {
        threadLocal.get().push(dialect);
    }
    public IDialect pop(){
        return threadLocal.get().pop();
    }
    public IDialect get(String id) {
        if (null == id || "rythm".equalsIgnoreCase(id)) return def;
        return null;
    }
    public void beginParse(IContext ctx) {
        String template = ctx.getRemain();
        for (IDialect d: dialects) {
            if (d.isMyTemplate(template)) {
                push(d);
                d.begin(ctx);
                break;
            }
        }
        IDialect d = get();
        List<IParserFactory> l = externalParsers.get(d);
        if (null != l) {
            for (IParserFactory pf: l) {
                d.registerParserFactory(pf);
            }
        }
    }

    public void endParse(IContext ctx) {
        IDialect d = pop();
        d.end(ctx);
    }

    static Map<String, IDialect> dialectIdMap = new HashMap<String, IDialect>(); static {
        for (IDialect dialect: dialects) {
            dialectIdMap.put(dialect.id(), dialect);
        }
    }

    private Map<IDialect, List<IParserFactory>> externalParsers = new HashMap<IDialect, List<IParserFactory>>() ;
    public void registerExternalParsers(String dialect, IParserFactory... factories) {
        if (null == dialect) dialect = "rythm";
        IDialect d = dialectIdMap.get(dialect);
        if (null == d) {
            throw new IllegalArgumentException("dialect not found: " + dialect);
        }
        externalParsers.put(d, Arrays.asList(factories));
    }
}
