package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.cache.ICacheService;
import com.greenlaw110.rythm.cache.NoCacheService;
import com.greenlaw110.rythm.cache.SimpleCacheService;
import com.greenlaw110.rythm.extension.IDurationParser;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Extends {@link Properties java properties} with a set of handy APIs
 */
public class RythmProperties extends Properties {
    private ILogger logger = Logger.get(RythmProperties.class);

    /**
     * Return Integer type value by key. If not found then the
     * default value specified will be return
     *
     * @param key
     * @param defVal the default value
     * @return
     */
    public Integer getAsInt(String key, Integer defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Integer) return (Integer) o;
        String s = o.toString();
        int i = Integer.valueOf(s);
        put(key, i);
        return i;
    }

    /**
     * Return Long type value by key. If not found then
     * the default value specified will be return
     *
     * @param key
     * @param defVal the default value
     * @return
     */
    public Long getAsLong(String key, Long defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Long) return (Long) o;
        String s = o.toString();
        long l = Long.valueOf(s);
        put(key, l);
        return l;
    }

    /**
     * Return Boolean type value by key. If not found then
     * the default value specified will be return
     *
     * @param key
     * @param defVal the default value
     * @return
     */
    public Boolean getAsBoolean(String key, Boolean defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Boolean) return (Boolean) o;
        String s = o.toString();
        boolean b = Boolean.valueOf(s);
        put(key, b);
        return b;
    }

    /**
     * Return a {@link File java File} by key. If not found then
     * the default value specified will be return.
     * <p/>
     * <p>The value stored in the properties could be a File instance
     * or a {@link String string} of the file path. If it is the former
     * case then the stored file will be returned immediately. Otherwise
     * the method will will return an new File instance created with
     * the path</p>
     *
     * @param key
     * @param defVal the default value
     * @return
     */
    public File getAsFile(String key, File defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof File) return (File) o;
        String s = o.toString();
        return new File(s);
    }


    /**
     * Return a {@link Rythm.Mode} type value by key. If no found
     * then the default value specified will be return.
     * <p/>
     * <p>The value stored in the properties could be a <code>Rythm.Mode</code>
     * instance or a String represent the mode, e.g. "dev". If the <code>String</code>
     * typed value is found, then it will be converted to <code>Rythm.Mode</code> by
     * <code>Rythm.Mode.valueOf(String)</code> and return</p>
     *
     * @param key
     * @param defVal
     * @return
     */
    public Rythm.Mode getAsMode(String key, Rythm.Mode defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Rythm.Mode) return (Rythm.Mode) o;
        String s = o.toString();
        Rythm.Mode mode = Rythm.Mode.valueOf(s);
        put(key, mode);
        return mode;
    }

    /**
     * This method is deprecated
     *
     * @param key
     * @param defMethod
     * @return
     */
    @Deprecated
    public Rythm.ReloadMethod getAsReloadMethod(String key, Rythm.ReloadMethod defMethod) {
        Object o = get(key);
        if (null == o) return defMethod;
        if (o instanceof Rythm.ReloadMethod) return (Rythm.ReloadMethod) o;
        String s = o.toString();
        Rythm.ReloadMethod method = Rythm.ReloadMethod.valueOf(s);
        put(key, method);
        return method;
    }

    /**
     * Return a {@link Pattern java Pattern} instance by key. If not found
     * then the specified default value will be returned.
     * <p/>
     * <p>The value stored in the properties could be a <code>Pattern</code>
     * instance or a <code>String</code> value. If the String value is found
     * the <code>Pattern.compile(s)</code> will be called to return the
     * <code>Pattern</code> instance</p>
     *
     * @param key
     * @param defVal
     * @return
     */
    public Pattern getAsPattern(String key, Pattern defVal) {
        Object o = get(key);
        if (null == o) return defVal;
        if (o instanceof Pattern) return (Pattern) o;
        String s = o.toString();
        Pattern p = Pattern.compile(s);
        put(key, p);
        return p;
    }

    /**
     * Return a {@link ICacheService cache service} instance by key. If not
     * found then it will check if <code>cache.enabled</code> configuration
     * item. If the configuration is set to <code>true</code>, then the
     * {@link SimpleCacheService#INSTANCE} is returned, otherwise 
     * {@link NoCacheService#INSTANCE} is returned.
     * 
     * <p>The value in the properties could be either an <code>ICacheService</code>
     * instance or a <code>String</code> typed value as the class name. If the
     * String value is found, the method will call 
     * <code>(ICacheService) Class.forName(s).newInstance()</code> to return the
     * <code>ICacheService</code> instance from the class name</p>
     * 
     * @param key
     * @return
     */
    public ICacheService getAsCacheService(String key) {
        Object o = get(key);
        if (null == o) {
            Boolean b = getAsBoolean("cache.enabled", false);
            return b ? SimpleCacheService.INSTANCE : NoCacheService.INSTANCE;
        }
        if (o instanceof ICacheService) return (ICacheService) o;
        String s = o.toString();
        try {
            ICacheService cache = (ICacheService) Class.forName(s).newInstance();
            return cache;
        } catch (Exception e) {
            logger.warn("error creating cache service from configuration item: %s.  Default implementation will be used instead", s);
            return SimpleCacheService.INSTANCE;
        }
    }

    /**
     * Return a {@link com.greenlaw110.rythm.extension.IDurationParser duration parser} instance by key. 
     * If not found then {@link com.greenlaw110.rythm.extension.IDurationParser#DEFAULT_PARSER} will
     * be returned.
     * 
     * <p>The value stored in the properties could be either an instance
     * of <code>IDurationParser</code> or a <code>String</code> value
     * of the implementation class name. If the <code>String</code> value
     * is found then the method will call
     * <code>(IDurationParser) Class.forName(s).newInstance();</code>
     * to return the instance</p>
     * 
     * @param key
     * @return
     */
    public IDurationParser getAsDurationParser(String key) {
        Object o = get(key);
        if (null == o) return IDurationParser.DEFAULT_PARSER;
        if (o instanceof IDurationParser) return (IDurationParser) o;
        String s = o.toString();
        try {
            return (IDurationParser) Class.forName(s).newInstance();
        } catch (Exception e) {
            logger.warn("error creating duration parser from configuration item: %s. Default implementation will be used instead", s);
            return IDurationParser.DEFAULT_PARSER;
        }
    }

    /**
     * A generic get and cast method. A <code>Class&lt;T&gt;</code> is
     * passed in as the parameter in order to do type cast. If the 
     * stored value cannot be cast to the type &lt;T&gt;, then 
     * <code>null</code> is returned
     * 
     * @param key the key to find the value
     * @param defVal the default value to be returned if not found
     * @param tc the class of the returned result that matches the type
     * @param <T> the type of the return result
     * @return
     */
    public <T> T getAs(String key, T defVal, Class<T> tc) {
        Object o = get(key);
        if (null == o) return defVal;
        if (tc.isAssignableFrom(o.getClass())) {
            return (T) o;
        }
        return null;
    }
}
