/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 8/06/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaBean {
    private String id;
    private int count;
    private boolean enabled;
    private Date date;
    private Map<String, Object> attrs;
    public JavaBean(String id, int count, boolean enabled, Date date) {
        this.id  = id;
        this.count = count;
        this.enabled = enabled;
        this.date = date;
        attrs = new HashMap<String, Object>();
    }
    public String getId() {
        return id;
    }
    public int getCount() {
        return count;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public Date getDate() {
        return date;
    }
    public void set(String key, Object val) {
        attrs.put(key, val);
    }
    public Object get(String key) {
        return attrs.get(key);
    }
}
