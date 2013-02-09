package com.greenlaw110.rythm.toString;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A TSNode (ToString Node) represents an object to be put into the toString stream
 */
public class TSNode {
    public Class<?> type = null;
    public Map<String, String> expressions = new HashMap<String, String>();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof TSNode) {
            return ((TSNode) obj).type.equals(type);
        }
        return false;
    }

    public static TSNode parseClass(Class<?> clazz) {
        Field f = null;
        Class c = f.getType();
        c.getComponentType();
        Type t = f.getGenericType();
        //t.
        return null;
    }
}
