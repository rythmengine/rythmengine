package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.Rythm;

import java.io.File;
import java.io.FileFilter;
import java.util.Properties;
import java.util.regex.Pattern;

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
    
    public Rythm.ReloadMethod getAsReloadMethod(String key, Rythm.ReloadMethod defMethod) {
        Object o = get(key);
        if (null == o) return defMethod;
        if (o instanceof Rythm.ReloadMethod) return (Rythm.ReloadMethod)o;
        String s = o.toString();
        Rythm.ReloadMethod method = Rythm.ReloadMethod.valueOf(s);
        put(key, method);
        return method;
    }

    public FileFilter getAsFileFilter(String key, FileFilter defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof FileFilter) return (FileFilter)o;
        String s = o.toString();
        final Pattern p = Pattern.compile(s);
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return p.matcher(pathname.getPath()).matches();
            }
        };
    }
    
    public Pattern getAsPattern(String key, Pattern defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Pattern) return (Pattern)o;
        String s = o.toString();
        Pattern p = Pattern.compile(s);
        put(key, p);
        return p;
    }

    public <T> T getAs(String key, T defVal, Class<T> tc) {
        Object o = get(key);
        if (null == o) return defVal;
        if (tc.isAssignableFrom(o.getClass())) {
            return (T)o;
        }
        return null;
    }
}
