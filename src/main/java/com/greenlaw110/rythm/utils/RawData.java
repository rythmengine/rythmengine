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
    
    public RawData append(Object val) {
        StringBuilder sb = new StringBuilder(data).append(val);
        return new RawData(sb);
    }

    public RawData appendTo(Object val) {
        StringBuilder sb = new StringBuilder(S.str(val)).append(data);
        return new RawData(sb);
    }

    @Override
    public String toString() {
        return data;
    }
    
    public static RawData valueOf(Object o) {
        return new RawData(o);
    }

    public static final RawData NULL = new RawData(null);
}
