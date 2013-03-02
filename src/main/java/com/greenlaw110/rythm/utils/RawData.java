package com.greenlaw110.rythm.utils;

import java.io.Serializable;

/**
 * Used for escaping
 */
public class RawData implements Serializable {
    public String data;

    public RawData(Object val) {
        if (val == null) {
            data = "";
        } else {
            data = val.toString();
        }
    }

    @Override
    public String toString() {
        return data;
    }

    public static final RawData NULL = new RawData(null);
}
