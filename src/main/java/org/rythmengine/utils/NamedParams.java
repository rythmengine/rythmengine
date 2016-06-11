/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class help to construct named value map
 */
public class NamedParams {

    private NamedParams() {
    }

    public static final NamedParams instance = new NamedParams();

    public static class Pair {
        public String k;
        public Object v;

        public Pair(String key, Object val) {
            k = key;
            v = val;
        }
    }

    public static Pair pair(String k, Object v) {
        return new Pair(k, v);
    }

    public static Pair p(String k, Object v) {
        return new Pair(k, v);
    }

    public static Map<String, Object> from(Pair... pairs) {
        Map<String, Object> map = new HashMap<String, Object>(pairs.length);
        for (Pair p : pairs) {
            map.put(p.k, p.v);
        }
        return map;
    }

}
