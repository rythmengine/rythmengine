package com.greenlaw110.rythm.internal.dialect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greenlaw110.rythm.internal.parser.build_in.BuildInParserFactory;
import com.greenlaw110.rythm.internal.parser.build_in.BuildInSpecialParserFactory;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.IParserFactory;

public abstract class DialectBase implements IDialect {
    
    public DialectBase() {
        registerBuildInParsers();
    }
    
    private List<IParserFactory> specialParsers = new ArrayList<IParserFactory>();
    @Override
    public void registerSpecialParser(IParserFactory parser) {
        if (!specialParsers.contains(parser)) specialParsers.add(parser);
    }

    private final Map<String, BuildInParserFactory> buildIns = new HashMap<String,BuildInParserFactory>();
    private void registerBuildInParsers() {
        for (Class<?> c: buildInParserClasses()) {
            if (BuildInSpecialParserFactory.class.isAssignableFrom(c)) {
                @SuppressWarnings("unchecked")
                Class<? extends BuildInSpecialParserFactory> c0 = (Class<? extends BuildInSpecialParserFactory>)c;
                try {
                    Constructor<? extends BuildInSpecialParserFactory> ct = c0.getConstructor();
                    ct.setAccessible(true);
                    BuildInSpecialParserFactory f = ct.newInstance();
                    registerSpecialParser(f);
                } catch (Exception e) {
                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                    else throw new RuntimeException(e);
                }
            } else if (BuildInParserFactory.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                @SuppressWarnings("unchecked")
                Class<? extends BuildInParserFactory> c0 = (Class<? extends BuildInParserFactory>)c;
                try {
                    Constructor<? extends BuildInParserFactory> ct = c0.getConstructor();
                    ct.setAccessible(true);
                    BuildInParserFactory f = ct.newInstance();
                    buildIns.put(f.keyword().toString(), f);
                    for (String s: f.interests()) {
                        buildIns.put(s, f);
                    }
                } catch (Exception e) {
                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                    else throw new RuntimeException(e);
                }
            }
        }
    }
    public IParser createBuildInParser(String keyword, IContext context) {
        BuildInParserFactory f = buildIns.get(keyword);
        return null == f ? null : f.create(this, context);
    }
    
    
    /**
     * Return the primary caret marker, e.g. "#" in play-groovy, "@" in rythm and "`" in japid. To escape the 
     * marker repeat the marker twice, e.g. "@@", "##", "``"
     * 
     * @return the primary caret
     */
    public abstract String a();
    
    protected abstract Class<?>[] buildInParserClasses();
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof IDialect) {
            return getClass().equals(o.getClass());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return id().hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s Dialect", id());
    }
}
