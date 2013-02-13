package com.greenlaw110.rythm.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Evaluate an object and return boolean value by convention
 */
public class Eval {
    /**
     * return <code>true</code> if the specified byte <code>b != 0</code>
     * @param b
     * @return
     */
    public static boolean eval(byte b) {
        return b != 0;
    }

    /**
     * return <code>true</code> if the specified char <code>c != 0</code>
     * @param c
     * @return
     */
    public static boolean eval(char c) {
        return c != 0;
    }

    /**
     * return the specified boolean directly
     * @param b
     * @return
     */
    public static boolean eval(boolean b) {
        return b;
    }

    /**
     * return <code>true</code> if the specified integer <code>n != 0</code>
     * @param n
     * @return
     */
    public static boolean eval(int n) {
        return n != 0;
    }

    /**
     * return <code>true</code> if the specified long <code>l != 0</code>
     * @param l
     * @return
     */
    public static boolean eval(long l) {
        return l != 0L;
    }

    /**
     * return <code>true</code> if the specified float <code>Math.abs(f) > 0.00000001</code>
     * @param f
     * @return
     */
    public static boolean eval(float f) {
        return Math.abs(f) > 0.00000001;
    }

    /**
     * return <code>true</code> if the specified double <code>Math.abs(f) > 0.00000001</code>
     * @param d
     * @return
     */
    public static boolean eval(double d) {
        return Math.abs(d) > 0.00000001;
    }

    /**
     * Return true if the specified string does not equals, ignore case, to "false" or "no"
     */
    public static boolean eval(String s) {
        if (S.isEmpty(s)) return false;
        if ("false".equalsIgnoreCase(s)) return false;
        if ("no".equalsIgnoreCase(s)) return false;
        return true;
    }

    /**
     * Return <code>true</code> if the collection is not empty
     * @param c
     * @return
     */
    public static boolean eval(Collection c) {
        return !c.isEmpty();
    }

    /**
     * Return <code>true</code> if the map is not empty
     * @param m
     * @return
     */
    public static boolean eval(Map m) {
        return !m.isEmpty();
    }

    /**
     * @see #eval(boolean) 
     * @param b
     * @return
     */
    public static boolean eval(Boolean b) {
        return b;
    }

    /**
     * @see #eval(char) 
     * @param c
     * @return
     */
    public static boolean eval(Character c) {
        return eval(c.charValue());
    }

    /**
     * @see #eval(float) 
     * @param f
     * @return
     */
    public static boolean eval(Float f) {
        return eval(f.floatValue());
    }

    /**
     * @see #eval(double) 
     * @param d
     * @return
     */
    public static boolean eval(Double d) {
        return eval(d.doubleValue());
    }

    /**
     * Evaluate the number's int value
     * @see #eval(int) 
     * @param n
     * @return
     */
    public static boolean eval(Number n) {
        return eval(n.intValue());
    }

    /**
     * General object type evaluation. 
     * <ul>
     * <li>return <code>false</code> if the object instance is <code>null</code></li>
     * <li>return <code>false</code> if the object instance is an empty {@link Collection}</li>
     * <li>if object is type of Character, Float, Double then use its primitive value to evaluate</li>
     * <li>if object is any other type of Number, then use it's {@link Number#intValue()} to evaluate</li>
     * </ul>
     * 
     * @param condition
     * @return
     */
    public static boolean eval(Object condition) {
        if (condition == null) {
            return false;
        } else if (condition.getClass().isArray()) {
            return ((Object[])condition).length > 0;
        }
        return true;
    }
}
