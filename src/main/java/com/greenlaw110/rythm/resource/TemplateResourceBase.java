package com.greenlaw110.rythm.resource;

import java.util.UUID;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TemplateResourceBase implements ITemplateResource {

    /**
     * When this field is set to null, it assumes using Rythm's singleton mode
     *
     * This field should be set to null if needs to serialize the template resource to some where, e.g. Cache
     */
    private RythmEngine engine;
    
    public TemplateResourceBase() {}
    
    public TemplateResourceBase(RythmEngine engine) {
        if (null == engine) return;
        this.engine = engine.isSingleton() ? null : engine;
    }
    
    protected RythmEngine engine() {
        return null == engine ? Rythm.engine : engine;
    }
    
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
    
    protected String cache;

    private long timestamp;
    
    protected long nextCheckPoint;

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (null == obj) return false;
        if (getClass().equals(obj.getClass())) {
            return ((TemplateResourceBase)obj).getKey().equals(getKey());
        }
        return false;
    }

    private long checkInterval() {
        if (engine().isProdMode()) return -1; // never check when running in product mode
        Long intv = userCheckInterval();
        return null == intv ? defCheckInterval() : intv;
    }

    protected abstract long defCheckInterval();

    protected Long userCheckInterval() {
        return engine().configuration.getAsLong(String.format("rythm.%s.interval", getClass().getSimpleName()), null);
    }

    protected abstract long lastModified();

    protected abstract String reload();

    @Override
    public String asTemplateContent() {
        if (null == cache) {
            cache = reload();
            timestamp = lastModified();
        }
        return cache;
    }

    @Override
    /**
     * Refresh the product is modified.
     *
     * @return true if the product has been modified
     */
    public final boolean refresh() {
        long checkInterval = checkInterval();
        if (checkInterval < 0) return false; // never check
        if (0 == checkInterval) {
            // always check
            checkModified();
            return isModified();
        }
        // should I check now ?
        long now = System.currentTimeMillis();
        if (nextCheckPoint < now) {
            checkModified();
        }
        nextCheckPoint = now + checkInterval;
        return isModified();
    }

    private void checkModified() {
        long modified = lastModified();
        if (timestamp < modified) {
            cache = null;
        }
    }

    private boolean isModified() {
        return null == cache;
    }

    @Override
    public String getSuggestedClassName() {
        return "C" + UUID.randomUUID().toString().replace('-', '_');
    }

    @Override
    public String tagName() {
        return null;
    }

    protected static final String path2CN(String path) {
        int colon = path.indexOf(":");
        if (++colon > 0) {
            path = path.substring(colon); // strip the driver letter from windows path and scheme from the URL
        }
        while (path.startsWith("/")) path = path.substring(1);
        while (path.startsWith("\\")) path = path.substring(1);
        // -- do not strip the file suffix. other wise a.html and a.js template will fetch a same template class
        //    in the end
        //int lastDotPos = path.lastIndexOf(".");
        //path = path.substring(0, lastDotPos);
        return path.replace('/', '_').replace('\\', '_').replace('.', '_');
    }
    
    public static void main(String[] args) {
        System.out.println(path2CN("http://abc.1.a/Black/jack/k.html"));
        System.out.println(path2CN("W:\\tmp\\a.b.a.html"));
    }
}
