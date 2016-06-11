/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.utils.RawData;
import org.rythmengine.utils.S;

/**
 * Used to help track loop state
 */
public class LoopUtil {
    private final Object obj;
    private final boolean isFirst;
    private final boolean isLast;

    public LoopUtil(boolean isFirst, boolean isLast) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = null;
    }
    
    public LoopUtil(boolean isFirst, boolean isLast, Object obj) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = obj;
    }

    public RawData sep(String sep) {
        return postSep(sep);
    }

    public RawData preSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.escape(obj);
        }
        return RawData.valueOf(result);
    }

    public RawData postSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.escape(obj);
        }
        return RawData.valueOf(result + (isLast ? "" : sep));
    }
}
