package com.greenlaw110.rythm.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class help to construct named value map
 */
public class NamedParams {
    public static class Pair {
        public String k;
        public Object v;
        public Pair(String key, Object val) {
            k = key;
            v = val;
        }
    }
    public static Pair p(String k, Object v) {return new Pair(k, v);}
    public static Map<String, Object> from(Pair... pairs) {
        Map<String, Object> map = new HashMap<String, Object>(pairs.length);
        for (Pair p: pairs) {
            map.put(p.k, p.v);
        }
        return map;
    }
}
