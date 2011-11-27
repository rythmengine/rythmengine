package com.greenlaw110.rythm.util;

public class S {
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static final int IGNORECASE = 0x00001000;
    public static final int IGNORESPACE = 0x00002000;

    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    public static boolean isEqual(String s1, String s2, int modifier) {
        if (null == s1) {
            return s2 == null;
        }
        if (null == s2)
            return false;
        if ((modifier & IGNORESPACE) != 0) {
            s1 = s1.trim();
            s2 = s2.trim();
        }
        if ((modifier & IGNORECASE) != 0) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return s1.equals(s2);
        }
    }
}
