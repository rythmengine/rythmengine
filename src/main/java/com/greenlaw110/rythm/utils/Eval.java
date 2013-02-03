package com.greenlaw110.rythm.utils;

import java.util.Collection;

/**
 * Evaluate an object and return boolean value by convention
 */
public class Eval {
    public static boolean eval(boolean b) {
        return b;
    }
    public static boolean eval(int n) {
        return n != 0;
    }
    public static boolean eval(float f) {
        return Math.abs(f) > 0.00000001;
    }
    public static boolean eval(double d) {
        return Math.abs(d) > 0.00000001;
    }
    public static boolean eval(Object condition) {
        if (condition == null) {
            return false;
        }
        if (condition instanceof Boolean && !(Boolean) condition) {
            return false;
        }
        if (condition instanceof Collection && ((Collection) condition).isEmpty()) {
            return false;
        }
        if (condition instanceof String && condition.toString().trim().equals("")) {
            return false;
        }
        if (condition instanceof Number && ((Number) condition).intValue() == 0) {
            return false;
        }
        return true;
    }
}
