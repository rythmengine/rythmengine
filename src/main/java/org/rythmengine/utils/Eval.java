/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.rythmengine.utils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Evaluate an object and return boolean value by convention
 */
public class Eval {
    /**
     * return <code>true</code> if the specified byte <code>b != 0</code>
     * @param b
     * @return boolean result
     */
    public static boolean eval(byte b) {
        return b != 0;
    }

    /**
     * return <code>true</code> if the specified char <code>c != 0</code>
     * @param c
     * @return boolean result
     */
    public static boolean eval(char c) {
        return c != 0;
    }

    /**
     * return the specified boolean directly
     * @param b
     * @return boolean result
     */
    public static boolean eval(boolean b) {
        return b;
    }

    /**
     * return <code>true</code> if the specified integer <code>n != 0</code>
     * @param n
     * @return boolean result
     */
    public static boolean eval(int n) {
        return n != 0;
    }

    /**
     * return <code>true</code> if the specified long <code>l != 0</code>
     * @param l
     * @return boolean result
     */
    public static boolean eval(long l) {
        return l != 0L;
    }

    /**
     * return <code>true</code> if the specified float <code>Math.abs(f) > 0.00000001</code>
     * @param f
     * @return boolean result
     */
    public static boolean eval(float f) {
        return Math.abs(f) > 0.00000001;
    }

    /**
     * return <code>true</code> if the specified double <code>Math.abs(f) > 0.00000001</code>
     * @param d
     * @return boolean result
     */
    public static boolean eval(double d) {
        return Math.abs(d) > 0.00000001;
    }

    /**
     * Return true if the specified string does not equals, ignore case, to "false" or "no"
     * 
     * @param s
     * @return boolean result
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
     * @return boolean result
     */
    public static boolean eval(Collection c) {
        return !c.isEmpty();
    }
    
    /**
     * Return <code>true</code> if the map is not empty
     * @param m
     * @return boolean result
     */
    public static boolean eval(Map m) {
        return !m.isEmpty();
    }

    /**
     * @see #eval(boolean) 
     * @param b
     * @return boolean result
     */
    public static boolean eval(Boolean b) {
        return b;
    }

    /**
     * @see #eval(char) 
     * @param c
     * @return boolean result
     */
    public static boolean eval(Character c) {
        return eval(c.charValue());
    }

    /**
     * @see #eval(float) 
     * @param f
     * @return boolean result
     */
    public static boolean eval(Float f) {
        return eval(f.floatValue());
    }

    /**
     * @see #eval(double) 
     * @param d
     * @return boolean result
     */
    public static boolean eval(Double d) {
        return eval(d.doubleValue());
    }

    /**
     * Evaluate the number's int value
     * @see #eval(int) 
     * @param n
     * @return boolean result
     */
    public static boolean eval(Number n) {
        return eval(n.intValue());
    }

    /**
     * General object type evaluation. 
     * <ul>
     * <li>return <code>false</code> if the object instance is <code>null</code></li>
     * <li>return <code>false</code> if the object instance is an empty {@link java.util.Collection} or {@link java.util.Map}</li>
     * <li>if object is type of Character, Float, Double then use its primitive value to evaluate</li>
     * <li>if object is any other type of Number, then use it's {@link Number#intValue()} to evaluate</li>
     * </ul>
     * 
     * @param condition
     * @return boolean result
     */
    public static boolean eval(Object condition) {
        if (condition == null) {
            return false;
        } else if (condition.getClass().isArray()) {
            return ((Object[])condition).length > 0;
        } else if (condition instanceof String) {
            return eval((String) condition);
        } else if (condition instanceof Boolean) {
            return (Boolean)condition;
        } else if (condition instanceof Collection) {
            return eval((Collection) condition);
        } else if (condition instanceof Map) {
            return eval((Map) condition);
        } else if (condition instanceof Double) {
            return eval((Double) condition);
        } else if (condition instanceof Float) {
            return eval((Float) condition);
        } else if (condition instanceof Number) {
            return eval((Number) condition);
        }
        return true;
    }

    /**
     * Eval Locale from a Locale
     * @param locale
     * @return the specified locale directly
     */
    public static Locale locale(Locale locale) {
        if (null == locale) throw new NullPointerException();
        return locale;
    }

    /**
     * Eval locale from language string
     * @param language
     * @return new Locale constructed from the language
     */
    public static Locale locale(String language) {
        if (language.contains("_")) {
            String[] sa = language.split("_");
            if (sa.length > 2) {
                return new Locale(sa[0], sa[1], sa[2]);
            } else if (sa.length > 1) {
                return new Locale(sa[0], sa[1]);
            } else {
                return new Locale(sa[0]);
            }
        }
        return new Locale(language);
    }

    /**
     * Eval locale from language and region
     * @param language
     * @param region
     * @return new Locale constructed from the lauguage and region 
     */
    public static Locale locale(String language, String region) {
        return new Locale(language, region);
    }

    /**
     * Eval locale from language, region and variant
     * @param language
     * @param region
     * @param variant
     * @return the new Locale constructed
     */
    public static Locale locale(String language, String region, String variant) {
        return new Locale(language, region, variant);
    }
}
