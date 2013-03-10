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
package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.Rythm;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Utility class to generate ranges for iteration purpose
 */
public abstract class Range<TYPE extends Comparable<TYPE>> implements Iterable<TYPE> {
    private final TYPE minInclusive;
    private TYPE maxExclusive;

    public Range(final TYPE minInclusive, final TYPE maxExclusive) {
        if (minInclusive == null || maxExclusive == null) {
            throw new NullPointerException();
        }
        if (minInclusive.compareTo(maxExclusive) > 0) {
            throw new IllegalArgumentException("max is greater than min");
        }
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }
    
    public TYPE min() {
        return minInclusive;
    }
    
    public TYPE max() {
        return maxExclusive;
    }
    
    private void extendMax() {
        maxExclusive = next(maxExclusive);
    }

    protected abstract TYPE next(TYPE element);
    
    public abstract int size();

    @Override
    public String toString() {
        return Rythm.toString("[@_.min() .. @_.max())", this);
    }

    private static final Pattern P_NUM = Pattern.compile("([0-9]+)(\\s*\\.\\.\\s*|\\s+(to|till)\\s+)([0-9]+)");
    private static final Pattern P_CHR = Pattern.compile("'(\\w)'(\\s*\\.\\.\\s*|\\s+(to|till)\\s+)'(\\w)'");
    public static Range valueOf(String s) {
        boolean open = true;
        if (s.endsWith("]")) {
            open = false;
        }
        s = s.trim();
        s = S.strip(s, "[", ")");
        s = S.strip(s, "[", "]");
        java.util.regex.Matcher m = P_NUM.matcher(s);
        boolean isChar = false;
        if (!m.matches()) {
            m = P_CHR.matcher(s);
            isChar = true;
        }
        
        if (!m.matches()) {
            throw new IllegalArgumentException("unknown range expression: " + s);
        }
        
        Range r;
        if (isChar) {
            char min = m.group(1).charAt(0), max = m.group(4).charAt(0);
            r = F.R(min, max);
        } else {
            int min = Integer.valueOf(m.group(1)), max = Integer.valueOf(m.group(4));
            r = F.R(min, max);
        }
        String verb = m.group(3);
        if ("till".equals(verb)) {
            open = false;
        }
        if (!open) {
            r.extendMax();
        }
        
        return r;
    }

    @Override
    public Iterator<TYPE> iterator() {
        return new Iterator<TYPE>() {
            private TYPE cur = minInclusive;

            @Override
            public boolean hasNext() {
                return !cur.equals(maxExclusive);
            }

            @Override
            public TYPE next() {
                TYPE retVal = cur;
                cur = Range.this.next(cur);
                return retVal;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
