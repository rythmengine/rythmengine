package com.greenlaw110.rythm.util;

import com.greenlaw110.rythm.Rythm;

import java.io.File;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 22/01/12
 * Time: 12:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class RythmProperties extends Properties {

    public Integer getAsInt(String key, Integer defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Integer) return (Integer)o;
        String s = o.toString();
        int i = Integer.valueOf(s);
        put(key, i);
        return i;
    }

    public Long getAsLong(String key, Long defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Long) return (Long)o;
        String s = o.toString();
        long l = Long.valueOf(s);
        put(key, l);
        return l;
    }

    public Boolean getAsBoolean(String key, Boolean defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Boolean) return (Boolean)o;
        String s = o.toString();
        boolean b = Boolean.valueOf(s);
        put(key, b);
        return b;
    }

    public File getAsFile(String key, File defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof File) return (File)o;
        String s = o.toString();
        return new File(s);
    }
    
    public Rythm.Mode getAsMode(String key, Rythm.Mode defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Rythm.Mode) return (Rythm.Mode)o;
        String s = o.toString();
        Rythm.Mode mode = Rythm.Mode.valueOf(s);
        put(key, mode);
        return mode;
    }
}
